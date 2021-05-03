package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CustomValidateBatchResponseTO {
    private String service;
    private String branch;
    private String profile;
    List<CustomValidateResponseTO> customValidateResponseList;
}
