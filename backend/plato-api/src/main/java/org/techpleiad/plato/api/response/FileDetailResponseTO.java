package org.techpleiad.plato.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class FileDetailResponseTO {
    private String service;
    private String profile;
    private String yaml;
    private JsonNode jsonNode;
}
