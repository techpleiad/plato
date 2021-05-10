package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CustomValidateInBatchReport {
    private String service;
    private String branch;
    private String profile;
    private List<CustomValidateReport> customValidateReportList;
}
