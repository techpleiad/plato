package org.techpleiad.plato.adapter.web.in;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.mapper.ServiceManagerMapper;
import org.techpleiad.plato.api.request.ServiceRequestTO;
import org.techpleiad.plato.api.response.ServiceResponseTO;
import org.techpleiad.plato.api.web.IServiceManagerController;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.in.IAddServiceUseCase;
import org.techpleiad.plato.core.port.in.IDeleteServiceUseCase;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

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
        return ResponseEntity.ok(addServiceUseCase.addService(serviceSpec));
    }

}
