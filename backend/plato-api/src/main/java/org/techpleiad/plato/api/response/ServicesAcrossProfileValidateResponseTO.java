package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ServicesAcrossProfileValidateResponseTO {
    private final String service;
    private final String branch;
    private final List<ProfilePropertiesResponseTO> missingProperty;
}
