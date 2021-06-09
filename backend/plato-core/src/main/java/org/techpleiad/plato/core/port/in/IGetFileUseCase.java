package org.techpleiad.plato.core.port.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.techpleiad.plato.core.domain.FileDetail;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IGetFileUseCase {

    String getFileAsYaml(final ServiceSpec serviceSpec, final String branch, final String profile, boolean merged) throws ExecutionException, InterruptedException, JsonProcessingException;

    JsonNode getFileAsJson(final ServiceSpec serviceSpec, final String branch, final String profile, boolean merged) throws ExecutionException, InterruptedException;

    List<FileDetail> getFileMapAsJson(final ServiceSpec serviceSpec, final String branch, final String profile) throws ExecutionException, InterruptedException, JsonProcessingException;

}
