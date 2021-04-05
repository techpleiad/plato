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
import java.util.stream.Collectors;

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
        //Step 1 Fetch Rules
        List<ValidationRule> validationRules = getValidationRules();
        List<ValidationRule> requiredValidationRules = new ArrayList<>();

        Set<String> services = new HashSet<>();
        Set<String> branches = new HashSet<>();
        Set<String> profiles = new HashSet<>();


        //Step 2 Filtering
        for (ValidationRule validationRule : validationRules) {
            ValidationRuleScope scope = validationRule.getScope();

            Set<String> services1 = new HashSet<>(scope.getServices());
            Set<String> branches1 = new HashSet<>(scope.getBranches());
            Set<String> profiles1 = new HashSet<>(scope.getProfiles());

            services.addAll(services1);
            branches.addAll(branches1);
            profiles.addAll(profiles1);

            if (scope.getServices().contains(service) || scope.getServices().isEmpty()) {
                if (scope.getBranches().contains(branch) || scope.getBranches().isEmpty()) {
                    if (scope.getProfiles().contains(profile) || scope.getProfiles().isEmpty()) {
                        requiredValidationRules.add(validationRule);
                    }
                }
            }
        }

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

        //Step 4 Filter and Sort Map
        Map<String, List<ValidationRule>> transformedValidationRuleMap = transformValidationRuleMap(validationRuleMap, services, branches, profiles);

        Map<String, ValidationRule> requiredValidationRuleMap = new HashMap<>();

        for (Map.Entry<String, List<ValidationRule>> validationRuleEntry : transformedValidationRuleMap.entrySet()) {
            List<ValidationRule> transformedValidationRules = validationRuleEntry.getValue();
            if (transformedValidationRules.size() == 1) {
                requiredValidationRuleMap.put(validationRuleEntry.getKey(), transformedValidationRules.get(0));
            } else {
                int shortestServiceLength = getShortestServiceLength(transformedValidationRules);
                int shortestBranchLength = getShortestBranchLength(transformedValidationRules);
                int shortestProfileLength = getShortestProfileLength(transformedValidationRules);

                List<ValidationRule> filteredByServiceRule = transformedValidationRules
                        .stream()
                        .filter(vr -> vr.getScope().getServices().size() == shortestServiceLength)
                        .collect(Collectors.toList());

                List<ValidationRule> filteredByBranchesRule = filteredByServiceRule
                        .stream()
                        .filter(vr -> vr.getScope().getBranches().size() == shortestBranchLength)
                        .collect(Collectors.toList());

                List<ValidationRule> filteredByProfilesRule = filteredByBranchesRule
                        .stream()
                        .filter(vr -> vr.getScope().getProfiles().size() == shortestProfileLength)
                        .collect(Collectors.toList());

                requiredValidationRuleMap.put(validationRuleEntry.getKey(), filteredByProfilesRule.get(0));
            }
        }
        return requiredValidationRuleMap;
    }

    private Map<String, List<ValidationRule>> transformValidationRuleMap(Map<String, List<ValidationRule>> validationRuleMap,
                                                                         Set<String> services,
                                                                         Set<String> branches,
                                                                         Set<String> profiles) {
        for (Map.Entry<String, List<ValidationRule>> validationRuleEntry : validationRuleMap.entrySet()) {
            List<ValidationRule> validationRules = validationRuleEntry.getValue();
            for (ValidationRule validationRule : validationRules) {
                ValidationRuleScope validationRuleScope = validationRule.getScope();
                if (validationRuleScope.getServices().isEmpty()) {
                    validationRuleScope.setServices(services);
                }
                if (validationRuleScope.getBranches().isEmpty()) {
                    validationRuleScope.setBranches(branches);
                }
                if (validationRuleScope.getProfiles().isEmpty()) {
                    validationRuleScope.setProfiles(profiles);
                }
            }
        }
        return validationRuleMap;
    }

    private int getShortestServiceLength(List<ValidationRule> validationRules) {
        int n = 10000000;
        for (ValidationRule validationRule : validationRules) {
            int x = validationRule.getScope().getServices().size();
            if (x <= n) {
                n = x;
            }
        }
        return n;
    }

    private int getShortestBranchLength(List<ValidationRule> validationRules) {
        int n = 10000000;
        for (ValidationRule validationRule : validationRules) {
            int x = validationRule.getScope().getBranches().size();
            if (x <= n) {
                n = x;
            }
        }
        return n;
    }

    private int getShortestProfileLength(List<ValidationRule> validationRules) {
        int n = 10000000;
        for (ValidationRule validationRule : validationRules) {
            int x = validationRule.getScope().getProfiles().size();
            if (x <= n) {
                n = x;
            }
        }
        return n;
    }

    private List<ValidationRule> getValidationRules() {
        return validationRulePersistencePort.getValidationRules();
    }
}
