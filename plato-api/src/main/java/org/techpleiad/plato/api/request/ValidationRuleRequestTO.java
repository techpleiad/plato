package org.techpleiad.plato.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ValidationRuleRequestTO {
    private final JsonNode rule;
    final private String ruleOnProperty;
    final private RuleScopeRequestTO scope;
}
