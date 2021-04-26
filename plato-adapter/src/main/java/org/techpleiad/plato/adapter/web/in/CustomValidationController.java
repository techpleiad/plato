package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.api.request.ServiceCustomValidateRequestTO;
import org.techpleiad.plato.api.web.ICustomValidationController;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.in.ICustomValidateUseCase;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class CustomValidationController implements ICustomValidationController {

    @Autowired
    private IGetServiceUseCase getServiceUseCase;

    @Autowired
    private ICustomValidateUseCase customValidateUseCase;

    @ExecutionTime
    @Override
    public ResponseEntity customValidate(@Valid ServiceCustomValidateRequestTO serviceCustomValidateRequestTO) throws ExecutionException, InterruptedException {
        ServiceSpec serviceSpec = getServiceUseCase.getService(serviceCustomValidateRequestTO.getService());
        customValidateUseCase
                .customValidateYamlFile(serviceSpec, serviceCustomValidateRequestTO.getService(), serviceCustomValidateRequestTO.getBranch(), serviceCustomValidateRequestTO
                        .getProfile());
        return null;
    }
}
