package org.techpleiad.plato.api.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class CustomValidateResponseTO {
    String property;
    JsonNode value;
    Set<ValidationMessage> validationMessages;

}
