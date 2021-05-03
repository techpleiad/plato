package org.techpleiad.plato.core.port.in;

import com.fasterxml.jackson.databind.JsonNode;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface ICustomValidateUseCase {

    Map<String, List<JsonNode>> customValidateYamlFile(ServiceSpec serviceSpec, String service, String branch, String profile) throws ExecutionException, InterruptedException;
}
