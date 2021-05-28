package org.techpleiad.plato.adapter.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.adapter.persistence.repository.ServiceSpecRepository;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.out.IServicePersistencePort;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ServiceSpecDBAdapter implements IServicePersistencePort {

    @Autowired
    private ServiceSpecRepository serviceSpecRepository;

    @Override
    public List<ServiceSpec> getServicesList() {
        return serviceSpecRepository.findAll();
    }

    @Override
    public Optional<ServiceSpec> getServiceById(final String serviceId) {
        return serviceSpecRepository.findById(serviceId);
    }

    @Override
    public List<ServiceSpec> getServicesList(final List<String> serviceIdList) {
        return serviceSpecRepository.findServiceSpecsByServiceIn(serviceIdList);
    }

    @Override
    public ServiceSpec addService(final ServiceSpec serviceSpec) {
        log.info("Creating Service :: {}", serviceSpec);
        return serviceSpecRepository.insert(serviceSpec);
    }

    @Override
    public Optional<ServiceSpec> deleteServiceById(final String serviceId) {
        final Optional<ServiceSpec> serviceSpec = getServiceById(serviceId);
        serviceSpec.ifPresent(service -> {
            serviceSpecRepository.deleteById(serviceId);
            log.info("Deleted Service Id :: {}", serviceId);
        });
        return serviceSpec;
    }
}
