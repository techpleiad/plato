package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ServiceCustomValidateRequestTO;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

public interface ICustomValidationController {

    @ApiOperation("Custom Validate file")
    @PostMapping(Constants.VERSION_SERVICES_BRANCHES + Constants.CUSTOM_VALIDATE)
    ResponseEntity customValidate(@Valid @RequestBody ServiceCustomValidateRequestTO serviceCustomValidateRequestTO) throws ExecutionException, InterruptedException;
}
