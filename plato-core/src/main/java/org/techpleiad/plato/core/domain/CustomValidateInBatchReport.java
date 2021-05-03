package org.techpleiad.plato.core.domain;

import lombok.Builder;

import java.util.List;

@Builder
public class CustomValidateInBatchReport {
    private String service;
    private String branch;
    private String profile;
    List<CustomValidateReport> customValidateReportList;
}
