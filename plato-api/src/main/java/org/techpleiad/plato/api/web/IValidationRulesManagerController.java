package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ValidationRuleRequestTO;

import javax.validation.Valid;

public interface IValidationRulesManagerController {

    @ApiOperation("Add validation rule")
    @PostMapping(Constants.VERSION_RULES)
    ResponseEntity addRule(@Valid @RequestBody ValidationRuleRequestTO validationRuleRequestTO);
}
