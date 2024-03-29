package org.techpleiad.plato.api.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.techpleiad.plato.api.advice.ValidJsonSchema;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@ToString
public class ValidationRuleRequestTO {
    @NotNull
    @ValidJsonSchema
    private final JsonNode rule;
    @NotEmpty
    private final String ruleOnProperty;
    @NotNull
    @Valid
    private final RuleScopeRequestTO scope;
}
