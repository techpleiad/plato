package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.techpleiad.plato.core.domain.BranchProfileReport;
import org.techpleiad.plato.core.domain.BranchReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.ConsistencyLevelAcrossBranchesReport;
import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;
import org.techpleiad.plato.core.domain.CustomValidateReport;
import org.techpleiad.plato.core.port.in.IHtmlServiceUseCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Service
@Slf4j
public class HtmlService implements IHtmlServiceUseCase {

    private static final String TR = "</tr>";
    private static final String TH = "</th>";
    private static final String TD = "</td>";


    private static final String LEGENDBRANCH = "<br><table style=\" border: 1px solid black; border-collapse: collapse;\"><tr><th>Legend</th></tr><tr><td style=\"background-color: red;  border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Inconsistent with other branches</td></tr><tr><td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Consistent and good to go</td></tr><tr><td style=\"background-color: yellow; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Properties match but the formatting does not</td></tr></table>";
    private static final String LEGENDPROFILE = "<br><table style=\" border: 1px solid black; border-collapse: collapse;\"><tr><th>Legend</th></tr><tr><td style=\"background-color: red;  border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Inconsistent with other profiles</td></tr><tr><td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td><td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">Consistent and good to go</td></tr></table>";


    @Override
    public String createCustomValidationInBatchReportMailBody(List<CustomValidateInBatchReport> customValidateInBatchReports) {
        Map<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceToMap = convertCustomValidateBranchReportToMap(customValidateInBatchReports);
        String htmlDocument = "<h1>Custom Validation Report</h1>";
        htmlDocument += createCustomValidationTable(serviceToMap);
        htmlDocument += createCustomValidationText(serviceToMap);
        return htmlDocument;
    }

    public Map<String, Map<String, Map<String, List<CustomValidateReport>>>> convertCustomValidateBranchReportToMap(List<CustomValidateInBatchReport> customValidateInBatchReports) {
        Map<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceToMap = new TreeMap<>();
        for (CustomValidateInBatchReport customValidateInBatchReport : customValidateInBatchReports) {
            if (serviceToMap.containsKey(customValidateInBatchReport.getService())) {
                Map<String, Map<String, List<CustomValidateReport>>> branchToMap = serviceToMap.get(customValidateInBatchReport.getService());
                if (branchToMap.containsKey(customValidateInBatchReport.getBranch())) {
                    Map<String, List<CustomValidateReport>> profileToMap = branchToMap.get(customValidateInBatchReport.getBranch());
                    profileToMap.putIfAbsent(customValidateInBatchReport.getProfile(), customValidateInBatchReport.getCustomValidateReportList());
                } else {
                    Map<String, List<CustomValidateReport>> profileToMap = new TreeMap<>();
                    profileToMap.put(customValidateInBatchReport.getProfile(), customValidateInBatchReport.getCustomValidateReportList());
                    branchToMap.put(customValidateInBatchReport.getBranch(), profileToMap);
                }
            } else {
                Map<String, List<CustomValidateReport>> profileToMap = new TreeMap<>();
                profileToMap.put(customValidateInBatchReport.getProfile(), customValidateInBatchReport.getCustomValidateReportList());
                Map<String, Map<String, List<CustomValidateReport>>> branchToMap = new TreeMap<>();
                branchToMap.put(customValidateInBatchReport.getBranch(), profileToMap);
                serviceToMap.put(customValidateInBatchReport.getService(), branchToMap);
            }
        }
        return serviceToMap;
    }

    private String createCustomValidationTable(final Map<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceToMap) {
        final String tableHead = createCustomValidationTableHead(serviceToMap);
        final String tableRows = createCustomValidationTableRows(serviceToMap);
        return "<table style=\"border: 1px solid black; border-collapse: collapse;\">" + tableHead + tableRows + "</table>";

    }

    private String createCustomValidationTableHead(final Map<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceToMap) {
        final StringBuilder tableHead = new StringBuilder("<tr style=\"border: 1px solid black;border-collapse: collapse;\"><th style=\"border: 1px solid black;border-collapse: collapse;\" rowspan=\"2\">Service Name</th>");
        for (final Map.Entry<String, Map<String, List<CustomValidateReport>>> branchMap : serviceToMap.entrySet().iterator().next().getValue().entrySet()) {
            tableHead.append("<th  colspan=\" ").append(branchMap.getValue().size())
                    .append("\" style=\"text-align: center; border: 1px solid black; border-collapse: collapse;\">")
                    .append(branchMap.getKey()).append(TH);
        }
        tableHead.append("</tr> <tr style=\"border: 1px solid black; border-collapse: collapse;\">");
        for (final Map.Entry<String, Map<String, List<CustomValidateReport>>> branchMap : serviceToMap.entrySet().iterator().next().getValue().entrySet()) {
            for (final Map.Entry<String, List<CustomValidateReport>> profileMap : branchMap.getValue().entrySet()) {
                tableHead.append("<td style=\"border: 1px solid black; border-collapse: collapse;\">").append(profileMap.getKey()).append(TD);
            }
        }
        tableHead.append(TR);
        return tableHead.toString();
    }

