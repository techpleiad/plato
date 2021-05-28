package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class FileDeleteException extends RuntimeException {
    private final String errorMessage;
    private final String filePath;


    public FileDeleteException(final String errorMessage, final String filePath) {
        this.errorMessage = errorMessage;
        this.filePath = filePath;
    }
}
