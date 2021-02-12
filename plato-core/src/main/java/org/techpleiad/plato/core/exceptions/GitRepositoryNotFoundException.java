package org.techpleiad.plato.core.exceptions;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GitRepositoryNotFoundException extends RuntimeException {
    private String errorMessage;
    private String url;

    public GitRepositoryNotFoundException(final String errorMessage, final String url) {
        this.errorMessage = errorMessage;
        this.url = url;
    }
}