    private String createCustomValidationTableRows(final Map<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceToMap) {
        final StringBuilder tableRows = new StringBuilder();
        for (final Map.Entry<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceMap : serviceToMap.entrySet()) {
            final StringBuilder tableRow = new StringBuilder("<tr style=\"border: 1px solid black;\"><td style=\"border: 1px solid black;border-collapse: collapse;\">" + serviceMap
                    .getKey()
                    + TD);
            for (final Map.Entry<String, Map<String, List<CustomValidateReport>>> branchMap : serviceMap.getValue().entrySet()) {
                for (final Map.Entry<String, List<CustomValidateReport>> profileMap : branchMap.getValue().entrySet()) {
                    if (profileMap.getValue().isEmpty()) {
                        tableRow.append("<td style=\"background-color: green; border: 1px solid black;border-collapse: collapse;\"></td>");
                    } else {
                        tableRow.append("<td style=\"background-color: red; border: 1px solid black;border-collapse: collapse;\"></td>");
                    }
                }
            }
            tableRow.append(TR);
            tableRows.append(tableRow.toString());
        }
        return tableRows.toString();
    }

    private String createCustomValidationText(final Map<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceToMap) {
        final StringBuilder htmlDocument = new StringBuilder();
        for (final Map.Entry<String, Map<String, Map<String, List<CustomValidateReport>>>> serviceMap : serviceToMap.entrySet()) {
            final StringBuilder serviceText = new StringBuilder("<h3>" + serviceMap.getKey() + "</h3>");
            for (final Map.Entry<String, Map<String, List<CustomValidateReport>>> branchMap : serviceMap.getValue().entrySet()) {
                final StringBuilder incorrectValueReport = new StringBuilder();
                incorrectValueReport.append("<P> <strong>").append(branchMap.getKey()).append("</strong></p>\n");
                for (final Map.Entry<String, List<CustomValidateReport>> profileMap : branchMap.getValue().entrySet()) {
                    incorrectValueReport.append(" <p> &nbsp; Profile: <strong>").append(profileMap.getKey()).append("</strong></p>");
                    for (final CustomValidateReport customValidateReport : profileMap.getValue()) {
                        incorrectValueReport.append("<P> &nbsp; &nbsp;Incorrect value in property :").append(customValidateReport.getProperty()).append("</p>");
                        for (String message : customValidateReport.getValidationMessages()) {
                            incorrectValueReport.append("<P> &nbsp; &nbsp; &nbsp;").append(message).append("</p>");
                        }
                    }

                }
                serviceText.append(incorrectValueReport.toString());
            }
            htmlDocument.append(serviceText.toString());
        }
        return htmlDocument.toString();
    }


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
            tableHead.append("<th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">").append(profileName).append(TH);
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
                .getKey() + TD);
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
    public String createConsistencyLevelMailBody(final List<ConsistencyLevelAcrossBranchesReport> reportList) {
        String htmlDocument = "<h1>Consistency Level Report</h1>";
        htmlDocument += createConsistencyLevelTable(reportList);
        htmlDocument += LEGENDPROFILE;
        htmlDocument += createConsistencyLevelMissingPropertiesText(reportList);
        return htmlDocument;
    }

    private String createConsistencyLevelTable(final List<ConsistencyLevelAcrossBranchesReport> reportList) {
        final String tableHead = createConsistencyLevelTableHead(reportList);
        final String tableContent = createConsistencyLevelTableRows(reportList);
        return "<table style=\"border: 1px solid black; border-collapse: collapse;\">" + tableHead + tableContent + "</table>";

    }

