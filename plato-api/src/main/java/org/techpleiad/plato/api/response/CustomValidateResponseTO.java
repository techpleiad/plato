package org.techpleiad.plato.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class CustomValidateResponseTO {
    private String property;
    private JsonNode value;
    private Set<String> validationMessages;
}
