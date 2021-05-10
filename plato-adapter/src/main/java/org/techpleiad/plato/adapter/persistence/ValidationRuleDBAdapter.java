package org.techpleiad.plato.adapter.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.adapter.domain.ValidationRuleEntity;
import org.techpleiad.plato.adapter.mapper.ValidationRuleMapper;
import org.techpleiad.plato.adapter.persistence.repository.ValidationRuleRepository;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.exceptions.ValidationRuleDoesNotExistException;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.List;
import java.util.Optional;

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

    @Override
    public ValidationRule deleteValidationRuleById(final String validationRuleId) {
        final Optional<ValidationRuleEntity> validationRuleEntity = validationRuleRepository.findById(validationRuleId);
        if (validationRuleEntity.isPresent()) {
            validationRuleRepository.deleteById(validationRuleId);
            log.info("Deleted ValidationRule Id :: {}", validationRuleId);
            return validationRuleMapper.convertValidationRuleEntityToValidationRule(validationRuleEntity.get());
        } else {
            throw new ValidationRuleDoesNotExistException("ValidationRule Does Not Exist", validationRuleId);
        }
    }
}
