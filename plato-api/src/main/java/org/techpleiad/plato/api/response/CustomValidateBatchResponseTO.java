package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
@Setter
public class CustomValidateBatchResponseTO {
    private String service;
    private String branch;
    private String profile;
    private List<CustomValidateResponseTO> customValidateReportList;
}
