package org.techpleiad.plato.adapter.converter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.in.IEncryptionServiceUseCase;

@Component
@WritingConverter
public class ServiceSpecWriteConverter implements Converter<ServiceSpec, ServiceSpec> {

    @Autowired
    private IEncryptionServiceUseCase iEncryptionServiceUseCase;

    @Override
    public ServiceSpec convert(ServiceSpec serviceSpec) {
        return ServiceSpec.builder()
                .service(serviceSpec.getService())
                .description(serviceSpec.getDescription())
                .directory(serviceSpec.getDirectory())
                .gitRepository(iEncryptionServiceUseCase.encryptGitRepository(serviceSpec.getGitRepository()))
                .profiles(serviceSpec.getProfiles())
                .branches(serviceSpec.getBranches())
                .build();
    }
}
