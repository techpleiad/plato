package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EmailException extends RuntimeException {
    private String errorMessage;

    public EmailException(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
