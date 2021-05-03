package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.ValidationMessage;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Builder
@Getter
public class CustomValidateReport {
    String property;
    JsonNode value;
    Set<ValidationMessage> validationMessages;
}
