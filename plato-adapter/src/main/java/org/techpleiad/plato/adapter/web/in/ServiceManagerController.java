package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.mapper.ServiceManagerMapper;
import org.techpleiad.plato.api.request.ServiceRequestTO;
import org.techpleiad.plato.api.request.ServicesAcrossBranchValidateRequestTO;
import org.techpleiad.plato.api.request.ServicesAcrossProfileValidateRequestTO;
import org.techpleiad.plato.api.response.ServiceResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossBranchValidateResponseTO;
import org.techpleiad.plato.api.response.ServicesAcrossProfileValidateResponseTO;
import org.techpleiad.plato.api.web.IServiceManagerController;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.in.IAddServiceUseCase;
import org.techpleiad.plato.core.port.in.IDeleteServiceUseCase;
import org.techpleiad.plato.core.port.in.IEmailServiceUseCase;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;
import org.techpleiad.plato.core.port.in.IHtmlServiceUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossBranchUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossProfileUseCase;


import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ServiceManagerController implements IServiceManagerController {

    @Autowired
    private IAddServiceUseCase addServiceUseCase;
    @Autowired
    private IDeleteServiceUseCase deleteServiceUseCase;
    @Autowired
    private IGetServiceUseCase getServiceUseCase;
    @Autowired
    private IEmailServiceUseCase emailServiceUseCase;
    @Autowired
    private IValidateAcrossProfileUseCase validateAcrossProfileUseCase;
    @Autowired
    private IValidateAcrossBranchUseCase validateAcrossBranchUseCase;
    @Autowired
    private IHtmlServiceUseCase htmlServiceUseCase;

    @Autowired
    private ServiceManagerMapper serviceManagerMapper;

    @ExecutionTime
    @Override
    public ResponseEntity<List<ServiceResponseTO>> getServices() {
        final List<ServiceSpec> servicesList = getServiceUseCase.getServicesList();
        final List<ServiceResponseTO> serviceSpecList = serviceManagerMapper.convertServiceSpecListToServiceResponseTOList(servicesList);
        return ResponseEntity.ok(serviceSpecList);
    }

    @ExecutionTime
    @Override
    public ResponseEntity<ServiceResponseTO> getServicesByName(final String serviceName) {
        final ServiceSpec serviceSpec = getServiceUseCase.getService(serviceName);
        return ResponseEntity.ok(serviceManagerMapper.convertServiceSpecToServiceResponseTO(serviceSpec));
    }

    @Override
    public ResponseEntity deleteService(final String serviceName) throws IOException {
        deleteServiceUseCase.deleteServiceById(serviceName);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity addService(@Valid final ServiceRequestTO serviceRequestTO) throws Exception {
        final ServiceSpec serviceSpec = serviceManagerMapper.convertServiceRequestTOToServiceSpec(serviceRequestTO);
        addServiceUseCase.addService(serviceSpec);
        return ResponseEntity.ok().build();
    }

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

        String mailBody = htmlServiceUseCase.createProfileReportMailBody(reportList, branchName);

        if(servicesAcrossProfileValidateRequestTO.getEmailConfig().isSendEmail()){
            emailServiceUseCase.sendEmail(mailBody, servicesAcrossProfileValidateRequestTO.getEmailConfig().getRecipients(), "Profile Consistency Report");
        }

        final List<ServicesAcrossProfileValidateResponseTO> responseList =
                reportList.stream().map(report ->
                        serviceManagerMapper.convertInconsistentProfilePropertyToProfilePropertyResponseTO(
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

        final List<ConsistencyAcrossBranchesReport> reportList = validateAcrossBranchUseCase.validateAcrossBranchesInServiceBatch(
                serviceSpecList,
                acrossBranchValidateRequestTO.getFromBranch(),
                acrossBranchValidateRequestTO.getToBranch()
        );

        String mailBody = htmlServiceUseCase.createBranchReportMailBody(reportList,acrossBranchValidateRequestTO.getFromBranch(),
                acrossBranchValidateRequestTO.getToBranch());

        if(acrossBranchValidateRequestTO.getEmailConfig().isSendEmail()){
            emailServiceUseCase.sendEmail(mailBody, acrossBranchValidateRequestTO.getEmailConfig().getRecipients(), "Branch Consistency Report");
        }

        final List<ServicesAcrossBranchValidateResponseTO> servicesAcrossBranchValidateResponseTO = reportList.stream().map(consistencyAcrossBranchesReport -> serviceManagerMapper
                .convertConsistencyAcrossBranchesReportToServicesAcrossBranchValidateResponseTO(consistencyAcrossBranchesReport)).collect(Collectors.toList());

        return ResponseEntity.ok(servicesAcrossBranchValidateResponseTO);
    }
}
