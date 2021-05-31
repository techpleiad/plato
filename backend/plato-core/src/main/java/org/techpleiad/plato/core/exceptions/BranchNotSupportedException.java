package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class BranchNotSupportedException extends RuntimeException {

    private static final String ERROR_MESSAGE = "service does not support these branches";
    private final String service;
    private final List<String> branches;

    public BranchNotSupportedException(final String service, final List<String> branches) {
        this.branches = branches;
        this.service = service;
    }

    public String getErrorMessage() {
        return ERROR_MESSAGE;
    }
}
