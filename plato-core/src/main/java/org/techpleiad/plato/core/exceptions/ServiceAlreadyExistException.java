package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ServiceAlreadyExistException extends RuntimeException {
    private String errorMessage;
    private String id;

    public ServiceAlreadyExistException(final String errorMessage, final String id) {
        this.errorMessage = errorMessage;
        this.id = id;
    }
}
