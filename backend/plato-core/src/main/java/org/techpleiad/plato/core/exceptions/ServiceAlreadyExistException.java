package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ServiceAlreadyExistException extends RuntimeException {
    private final String errorMessage;
    private final String id;

    public ServiceAlreadyExistException(final String errorMessage, final String id) {
        this.errorMessage = errorMessage;
        this.id = id;
    }
}
