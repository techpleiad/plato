package org.techpleiad.plato.core.domain;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ValidationRule {
    private final String ruleId;
    private final RuleScope scope;
    private final String ruleOnProperty;
    private final JsonNode rule;
}
