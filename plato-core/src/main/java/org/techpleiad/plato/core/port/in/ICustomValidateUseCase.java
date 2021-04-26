package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.concurrent.ExecutionException;

public interface ICustomValidateUseCase {

    void customValidateYamlFile(ServiceSpec serviceSpec, String service, String branch, String profile) throws ExecutionException, InterruptedException;
}
