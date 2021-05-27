package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ValidationRuleRequestTO;

import javax.validation.Valid;

public interface IValidationRulesManagerController {

    @ApiOperation("Add validation rule")
    @PostMapping(Constants.VERSION_RULES)
    ResponseEntity addRule(@Valid @RequestBody ValidationRuleRequestTO validationRuleRequestTO);

    @ApiOperation("Get validation rules")
    @GetMapping(value = Constants.VERSION_RULES)
    ResponseEntity getRules();

    @ApiOperation("Get validation rules By Service")
    @GetMapping(value = Constants.VERSION_RULES + "/{serviceName}")
    ResponseEntity getRulesByService(@Valid @PathVariable String serviceName);

    @ApiOperation("Delete validation rule By ID")
    @DeleteMapping(value = Constants.VERSION_RULES + "/{validationRuleId}")
    ResponseEntity deleteRuleByID(@Valid @PathVariable String validationRuleId);
}
