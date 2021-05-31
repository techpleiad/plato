package org.techpleiad.plato.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileNotSupportedException extends RuntimeException {
    private static final String ERROR_MESSAGE = "service does not support these profiles";
    private final String service;
    private final String profile;

    public String getErrorMessage() {
        return ERROR_MESSAGE;
    }
}
