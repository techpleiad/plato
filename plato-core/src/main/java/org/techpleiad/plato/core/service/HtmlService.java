package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.techpleiad.plato.core.domain.BranchProfileReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.port.in.IHtmlServiceUseCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@Slf4j
public class HtmlService implements IHtmlServiceUseCase {

    private static final String TR = "</tr>";
    private static final String LEGENDBRANCH = "<br><table style=\" border: 1px solid black; border-collapse: collapse;\"><tr><th>Legend</th></tr><tr><td style=\"background-color: red;  border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Inconsistent with other branches</td></tr><tr><td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Consistent and good to go</td></tr><tr><td style=\"background-color: yellow; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Properties match but the formatting does not</td></tr></table>";
    private static final String LEGENDPROFILE = "<br><table style=\" border: 1px solid black; border-collapse: collapse;\"><tr><th>Legend</th></tr><tr><td style=\"background-color: red;  border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Inconsistent with other profiles</td></tr><tr><td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Consistent and good to go</td></tr></table>";

    @Override
    public String createBranchReportMailBody(final List<ConsistencyAcrossBranchesReport> reportList, final String fromBranch, final String toBranch) {
        String htmlDocument = "<h1>Branch Consistency Report<h1>";
        htmlDocument += "<h3>" + "Inconsistency in branches " + fromBranch + " and " + toBranch + " in configuration files" + "</h3> </br>";
        final Map<String, Map<String, BranchProfileReport>> reportMap = convertBranchReportToHashMap(reportList);
        htmlDocument += createBranchTable(reportMap);
        htmlDocument += LEGENDBRANCH;
        return htmlDocument;
    }

    private Map<String, Map<String, BranchProfileReport>> convertBranchReportToHashMap(final List<ConsistencyAcrossBranchesReport> reportList) {
        final Map<String, Map<String, BranchProfileReport>> reportMap = new HashMap<>();
        for (final ConsistencyAcrossBranchesReport consistencyAcrossBranchesReport : reportList) {
            final Map<String, BranchProfileReport> innerReport = new HashMap<>();
            for (final BranchProfileReport branchProfileReport : consistencyAcrossBranchesReport.getReport()) {
                innerReport.put(branchProfileReport.getProfile(), branchProfileReport);
            }
            reportMap.put(consistencyAcrossBranchesReport.getService(), innerReport);
        }
        return reportMap;
    }

