package org.techpleiad.plato.core.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.ServiceAlreadyExistException;
import org.techpleiad.plato.core.exceptions.ServiceNotFoundException;
import org.techpleiad.plato.core.port.out.IServicePersistencePort;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ServiceManagerTest {

    @InjectMocks
    private ServiceManager serviceManager;

    @Mock
    IServicePersistencePort servicePersistencePortMock;

    @Mock
    GitService gitServiceMock;

    @Test
    void givenServiceSpec_whenAddService_thenThrowError() {

        final ServiceSpec serviceSpec = ServiceSpec.builder()
                .service("Rule_Manager")
                .build();

        Mockito.when(servicePersistencePortMock.getServiceById(serviceSpec.getService())).thenReturn(Optional.ofNullable(serviceSpec));

        Assertions.assertThrows(ServiceAlreadyExistException.class, () -> serviceManager.addService(serviceSpec));


    }


    @Test
    void getServicesList() {
    }

    @Test
    void givenServiceId_whenGetService_thenThrowError() {
        final ServiceSpec serviceSpec = ServiceSpec.builder()
                .service("Rule_Manager")
                .build();

        Mockito.when(servicePersistencePortMock.getServiceById(serviceSpec.getService())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ServiceNotFoundException.class, () -> serviceManager.getService(serviceSpec.getService()));

    }

    @Test
    void deleteServiceById() {
    }
}
