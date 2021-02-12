package org.techpleiad.plato.adapter.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.adapter.persistence.repository.ServiceSpecDBRepository;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.out.IServicePersistencePort;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ServiceSpecDBAdapter implements IServicePersistencePort {

    @Autowired
    private ServiceSpecDBRepository serviceSpecDBRepository;

    @Override
    public List<ServiceSpec> getServicesList() {
        return serviceSpecDBRepository.findAll();
    }

    @Override
    public Optional<ServiceSpec> getServiceById(final String serviceId) {
        return serviceSpecDBRepository.findById(serviceId);
    }

    @Override
    public List<ServiceSpec> getServicesList(final List<String> serviceIdList) {
        return serviceSpecDBRepository.findServiceSpecsByServiceIn(serviceIdList);
    }

    @Override
    public void addService(final ServiceSpec serviceSpec) {
        serviceSpecDBRepository.insert(serviceSpec);
        log.info("Created Service :: {}", serviceSpec);
    }

    @Override
    public Optional<ServiceSpec> deleteServiceById(final String serviceId) {
        final Optional<ServiceSpec> serviceSpec = getServiceById(serviceId);
        serviceSpec.ifPresent(service -> {
            serviceSpecDBRepository.deleteById(serviceId);
            log.info("Deleted Service Id :: {}", serviceId);
        });
        return serviceSpec;
    }
}
