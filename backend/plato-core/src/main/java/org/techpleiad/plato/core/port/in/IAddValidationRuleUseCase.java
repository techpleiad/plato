package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ValidationRule;

public interface IAddValidationRuleUseCase {

    ValidationRule addValidationRule(ValidationRule validationRule);
}
