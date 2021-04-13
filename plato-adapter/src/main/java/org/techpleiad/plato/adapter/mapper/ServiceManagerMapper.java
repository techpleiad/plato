package org.techpleiad.plato.adapter.mapper;


import org.mapstruct.Mapper;
import org.techpleiad.plato.api.request.ServiceRequestTO;
import org.techpleiad.plato.api.response.ServiceResponseTO;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceManagerMapper {

    ServiceSpec convertServiceRequestTOToServiceSpec(final ServiceRequestTO serviceRequestTO);

    ServiceResponseTO convertServiceSpecToServiceResponseTO(final ServiceSpec serviceSpec);

    List<ServiceResponseTO> convertServiceSpecListToServiceResponseTOList(List<ServiceSpec> serviceSpecs);

}
