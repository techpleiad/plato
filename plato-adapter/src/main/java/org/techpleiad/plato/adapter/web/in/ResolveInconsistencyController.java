package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.mapper.ValidationMapper;
import org.techpleiad.plato.api.response.ServicesAcrossProfileValidateResponseTO;
import org.techpleiad.plato.api.web.IResolveInconsistencyController;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;
import org.techpleiad.plato.core.port.in.IResolveInconsistencyUseCase;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class ResolveInconsistencyController implements IResolveInconsistencyController {

    @Autowired
    private IGetServiceUseCase getServiceUseCase;

    @Autowired
    private ValidationMapper validationMapper;

    @Autowired
    private IResolveInconsistencyUseCase resolveInconsistencyUseCase;

    @Override
    public ResponseEntity resolveInconsistencyAcrossProfiles(ServicesAcrossProfileValidateResponseTO servicesAcrossProfileValidateResponseTO) throws ExecutionException, InterruptedException, GitAPIException, IOException {

        final List<ServiceSpec> serviceSpecList = getServiceUseCase.getServicesList(Collections.singletonList(servicesAcrossProfileValidateResponseTO.getService()));

        ConsistencyAcrossProfilesReport consistencyAcrossProfilesReport = validationMapper
                .convertServicesAcrossProfileValidateResponseTOToConsistencyAcrossProfile(servicesAcrossProfileValidateResponseTO);

        resolveInconsistencyUseCase
                .resolveInconsistencyAcrossProfiles(serviceSpecList.get(0), servicesAcrossProfileValidateResponseTO.getBranch(), consistencyAcrossProfilesReport);

        return null;
    }
}
