package org.techpleiad.plato.api.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class InvalidRuleException extends RuntimeException {
    private final String errorMessage;

    public InvalidRuleException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
