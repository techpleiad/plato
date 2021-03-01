package org.techpleiad.plato.core.exceptions;


import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class GitUsernameNullException extends RuntimeException {
    private String errorMessage;

    public GitUsernameNullException(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
