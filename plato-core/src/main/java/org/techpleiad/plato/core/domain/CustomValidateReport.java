package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class CustomValidateReport {
    private String property;
    private JsonNode value;
    private Set<String> validationMessages;
}
