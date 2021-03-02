package org.techpleiad.plato.core.exceptions;


import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class InvalidGitCredentials extends RuntimeException {
    private String errorMessage;

    public InvalidGitCredentials(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
