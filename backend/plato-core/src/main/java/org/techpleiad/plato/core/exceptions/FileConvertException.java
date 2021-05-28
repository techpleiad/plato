package org.techpleiad.plato.core.exceptions;

import lombok.Getter;

@Getter
public class FileConvertException extends RuntimeException {
    private final String errorMessage;

    public FileConvertException(final String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