    private String createConsistencyLevelTableRows(final List<ConsistencyLevelAcrossBranchesReport> reportList) {
        final StringBuilder tableRows = new StringBuilder();
        for (final ConsistencyLevelAcrossBranchesReport consistencyLevelAcrossBranchesReport : reportList) {
            final StringBuilder tableRow = new StringBuilder("<tr style=\"border: 1px solid black;\"><td style=\"border: 1px solid black;border-collapse: collapse;\">" + consistencyLevelAcrossBranchesReport
                    .getService() + TD);
            for (final BranchReport branchReport : consistencyLevelAcrossBranchesReport.getBranchReports()) {
                for (final BranchProfileReport branchProfileReport : branchReport.getConsistencyAcrossBranchesReport().getReport()) {
                    if (branchProfileReport.getPropertyValuePair().isEmpty()) {
                        tableRow.append("<td style=\"background-color: green; border: 1px solid black;border-collapse: collapse;\"></td>");
                    } else {
                        tableRow.append("<td style=\"background-color: red; border: 1px solid black;border-collapse: collapse;\"></td>");
                    }
                }
            }
            tableRow.append(TR);
            tableRows.append(tableRow.toString());
        }
        return tableRows.toString();
    }

    private String createConsistencyLevelMissingPropertiesText(final List<ConsistencyLevelAcrossBranchesReport> reportList) {
        final StringBuilder htmlDocument = new StringBuilder();
        for (final ConsistencyLevelAcrossBranchesReport consistencyLevelAcrossBranchesReport : reportList) {
            final StringBuilder serviceText = new StringBuilder("<h3>" + consistencyLevelAcrossBranchesReport.getService() + "</h3>");
            for (final BranchReport branchReport : consistencyLevelAcrossBranchesReport.getBranchReports()) {
                final StringBuilder missingDataBranchReport = new StringBuilder();
                missingDataBranchReport.append("<P> <strong>").append(branchReport.getFromBranch()).append(" - ").append(branchReport.getToBranch()).append("</strong></p>\n");
                for (final BranchProfileReport branchProfileReport : branchReport.getConsistencyAcrossBranchesReport().getReport()) {
                    missingDataBranchReport.append(" <p> &nbsp; Profile: <strong>").append(branchProfileReport.getProfile()).append("</strong></p>");
                    for (final Pair<String, String> report : branchProfileReport.getPropertyValuePair().stream().filter(pair -> pair.getSecond().equals("MISMATCH"))
                            .collect(Collectors.toList())) {
                        missingDataBranchReport.append("<P> &nbsp; &nbsp;Mismatch of values in:").append(report.getFirst()).append("</p>");
                    }
                    for (final Pair<String, String> report : branchProfileReport.getPropertyValuePair().stream().filter(pair -> pair.getSecond().equals("MISSING"))
                            .collect(Collectors.toList())) {
                        missingDataBranchReport.append("<P>&nbsp; &nbsp;Missing values in ").append(branchReport.getFromBranch()).append(" branch ").append(":")
                                .append(report.getFirst())
                                .append("</p>");
                    }
                }
                serviceText.append(missingDataBranchReport.toString());
            }
            htmlDocument.append(serviceText.toString());
        }
        return htmlDocument.toString();
    }

    private String createConsistencyLevelTableHead(final List<ConsistencyLevelAcrossBranchesReport> reportList) {
        final StringBuilder tableHead = new StringBuilder("<tr style=\"border: 1px solid black;border-collapse: collapse;\"><th style=\"border: 1px solid black;border-collapse: collapse;\" rowspan=\"2\">Service Name</th>");
        for (final BranchReport branchReport : reportList.get(0).getBranchReports()) {
            tableHead.append("<th  colspan=\" ").append(branchReport.getConsistencyAcrossBranchesReport().getReport().size())
                    .append("\" style=\"text-align: center; border: 1px solid black; border-collapse: collapse;\">")
                    .append(branchReport.getFromBranch()).append("-").append(branchReport.getToBranch()).append(TH);
        }
        tableHead.append("</tr> <tr style=\"border: 1px solid black; border-collapse: collapse;\">");
        for (final BranchReport branchReport : reportList.get(0).getBranchReports()) {
            for (final BranchProfileReport branchProfileReport : branchReport.getConsistencyAcrossBranchesReport().getReport()) {
                tableHead.append("<td style=\"border: 1px solid black; border-collapse: collapse;\">").append(branchProfileReport.getProfile()).append(TD);
            }
        }
        tableHead.append(TR);
        return tableHead.toString();
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
            tableHead.append("<th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">").append(profileName).append(TH);
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
                .getKey() + TD);
        for (final String profile : profileNames) {
            columns.append(createProfileTableColumn(serviceName.getValue(), profile));
        }
        return "<tr>" + columns.toString() + TR;
    }

    private String createProfileTableColumn(final Map<String, List<String>> profileReport, final String profile) {
        if (profileReport.containsKey(profile) && profileReport.get(profile) != null) {
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
