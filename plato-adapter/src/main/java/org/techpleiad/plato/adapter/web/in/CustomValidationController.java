package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.mapper.CustomValidationMapper;
import org.techpleiad.plato.api.request.ServiceCustomValidateRequestTO;
import org.techpleiad.plato.api.response.CustomValidateBatchResponseTO;
import org.techpleiad.plato.api.web.ICustomValidationController;
import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;
import org.techpleiad.plato.core.port.in.ICustomValidateUseCase;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class CustomValidationController implements ICustomValidationController {

    @Autowired
    private ICustomValidateUseCase customValidateUseCase;

    @Autowired
    private CustomValidationMapper customValidationMapper;


    @Override
    public ResponseEntity<List<CustomValidateBatchResponseTO>> customValidateInBatch(@Valid ServiceCustomValidateRequestTO serviceCustomValidateBatchRequestTO) throws ExecutionException, InterruptedException {
        List<CustomValidateInBatchReport> customValidateInBatchReports = customValidateUseCase
                .customValidateInBatch(serviceCustomValidateBatchRequestTO.getServices(), serviceCustomValidateBatchRequestTO.getBranches(), serviceCustomValidateBatchRequestTO
                        .getProfiles());

        List<CustomValidateBatchResponseTO> reportList = customValidationMapper
                .convertCustomValidateInBranchReportListToCustomValidateBatchResponseTOList(customValidateInBatchReports);

        return ResponseEntity.ok(reportList);
    }
}
