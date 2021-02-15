package org.techpleiad.plato.core.exceptions;

import lombok.Getter;

@Getter
public class FileConvertException extends RuntimeException {
    private String errorMessage;
    public FileConvertException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
