package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.mapper.ValidationRuleMapper;
import org.techpleiad.plato.api.request.ValidationRuleRequestTO;
import org.techpleiad.plato.api.web.IValidationRulesManagerController;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.domain.ValidationRuleScope;
import org.techpleiad.plato.core.port.in.IAddValidationRuleUseCase;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class ValidationRuleController implements IValidationRulesManagerController {
    @Autowired
    private IAddValidationRuleUseCase addValidationRuleUseCase;
    @Autowired
    private ValidationRuleMapper validationRuleMapper;
    @Autowired
    private IValidationRulePersistencePort validationRulePersistencePort;


    @ExecutionTime
    @Override
    public ResponseEntity<ValidationRule> addRule(final ValidationRuleRequestTO validationRuleRequestTO) {
        final ValidationRule validationRule = validationRuleMapper.convertValidationRuleRequestTOtoValidationRule(validationRuleRequestTO);
        return ResponseEntity.ok(addValidationRuleUseCase.addValidationRule(validationRule));
    }

    @ExecutionTime
    @Override
    public ResponseEntity<List<ValidationRule>> getRules() {
        List<ValidationRule> validationRules = validationRulePersistencePort.getValidationRules();
        return ResponseEntity.ok(validationRules);
    }

    @ExecutionTime
    @Override
    public ResponseEntity<List<ValidationRule>> getRulesByService(String service) {
        List<ValidationRule> validationRules = validationRulePersistencePort.getValidationRules();
        List<ValidationRule> requiredValidationRules = new ArrayList<>();
        for (ValidationRule validationRule : validationRules) {
            ValidationRuleScope scope = validationRule.getScope();
            if (scope.getServices().contains(service) || scope.getServices().isEmpty()) {
                requiredValidationRules.add(validationRule);

            }
        }
        return ResponseEntity.ok(requiredValidationRules);
    }

    @Override
    public ResponseEntity<ValidationRule> deleteRuleByID(String validationRuleId) {
        ValidationRule validationRule = validationRulePersistencePort.deleteValidationRuleById(validationRuleId);
        return ResponseEntity.ok(validationRule);
    }
}
