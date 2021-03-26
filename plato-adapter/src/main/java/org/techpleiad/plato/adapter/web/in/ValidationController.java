package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.config.ConfigToolConfig;
import org.techpleiad.plato.adapter.mapper.ServiceManagerMapper;
import org.techpleiad.plato.adapter.mapper.ValidationRuleMapper;
import org.techpleiad.plato.api.request.ServicesAcrossBranchValidateRequestTO;
import org.techpleiad.plato.api.request.ServicesAcrossProfileValidateRequestTO;
import org.techpleiad.plato.api.request.ServicesConsistencyLevelAcrossBranchValidateRequestTO;
import org.techpleiad.plato.api.response.ConsistencyLevelValidateResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossBranchValidateResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossProfileValidateResponseTO;
import org.techpleiad.plato.api.web.IValidationController;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.ConsistencyLevelAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.domain.ValidationAcrossBranchProperties;
import org.techpleiad.plato.core.port.in.IEmailServiceUseCase;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;
import org.techpleiad.plato.core.port.in.IHtmlServiceUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossBranchConsistencyLevelUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossBranchUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossProfileUseCase;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ValidationController implements IValidationController {
    @Autowired
    private IGetServiceUseCase getServiceUseCase;
    @Autowired
    private ValidationRuleMapper validationRuleMapper;
    @Autowired
    private IEmailServiceUseCase emailServiceUseCase;
    @Autowired
    private IValidateAcrossProfileUseCase validateAcrossProfileUseCase;
    @Autowired
    private IValidateAcrossBranchUseCase validateAcrossBranchUseCase;
    @Autowired
    private IValidateAcrossBranchConsistencyLevelUseCase validateAcrossBranchConsistencyLevelUseCase;
    @Autowired
    private IHtmlServiceUseCase htmlServiceUseCase;
    @Autowired
    private ConfigToolConfig configToolConfig;
    @Autowired
    private ServiceManagerMapper serviceManagerMapper;

    @ExecutionTime
    @Override
    public ResponseEntity<List<ServicesAcrossProfileValidateResponseTO>> validateAcrossProfiles(
            @Valid final ServicesAcrossProfileValidateRequestTO servicesAcrossProfileValidateRequestTO,
            final String branchName
    ) throws ExecutionException, InterruptedException {
        final List<ServiceSpec> serviceSpecList = getServiceUseCase.getServicesList(servicesAcrossProfileValidateRequestTO.getServices());

        final List<ConsistencyAcrossProfilesReport> reportList = validateAcrossProfileUseCase
                .validateAcrossProfilesInServiceBatch(
                        serviceSpecList,
                        branchName,
                        servicesAcrossProfileValidateRequestTO.isIncludeSuppressed()
                );


        if (servicesAcrossProfileValidateRequestTO.getEmail() != null && !CollectionUtils.isEmpty(servicesAcrossProfileValidateRequestTO.getEmail().getRecipients())) {
            final String subject = configToolConfig.getProfileConsistencyEmailSubject().replace("{{branch}}", branchName);
            final String mailBody = htmlServiceUseCase.createProfileReportMailBody(reportList, branchName);
            emailServiceUseCase
                    .sendEmail(mailBody, servicesAcrossProfileValidateRequestTO.getEmail().getRecipients(), subject, configToolConfig
                            .getEmailFrom());
        }

        final List<ServicesAcrossProfileValidateResponseTO> responseList =
                reportList.stream().map(report ->
                        validationRuleMapper.convertInconsistentProfilePropertyToProfilePropertyResponseTO(
                                report.getService(),
                                branchName,
                                report
                        )).collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @ExecutionTime
    @Override
    public ResponseEntity validateAcrossBranches(@Valid final ServicesAcrossBranchValidateRequestTO acrossBranchValidateRequestTO)
            throws ExecutionException, InterruptedException {

        final List<ServiceSpec> serviceSpecList = getServiceUseCase.getServicesList(acrossBranchValidateRequestTO.getServices());

        final ValidationAcrossBranchProperties validationAcrossBranchProperties = ValidationAcrossBranchProperties.builder()
                .fromBranch(acrossBranchValidateRequestTO.getFromBranch())
                .toBranch(acrossBranchValidateRequestTO.getToBranch())
                .propertyValueEqual(acrossBranchValidateRequestTO.isPropertyValueEqual())
                .build();

        final List<ConsistencyAcrossBranchesReport> reportList = validateAcrossBranchUseCase.validateAcrossBranchesInServiceBatch(
                serviceSpecList,
                validationAcrossBranchProperties
        );

        if (acrossBranchValidateRequestTO.getEmail() != null && !CollectionUtils.isEmpty(acrossBranchValidateRequestTO.getEmail().getRecipients())) {
            final String subject = configToolConfig.getBranchConsistencyEmailSubject()
                    .replace("{{branch1}}", acrossBranchValidateRequestTO.getFromBranch())
                    .replace("{{branch2}}", acrossBranchValidateRequestTO.getToBranch());
            final String mailBody = htmlServiceUseCase.createBranchReportMailBody(reportList, acrossBranchValidateRequestTO.getFromBranch(),
                    acrossBranchValidateRequestTO.getToBranch());
            emailServiceUseCase.sendEmail(mailBody, acrossBranchValidateRequestTO.getEmail().getRecipients(), subject, configToolConfig
                    .getEmailFrom());
        }

        final List<ServicesAcrossBranchValidateResponseTO> servicesAcrossBranchValidateResponseTO = validationRuleMapper
                .convertConsistencyAcrossBranchesReportListToServicesAcrossBranchListValidateResponseTO(reportList);

        return ResponseEntity.ok(servicesAcrossBranchValidateResponseTO);
    }

    @ExecutionTime
    @Override
    public ResponseEntity validateAcrossBranchesConsistencyLevel(@Valid final ServicesConsistencyLevelAcrossBranchValidateRequestTO servicesConsistencyLevelAcrossBranchValidateRequestTO) throws ExecutionException, InterruptedException {
        final List<ServiceSpec> serviceSpecList = getServiceUseCase.getServicesList(servicesConsistencyLevelAcrossBranchValidateRequestTO.getServices());

        final List<ConsistencyLevelAcrossBranchesReport> reportList = validateAcrossBranchConsistencyLevelUseCase.validateAcrossBranchesConsistencyLevelServiceBatch(
                serviceSpecList,
                servicesConsistencyLevelAcrossBranchValidateRequestTO.isPropertyValueEqual(),
                servicesConsistencyLevelAcrossBranchValidateRequestTO.getTargetBranch()
        );

        if (servicesConsistencyLevelAcrossBranchValidateRequestTO.getEmail() != null && !CollectionUtils
                .isEmpty(servicesConsistencyLevelAcrossBranchValidateRequestTO.getEmail().getRecipients())) {
            final String mailBody = htmlServiceUseCase.createConsistencyLevelMailBody(reportList);
            emailServiceUseCase.sendEmail(mailBody, servicesConsistencyLevelAcrossBranchValidateRequestTO.getEmail().getRecipients(), configToolConfig
                    .getBranchConsistencyEmailSubject(), configToolConfig
                    .getEmailFrom());
        }

        final List<ConsistencyLevelValidateResponseTO> consistencyLevelValidateResponseTO = validationRuleMapper
                .convertConsistencyLevelBranchesReportToConsistencyLevelResponse(reportList);

        return ResponseEntity.ok(consistencyLevelValidateResponseTO);
    }

}
