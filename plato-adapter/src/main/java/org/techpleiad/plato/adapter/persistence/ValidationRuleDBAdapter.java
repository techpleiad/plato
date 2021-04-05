package org.techpleiad.plato.adapter.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.adapter.domain.ValidationRuleEntity;
import org.techpleiad.plato.adapter.mapper.ValidationRuleMapper;
import org.techpleiad.plato.adapter.persistence.repository.ValidationRuleRepository;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.List;

@Repository
@Slf4j
public class ValidationRuleDBAdapter implements IValidationRulePersistencePort {

    @Autowired
    private ValidationRuleRepository validationRuleRepository;

    @Autowired
    private ValidationRuleMapper validationRuleMapper;

    @Override
    public List<ValidationRule> findExistingValidationRuleByScopeAndRuleOnProperty(final ValidationRule validationRule) {

        List<ValidationRuleEntity> validationRuleEntityList = validationRuleRepository
                .findValidationRuleByRuleOnPropertyEquals(validationRule.getRuleOnProperty());
        List<ValidationRule> validationRules = validationRuleMapper.convertValidationRuleEntityListToValidationRuleList(validationRuleEntityList);
        return validationRules;
    }

    @Override
    public List<ValidationRule> getValidationRules() {
        List<ValidationRuleEntity> validationRuleEntityList = validationRuleRepository.findAll();
        List<ValidationRule> validationRules = validationRuleMapper.convertValidationRuleEntityListToValidationRuleList(validationRuleEntityList);
        return validationRules;
    }

    @Override
    public ValidationRule addValidationRule(final ValidationRule validationRule) {
        log.info("Creating Validation Rule :: {}", validationRule);
        ValidationRuleEntity validationRuleEntity = validationRuleMapper.convertValidationRuleToValidationRuleEntity(validationRule);
        return validationRuleMapper.convertValidationRuleEntityToValidationRule(validationRuleRepository.insert(validationRuleEntity));
    }
}
