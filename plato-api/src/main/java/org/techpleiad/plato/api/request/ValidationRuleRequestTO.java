package org.techpleiad.plato.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@ToString
public class ValidationRuleRequestTO {
    @NotNull
    private final JsonNode rule;
    @NotNull
    private final String ruleOnProperty;
    @NotNull
    private final RuleScopeRequestTO scope;
}
