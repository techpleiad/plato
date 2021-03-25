package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ValidationRuleAlreadyExistsException extends RuntimeException {
    private String errorMessage;
    private String id;

    public ValidationRuleAlreadyExistsException(final String errorMessage, final String id) {
        this.errorMessage = errorMessage;
        this.id = id;
    }
}
