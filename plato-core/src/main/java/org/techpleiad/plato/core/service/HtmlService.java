package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    public String createBranchReportMailBody(List<ConsistencyAcrossBranchesReport> reportList, String fromBranch, String toBranch){
        String htmlDocument = "<h1>Branch Consistency Report<h1>";
        htmlDocument += "<h3>"+"Inconsistency in branches " + fromBranch +" and " + toBranch + " in configuration files" + "</h3> </br>";
        Map<String, Map<String,BranchProfileReport>> reportMap = convertBranchReportToHashMap(reportList);
        htmlDocument += createBranchTable(reportMap);
        htmlDocument += LEGENDBRANCH;
        return htmlDocument;
    }

    private Map<String, Map<String,BranchProfileReport>> convertBranchReportToHashMap(List<ConsistencyAcrossBranchesReport> reportList){
        Map<String, Map<String,BranchProfileReport >> reportMap = new HashMap<>();
        for (ConsistencyAcrossBranchesReport consistencyAcrossBranchesReport: reportList) {
            Map<String, BranchProfileReport> innerReport = new HashMap<>();
            for (BranchProfileReport branchProfileReport:consistencyAcrossBranchesReport.getReport()) {
                innerReport.put(branchProfileReport.getProfile(), branchProfileReport);
            }
            reportMap.put(consistencyAcrossBranchesReport.getService(), innerReport);
        }
        return reportMap;
    }

    private String createBranchTable(Map<String, Map<String,BranchProfileReport>> branchReport){
        Set<String> profileNames = new HashSet<>();

        for (Map.Entry<String, Map<String,BranchProfileReport>> serviceName: branchReport.entrySet()){
            for (Map.Entry<String, BranchProfileReport> profileName: serviceName.getValue().entrySet()) {
                profileNames.add(profileName.getKey());
            }
        }
        StringBuilder tableHead = new StringBuilder("<tr> <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"> Services </th>");
        for (String profileName:profileNames) {
            tableHead.append("<th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">").append(profileName).append("</th>");
        }
        tableHead.append(TR);
        StringBuilder rows = new StringBuilder();
        for (Map.Entry<String, Map<String,BranchProfileReport>> serviceName: branchReport.entrySet()) {
            rows.append(createBranchTableRow(serviceName, profileNames));
        }
        return "<table style=\"width: 100%; border: 1px solid black; border-collapse: collapse;\">" + tableHead.toString() + rows.toString() + " </table>";
    }

    private String createBranchTableRow(Map.Entry<String, Map<String,BranchProfileReport>> serviceName, Set<String> profileNames){
        StringBuilder columns = new StringBuilder("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">" + serviceName.getKey() + "</td>");
        for (String profile:profileNames) {
            columns.append(createBranchTableColumn(serviceName.getValue(), profile));
        }
        return "<tr>" + columns.toString() + TR;
    }

    private String createBranchTableColumn(Map<String,BranchProfileReport> profileReport, String profile){
        if(profileReport.containsKey(profile)){
            if(profileReport.get(profile).isFileEqual() && profileReport.get(profile).getPropertyValueEqual()){
                return "<td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            }else if(!profileReport.get(profile).isFileEqual() && profileReport.get(profile).getPropertyValueEqual()){
                return "<td style=\"background-color: yellow; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            }else {
                return "<td style=\"background-color: red; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            }
        }
        else {
            return "<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\" > N/A </td>";
        }
    }

    @Override
    public String createProfileReportMailBody(List<ConsistencyAcrossProfilesReport> reportList, String branchName){
        String htmlDocument = "<h1>Profile Consistency Report<h1>";
        htmlDocument += "<h3>"+"Inconsistency in profiles in" + " configuration files in " + branchName + " branch"+"</h3> </br>";
        Map<String, Map<String, List<String>>> reportMap = convertProfileReportToHashMap(reportList);
        htmlDocument += createProfileTable(reportMap);
        htmlDocument += LEGENDPROFILE;
        htmlDocument += createMissingPropertiesText(reportList);
        return htmlDocument;
    }

    private Map<String, Map<String, List<String>>> convertProfileReportToHashMap(List<ConsistencyAcrossProfilesReport> reportList){
        Map<String, Map<String, List<String>>> reportMap = new HashMap<>();
        for (ConsistencyAcrossProfilesReport consistencyAcrossProfilesReport:reportList) {
            reportMap.put(consistencyAcrossProfilesReport.getService(), consistencyAcrossProfilesReport.getMissingProperty());
        }
        return reportMap;
    }

    private String createProfileTable(Map<String, Map<String, List<String>>> profileReport){
        Set<String> profileNames = new HashSet<>();
        for (Map.Entry<String, Map<String, List<String>>> serviceName: profileReport.entrySet()){
            for (Map.Entry<String, List<String>> profileName: serviceName.getValue().entrySet()) {
                profileNames.add(profileName.getKey());
            }
        }

        StringBuilder tableHead = new StringBuilder("<tr> <th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"> Services </th>");
        for (String profileName:profileNames) {
            tableHead.append("<th style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">").append(profileName).append("</th>");
        }
        tableHead.append(TR);
        StringBuilder rows = new StringBuilder();
        for (Map.Entry<String, Map<String,List<String>>> serviceName: profileReport.entrySet()) {
            rows.append(createProfileTableRow(serviceName, profileNames));
        }
        return "<table style=\"width: 100%; border: 1px solid black; border-collapse: collapse;\">" + tableHead.toString() + rows.toString() + " </table>";
    }

    private String createProfileTableRow(Map.Entry<String, Map<String,List<String>>> serviceName, Set<String> profileNames){
        StringBuilder columns = new StringBuilder("<td style=\"border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\">" + serviceName.getKey() + "</td>");
        for (String profile:profileNames) {
            columns.append(createProfileTableColumn(serviceName.getValue(), profile));
        }
        return "<tr>" + columns.toString() + TR;
    }

    private String createProfileTableColumn(Map<String,List<String>> profileReport, String profile){
        if(profileReport.containsKey(profile)){
            if(profileReport.get(profile).isEmpty() ){
                return "<td style=\"background-color: green; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            }else {
                return "<td style=\"background-color: red; border: 1px solid black; border-collapse: collapse; padding: 15px; text-align: left;\"></td>";
            }
        }
        else {
            return "<td> N/A </td>";
        }
    }

    private String createMissingPropertiesText(List<ConsistencyAcrossProfilesReport> reportList){
        StringBuilder missingProperties = new StringBuilder("<h3> Missing Properties </h3>");
        for (ConsistencyAcrossProfilesReport consistencyAcrossProfilesReport: reportList) {
            missingProperties.append("<p> <strong>").append(consistencyAcrossProfilesReport.getService()).append("</strong> </p>");
            for (Map.Entry<String, List<String>> missingProperty: consistencyAcrossProfilesReport.getMissingProperty().entrySet()) {
                missingProperties.append("<p> &nbsp; &nbsp; Profile: <strong>").append(missingProperty.getKey()).append(" </strong> </p>");
                for (String property: missingProperty.getValue()) {
                    missingProperties.append("<p> &nbsp; &nbsp; &nbsp; &nbsp; ").append(property).append("</p>");
                }
            }
        }
        return missingProperties.toString();
    }
}
