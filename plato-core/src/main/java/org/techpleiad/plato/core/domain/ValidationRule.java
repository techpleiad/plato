package org.techpleiad.plato.core.domain;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class ValidationRule {
    private String ruleId;
    private ValidationRuleScope scope;
    private String ruleOnProperty;
    private JsonNode rule;
}