    private String createBranchTable(final Map<String, Map<String, BranchProfileReport>> branchReport) {
        final Set<String> profileNames = new HashSet<>();

        for (final Map.Entry<String, Map<String, BranchProfileReport>> serviceName : branchReport.entrySet()) {
            for (final Map.Entry<String, BranchProfileReport> profileName : serviceName.getValue().entrySet()) {
                profileNames.add(profileName.getKey());
            }
        }
        final StringBuilder tableHead = new StringBuilder("<tr> <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"> Services </th>");
        for (final String profileName : profileNames) {
            tableHead.append("<th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">").append(profileName).append("</th>");
        }
        tableHead.append(TR);
        final StringBuilder rows = new StringBuilder();
        for (final Map.Entry<String, Map<String, BranchProfileReport>> serviceName : branchReport.entrySet()) {
            rows.append(createBranchTableRow(serviceName, profileNames));
        }
        return "<table style=\"width: 100%; border: 1px solid black; border-collapse: collapse;\">" + tableHead.toString() + rows.toString() + " </table>";
    }

    private String createBranchTableRow(final Map.Entry<String, Map<String, BranchProfileReport>> serviceName, final Set<String> profileNames) {
        final StringBuilder columns = new StringBuilder("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">" + serviceName
                .getKey() + "</td>");
        for (final String profile : profileNames) {
            columns.append(createBranchTableColumn(serviceName.getValue(), profile));
        }
        return "<tr>" + columns.toString() + TR;
    }

    private String createBranchTableColumn(final Map<String, BranchProfileReport> profileReport, final String profile) {
        if (profileReport.containsKey(profile)) {
            if (profileReport.get(profile).isFileEqual() && profileReport.get(profile).getPropertyValueEqual()) {
                return "<td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            } else if (!profileReport.get(profile).isFileEqual() && profileReport.get(profile).getPropertyValueEqual()) {
                return "<td style=\"background-color: yellow; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            } else {
                return "<td style=\"background-color: red; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            }
        } else {
            return "<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\" > N/A </td>";
        }
    }

    @Override
    public String createProfileReportMailBody(final List<ConsistencyAcrossProfilesReport> reportList, final String branchName) {
        String htmlDocument = "<h1>Profile Consistency Report<h1>";
        htmlDocument += "<h3>" + "Inconsistency in profiles in" + " configuration files in " + branchName + " branch" + "</h3> </br>";
        final Map<String, Map<String, List<String>>> reportMap = convertProfileReportToHashMap(reportList);
        htmlDocument += createProfileTable(reportMap);
        htmlDocument += LEGENDPROFILE;
        htmlDocument += createMissingPropertiesText(reportList);
        return htmlDocument;
    }

    private Map<String, Map<String, List<String>>> convertProfileReportToHashMap(final List<ConsistencyAcrossProfilesReport> reportList) {
        final Map<String, Map<String, List<String>>> reportMap = new HashMap<>();
        for (final ConsistencyAcrossProfilesReport consistencyAcrossProfilesReport : reportList) {
            reportMap.put(consistencyAcrossProfilesReport.getService(), consistencyAcrossProfilesReport.getMissingProperty());
        }
        return reportMap;
    }

    private String createProfileTable(final Map<String, Map<String, List<String>>> profileReport) {
        final Set<String> profileNames = new HashSet<>();
        for (final Map.Entry<String, Map<String, List<String>>> serviceName : profileReport.entrySet()) {
            for (final Map.Entry<String, List<String>> profileName : serviceName.getValue().entrySet()) {
                profileNames.add(profileName.getKey());
            }
        }

        final StringBuilder tableHead = new StringBuilder("<tr> <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"> Services </th>");
        for (final String profileName : profileNames) {
            tableHead.append("<th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">").append(profileName).append("</th>");
        }
        tableHead.append(TR);
        final StringBuilder rows = new StringBuilder();
        for (final Map.Entry<String, Map<String, List<String>>> serviceName : profileReport.entrySet()) {
            rows.append(createProfileTableRow(serviceName, profileNames));
        }
        return "<table style=\"width: 100%; border: 1px solid black; border-collapse: collapse;\">" + tableHead.toString() + rows.toString() + " </table>";
    }

    private String createProfileTableRow(final Map.Entry<String, Map<String, List<String>>> serviceName, final Set<String> profileNames) {
        final StringBuilder columns = new StringBuilder("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">" + serviceName
                .getKey() + "</td>");
        for (final String profile : profileNames) {
            columns.append(createProfileTableColumn(serviceName.getValue(), profile));
        }
        return "<tr>" + columns.toString() + TR;
    }

    private String createProfileTableColumn(final Map<String, List<String>> profileReport, final String profile) {
        if (profileReport.containsKey(profile)) {
            if (profileReport.get(profile).isEmpty()) {
                return "<td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            } else {
                return "<td style=\"background-color: red; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            }
        } else {
            return "<td> N/A </td>";
        }
    }

    private String createMissingPropertiesText(final List<ConsistencyAcrossProfilesReport> reportList) {
        final StringBuilder missingProperties = new StringBuilder("<h3> Missing Properties </h3>");
        for (final ConsistencyAcrossProfilesReport report : reportList) {

            if (noMissingProperties(report))
                continue;

            missingProperties.append("<p> <strong>").append(report.getService()).append("</strong> </p>");
            for (final Map.Entry<String, List<String>> missingProperty : report.getMissingProperty().entrySet()) {
                if (CollectionUtils.isEmpty(missingProperty.getValue()))
                    continue;

                missingProperties.append("<p> &nbsp; &nbsp; Profile: <strong>").append(missingProperty.getKey()).append(" </strong> </p>");
                for (final String property : missingProperty.getValue()) {
                    missingProperties.append("<p> &nbsp; &nbsp; &nbsp; &nbsp; ").append(property).append("</p>");
                }
            }
            missingProperties.append("<br>");
        }
        return missingProperties.toString();
    }

    private boolean noMissingProperties(final ConsistencyAcrossProfilesReport report) {
        if (CollectionUtils.isEmpty(report.getMissingProperty()))
            return true;

        return !report.getMissingProperty().values()
                .stream()
                .filter(e -> !CollectionUtils.isEmpty(e))
                .findAny()
                .isPresent();
    }
}
