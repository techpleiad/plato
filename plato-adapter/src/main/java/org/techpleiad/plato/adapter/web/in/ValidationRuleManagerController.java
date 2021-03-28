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
import org.techpleiad.plato.core.port.in.IAddValidationRuleUseCase;

@RestController
@Slf4j
public class ValidationRuleManagerController implements IValidationRulesManagerController {
    @Autowired
    private IAddValidationRuleUseCase addValidationRuleUseCase;
    @Autowired
    private ValidationRuleMapper validationRuleMapper;


    @ExecutionTime
    @Override
    public ResponseEntity addRule(final ValidationRuleRequestTO validationRuleRequestTO) {
        final ValidationRule validationRule = validationRuleMapper.convertValidationRuleRequestTOtoValidationRule(validationRuleRequestTO);
        return ResponseEntity.ok(addValidationRuleUseCase.addValidationRule(validationRule));
    }
}
