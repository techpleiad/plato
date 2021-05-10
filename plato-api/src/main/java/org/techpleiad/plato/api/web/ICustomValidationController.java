package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ServiceCustomValidateRequestTO;
import org.techpleiad.plato.api.response.CustomValidateBatchResponseTO;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ICustomValidationController {

    @ApiOperation("Custom Validate files")
    @PostMapping(Constants.VERSION_SERVICES_BRANCHES + Constants.CUSTOM_VALIDATE)
    ResponseEntity<List<CustomValidateBatchResponseTO>> customValidateInBatch(@Valid @RequestBody ServiceCustomValidateRequestTO serviceCustomValidateBatchRequestTO) throws ExecutionException, InterruptedException;
}
