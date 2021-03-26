package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ServiceSpec;

public interface IAddServiceUseCase {

    ServiceSpec addService(ServiceSpec serviceSpec);
}
