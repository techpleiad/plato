package org.techpleiad.plato.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@ToString
@Getter
@AllArgsConstructor
public class InvalidRuleException extends RuntimeException {
    private final String errorMessage;
    private final Set<String> detailMessage;
}
