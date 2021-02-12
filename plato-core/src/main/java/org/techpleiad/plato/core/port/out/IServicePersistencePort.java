package org.techpleiad.plato.core.port.out;

import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;
import java.util.Optional;

public interface IServicePersistencePort {

    void addService(ServiceSpec serviceSpec);

    Optional<ServiceSpec> deleteServiceById(String serviceId);

    List<ServiceSpec> getServicesList();

    Optional<ServiceSpec> getServiceById(String serviceId);

    List<ServiceSpec> getServicesList(List<String> serviceIdList);
}
