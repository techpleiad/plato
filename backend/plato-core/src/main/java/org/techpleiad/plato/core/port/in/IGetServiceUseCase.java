package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;

public interface IGetServiceUseCase {

    List<ServiceSpec> getServicesList();

    ServiceSpec getService(String serviceId);

    List<ServiceSpec> getServicesList(List<String> serviceIdList);
}
