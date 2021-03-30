package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.exceptions.ValidationRuleAlreadyExistsException;
import org.techpleiad.plato.core.port.in.IAddValidationRuleUseCase;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ValidationRuleManager implements IAddValidationRuleUseCase {
    @Autowired
    private IValidationRulePersistencePort validationRulePersistencePort;

    @Override
    public ValidationRule addValidationRule(final ValidationRule validationRule) {
        final Set<String> services = validationRule.getScope().getServices();
        final Set<String> branches = validationRule.getScope().getBranches();
        final Set<String> profiles = validationRule.getScope().getProfiles();
        final List<ValidationRule> existingValidationRules = validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(validationRule);
        if (!existingValidationRules.isEmpty()) {
            for (final ValidationRule rule : existingValidationRules) {
                final Set<String> ruleServices = new HashSet<>(rule.getScope().getServices());
                final Set<String> ruleBranches = new HashSet<>(rule.getScope().getBranches());
                final Set<String> ruleProfiles = new HashSet<>(rule.getScope().getProfiles());

                final Set<String> ruleServices1 = new HashSet<>(rule.getScope().getServices());
                final Set<String> ruleBranches1 = new HashSet<>(rule.getScope().getBranches());
                final Set<String> ruleProfiles1 = new HashSet<>(rule.getScope().getProfiles());
                
                ruleServices.retainAll(services);
                if (!ruleServices.isEmpty() || (ruleServices1.isEmpty() && services.isEmpty())) {
                    ruleBranches.retainAll(branches);
                    if (!ruleBranches.isEmpty() || (ruleBranches1.isEmpty() && branches.isEmpty())) {
                        ruleProfiles.retainAll(profiles);
                        if (!ruleProfiles.isEmpty() || (ruleProfiles1.isEmpty() && profiles.isEmpty())) {
                            throw new ValidationRuleAlreadyExistsException("Validation Rule for this property in given scope already exists", validationRule
                                    .getRuleOnProperty(), validationRule.getRule().toString());
                        }
                    }
                }
            }
        }
        return validationRulePersistencePort.addValidationRule(validationRule);
    }
}
