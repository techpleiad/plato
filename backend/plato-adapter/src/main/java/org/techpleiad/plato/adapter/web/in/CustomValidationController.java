package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.config.ConfigToolConfig;
import org.techpleiad.plato.adapter.mapper.CustomValidationMapper;
import org.techpleiad.plato.api.request.ServiceCustomValidateRequestTO;
import org.techpleiad.plato.api.response.CustomValidateBatchResponseTO;
import org.techpleiad.plato.api.web.ICustomValidationController;
import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;
import org.techpleiad.plato.core.port.in.ICustomValidateUseCase;
import org.techpleiad.plato.core.port.in.IEmailServiceUseCase;
import org.techpleiad.plato.core.port.in.IHtmlServiceUseCase;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
@Slf4j
public class CustomValidationController implements ICustomValidationController {

    @Autowired
    private ICustomValidateUseCase customValidateUseCase;

    @Autowired
    private CustomValidationMapper customValidationMapper;

    @Autowired
    private IHtmlServiceUseCase htmlServiceUseCase;

    @Autowired
    private ConfigToolConfig configToolConfig;

    @Autowired
    private IEmailServiceUseCase emailServiceUseCase;


    @Override
    public ResponseEntity<List<CustomValidateBatchResponseTO>> customValidateInBatch(@Valid ServiceCustomValidateRequestTO serviceCustomValidateBatchRequestTO) throws ExecutionException, InterruptedException {
        List<CustomValidateInBatchReport> customValidateInBatchReports = customValidateUseCase
                .customValidateInBatch(serviceCustomValidateBatchRequestTO.getServices(), serviceCustomValidateBatchRequestTO.getBranches(), serviceCustomValidateBatchRequestTO
                        .getProfiles());

        if (serviceCustomValidateBatchRequestTO.getEmail() != null && !CollectionUtils.isEmpty(serviceCustomValidateBatchRequestTO.getEmail().getRecipients())) {
            final String subject = configToolConfig.getProfileConsistencyEmailSubject();
            final String mailBody = htmlServiceUseCase.createCustomValidationInBatchReportMailBody(customValidateInBatchReports);
            emailServiceUseCase
                    .sendEmail(mailBody, serviceCustomValidateBatchRequestTO.getEmail().getRecipients(), subject, configToolConfig
                            .getEmailFrom());
        }

        List<CustomValidateBatchResponseTO> reportList = customValidationMapper
                .convertCustomValidateInBranchReportListToCustomValidateBatchResponseTOList(customValidateInBatchReports);

        return ResponseEntity.ok(reportList);
    }
}
