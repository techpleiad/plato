package org.techpleiad.plato.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class ValidationRuleRequestTO {
    private String rule;
    private String ruleOnProperty;
    private RuleScopeRequestTO scope;
}
