package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ProfilePropertiesResponseTO {
    private String profile;
    private List<String> properties;
    private String document;
}
