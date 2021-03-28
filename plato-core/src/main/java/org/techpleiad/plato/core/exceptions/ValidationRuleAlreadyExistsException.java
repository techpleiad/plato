package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ValidationRuleAlreadyExistsException extends RuntimeException {
    private final String errorMessage;
    private final String ruleOnProperty;

    public ValidationRuleAlreadyExistsException(final String errorMessage, final String ruleOnProperty) {
        this.errorMessage = errorMessage;
        this.ruleOnProperty = ruleOnProperty;
    }
}
