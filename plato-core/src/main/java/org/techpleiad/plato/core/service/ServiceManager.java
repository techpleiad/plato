package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.domain.Branch;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.ServiceAlreadyExistException;
import org.techpleiad.plato.core.exceptions.ServiceNotFoundException;
import org.techpleiad.plato.core.exceptions.ServicesNotFoundException;
import org.techpleiad.plato.core.port.in.IAddServiceUseCase;
import org.techpleiad.plato.core.port.in.IDeleteServiceUseCase;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;
import org.techpleiad.plato.core.port.out.IServicePersistencePort;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServiceManager implements IAddServiceUseCase, IGetServiceUseCase, IDeleteServiceUseCase {

    @Autowired
    private IServicePersistencePort servicePersistencePort;
    @Autowired
    private GitService gitService;

    @Override
    public void addService(final ServiceSpec serviceSpec) {
        if (servicePersistencePort.getServiceById(serviceSpec.getService()).isPresent()) {
            throw new ServiceAlreadyExistException("service name already exists", serviceSpec.getService());
        }

        gitService.validateGitUrlAndBranches(serviceSpec.getGitRepository(), serviceSpec.getBranches().stream().map(Branch::getName).collect(Collectors.toList()));
        servicePersistencePort.addService(serviceSpec);
    }

    @Override
    public List<ServiceSpec> getServicesList() {
        return servicePersistencePort.getServicesList();
    }

    @Override
    public ServiceSpec getService(final String serviceId) {
        return servicePersistencePort.getServiceById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException("service name is not valid", serviceId));
    }

    @Override
    public List<ServiceSpec> getServicesList(final List<String> serviceIdList) {

        final List<ServiceSpec> serviceSpecList = servicePersistencePort.getServicesList(serviceIdList);
        final Set<String> serviceSet = serviceSpecList.stream()
                .map(ServiceSpec::getService)
                .collect(Collectors.toSet());

        final List<String> missingServiceList = serviceIdList.stream()
                .filter(service -> !serviceSet.contains(service))
                .collect(Collectors.toList());

        if (!missingServiceList.isEmpty()) {
            throw new ServicesNotFoundException("services name does not exist", missingServiceList);
        }

        return serviceSpecList;
    }

    @Override
    public void deleteServiceById(final String serviceId) {
        log.info("delete Service Id :: {}", serviceId);
        servicePersistencePort.deleteServiceById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException("service name does not exist", serviceId));
    }
}
