package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ServiceNotFoundException extends RuntimeException {
    private String errorMessage;
    private String service;

    public ServiceNotFoundException(final String errorMessage, final String service) {
        this.errorMessage = errorMessage;
        this.service = service;
    }
}
