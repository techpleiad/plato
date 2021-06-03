package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class FileDetail {
    private String service;
    private String profile;
    private String yaml;
    private JsonNode jsonNode;
}
