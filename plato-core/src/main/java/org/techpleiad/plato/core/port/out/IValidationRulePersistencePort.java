package org.techpleiad.plato.core.port.out;

import org.techpleiad.plato.core.domain.ValidationRule;

import java.util.List;

public interface IValidationRulePersistencePort {
    void addValidationRule(ValidationRule validationRule);

    List<ValidationRule> findExistingValidationRuleByScopeAndRuleOnProperty(ValidationRule validationRule);
}
