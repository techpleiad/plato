package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class ServicesNotFoundException extends RuntimeException {
    private final String errorMessage;
    private final List<String> serviceList;

    public ServicesNotFoundException(final String errorMessage, final List<String> serviceList) {
        this.errorMessage = errorMessage;
        this.serviceList = serviceList;
    }
}
