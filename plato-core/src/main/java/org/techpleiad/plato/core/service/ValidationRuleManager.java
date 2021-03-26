package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.exceptions.ValidationRuleAlreadyExistsException;
import org.techpleiad.plato.core.port.in.IAddValidationRuleUseCase;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

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
        final List<ValidationRule> validationRules = validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(validationRule);
        if (!validationRules.isEmpty()) {
            for (final ValidationRule rule : validationRules) {
                final Set<String> ruleServices = rule.getScope().getServices();
                final Set<String> ruleBranches = rule.getScope().getBranches();
                final Set<String> ruleProfiles = rule.getScope().getProfiles();

                if (services.isEmpty() && ruleServices.isEmpty()) {
                    if (ruleBranches.isEmpty() && branches.isEmpty()) {
                        if (ruleProfiles.isEmpty() && profiles.isEmpty()) {
                            throw new ValidationRuleAlreadyExistsException("Validation Rule for this property in given scope already exists", validationRule.getRuleOnProperty());
                        }
                    }
                }

                ruleServices.retainAll(services);
                if (!ruleServices.isEmpty()) {
                    ruleBranches.retainAll(branches);
                    if (!ruleBranches.isEmpty()) {
                        ruleProfiles.retainAll(profiles);
                        if (!ruleProfiles.isEmpty()) {
                            throw new ValidationRuleAlreadyExistsException("Validation Rule for this property in given scope already exists", validationRule.getRuleOnProperty());
                        }
                    }
                }

            }
        }
        return validationRulePersistencePort.addValidationRule(validationRule);
    }
}
