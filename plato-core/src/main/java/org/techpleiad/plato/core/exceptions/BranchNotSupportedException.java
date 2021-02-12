package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class BranchNotSupportedException extends RuntimeException {

    private String errorMessage = "service does not support these branches";
    private String service;
    private List<String> branches;

    public BranchNotSupportedException(final String service, final List<String> branches) {
        this.branches = branches;
        this.service = service;
    }
}
