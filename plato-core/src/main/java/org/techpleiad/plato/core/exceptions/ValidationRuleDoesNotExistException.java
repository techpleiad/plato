package org.techpleiad.plato.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ValidationRuleDoesNotExistException extends RuntimeException {
    private final String errorMessage;
    private final String ruleId;
}
