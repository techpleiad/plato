package org.techpleiad.plato.adapter.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.adapter.persistence.repository.ValidationRuleRepository;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.List;

@Repository
@Slf4j
public class ValidationRuleDBAdapter implements IValidationRulePersistencePort {

    @Autowired
    private ValidationRuleRepository validationRuleRepository;

    @Override
    public List<ValidationRule> findExistingValidationRuleByScopeAndRuleOnProperty(final ValidationRule validationRule) {
        return validationRuleRepository
                .findValidationRuleByRuleOnPropertyEquals(validationRule.getRuleOnProperty());
    }

    @Override
    public ValidationRule addValidationRule(final ValidationRule validationRule) {
        log.info("Creating Validation Rule :: {}", validationRule);
        return validationRuleRepository.insert(validationRule);
    }
}
