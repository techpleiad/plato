package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.domain.ValidationRuleScope;
import org.techpleiad.plato.core.exceptions.ValidationRuleAlreadyExistsException;
import org.techpleiad.plato.core.port.in.IAddValidationRuleUseCase;
import org.techpleiad.plato.core.port.in.IGetValidationRuleUseCase;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ValidationRuleService implements IAddValidationRuleUseCase, IGetValidationRuleUseCase {
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

    @Override
    public Map<String, ValidationRule> getValidationRuleMapByScope(String service, String branch, String profile) {

        List<ValidationRule> requiredValidationRules = getAppropriateValidationRules(service, branch, profile);

        Map<String, List<ValidationRule>> validationRuleMap = createValidationRuleMap(requiredValidationRules);

        Map<String, ValidationRule> requiredValidationRuleMap = filterValidationRuleMap(validationRuleMap);

        return requiredValidationRuleMap;
    }

    private List<ValidationRule> getAppropriateValidationRules(String service, String branch, String profile) {
        //Step 1 Fetch Rules
        List<ValidationRule> validationRules = getValidationRules();
        //Step 2 Filtering
        List<ValidationRule> requiredValidationRules = new ArrayList<>();
        for (ValidationRule validationRule : validationRules) {
            ValidationRuleScope scope = validationRule.getScope();
            if (scope.getServices().contains(service) || scope.getServices().isEmpty()) {
                if (scope.getBranches().contains(branch) || scope.getBranches().isEmpty()) {
                    if (scope.getProfiles().contains(profile) || scope.getProfiles().isEmpty()) {
                        requiredValidationRules.add(validationRule);
                    }
                }
            }
        }
        return requiredValidationRules;
    }

    private Map<String, List<ValidationRule>> createValidationRuleMap(List<ValidationRule> requiredValidationRules) {
        //Step 3 Create Map
        Map<String, List<ValidationRule>> validationRuleMap = new HashMap<>();
        for (ValidationRule validationRule : requiredValidationRules) {
            if (validationRuleMap.get(validationRule.getRuleOnProperty()) == null) {
                List<ValidationRule> rule = new ArrayList<>();
                rule.add(validationRule);
                validationRuleMap.put(validationRule.getRuleOnProperty(), rule);
            } else {
                List<ValidationRule> rules = validationRuleMap.get(validationRule.getRuleOnProperty());
                rules.add(validationRule);
                validationRuleMap.replace(validationRule.getRuleOnProperty(), rules);
            }
        }
        return validationRuleMap;
    }

    private Map<String, ValidationRule> filterValidationRuleMap(Map<String, List<ValidationRule>> validationRuleMap) {
        //Step 4 Filter and Sort Map
        Map<String, ValidationRule> requiredValidationRuleMap = new HashMap<>();
        for (Map.Entry<String, List<ValidationRule>> validationRuleEntry : validationRuleMap.entrySet()) {
            List<ValidationRule> validationRuleList = validationRuleEntry.getValue();
            if (validationRuleList.size() == 1) {
                requiredValidationRuleMap.put(validationRuleEntry.getKey(), validationRuleList.get(0));
            } else {
                boolean neServices = false;
                boolean eServices = false;
                boolean neBranches = false;
                boolean eBranches = false;
                boolean neProfiles = false;
                boolean eProfiles = false;
                for (ValidationRule validationRule : validationRuleList) {
                    if (validationRule.getScope().getServices().isEmpty()) {
                        eServices = true;
                    } else {
                        neServices = true;
                    }
                    if (validationRule.getScope().getBranches().isEmpty()) {
                        eBranches = true;
                    } else {
                        neBranches = true;
                    }
                    if (validationRule.getScope().getProfiles().isEmpty()) {
                        eProfiles = true;
                    } else {
                        neProfiles = true;
                    }
                }
                if (neServices && eServices) {
                    validationRuleList.removeIf(p -> p.getScope().getServices().isEmpty());
                    if (validationRuleList.size() == 1) {
                        requiredValidationRuleMap.put(validationRuleEntry.getKey(), validationRuleEntry.getValue().get(0));
                        continue;
                    }
                }
                if (neBranches && eBranches) {
                    validationRuleList.removeIf(p -> p.getScope().getBranches().isEmpty());
                    if (validationRuleList.size() == 1) {
                        requiredValidationRuleMap.put(validationRuleEntry.getKey(), validationRuleEntry.getValue().get(0));
                        continue;
                    }
                }
                if (neProfiles && eProfiles) {
                    validationRuleList.removeIf(p -> p.getScope().getProfiles().isEmpty());
                    if (validationRuleList.size() == 1) {
                        requiredValidationRuleMap.put(validationRuleEntry.getKey(), validationRuleEntry.getValue().get(0));
                        continue;
                    }
                }
            }
            requiredValidationRuleMap.put(validationRuleEntry.getKey(), validationRuleEntry.getValue().get(0));
        }
        return requiredValidationRuleMap;
    }

    private List<ValidationRule> getValidationRules() {
        return validationRulePersistencePort.getValidationRules();
    }
}
