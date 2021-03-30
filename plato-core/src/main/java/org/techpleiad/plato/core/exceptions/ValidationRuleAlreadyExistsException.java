package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ValidationRuleAlreadyExistsException extends RuntimeException {
    private final String errorMessage;
    private final String ruleOnProperty;
    private final String rule;

    public ValidationRuleAlreadyExistsException(final String errorMessage, final String ruleOnProperty, final String rule) {
        this.errorMessage = errorMessage;
        this.ruleOnProperty = ruleOnProperty;
        this.rule = rule;
    }
}
