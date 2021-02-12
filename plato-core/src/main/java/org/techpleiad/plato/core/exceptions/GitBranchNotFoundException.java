package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class GitBranchNotFoundException extends RuntimeException {
    private String errorMessage;
    private String url;
    private List<String> branches;

    public GitBranchNotFoundException(final String errorMessage, final String url, final List<String> branches) {
        super(errorMessage + " : " + url);
        this.errorMessage = errorMessage;
        this.branches = branches;
        this.url = url;
    }
}
