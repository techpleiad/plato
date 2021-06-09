package org.techpleiad.plato.adapter.web.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.adapter.mapper.FileDetailMapper;
import org.techpleiad.plato.api.response.FileDetailResponseTO;
import org.techpleiad.plato.api.web.IYamlFileManagerController;
import org.techpleiad.plato.core.domain.FileDetail;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.ServiceNotFoundException;
import org.techpleiad.plato.core.port.in.IGetFileUseCase;
import org.techpleiad.plato.core.port.out.IServicePersistencePort;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin
public class FileManagerController implements IYamlFileManagerController {

    @Autowired
    private IServicePersistencePort servicePersistencePort;

    @Autowired
    private IGetFileUseCase getFile;

    @Autowired
    private FileDetailMapper fileDetailMapper;

    @Override
    public ResponseEntity getFileByName(String serviceName, String branchName, String format, String type, String profile) throws InterruptedException, ExecutionException, JsonProcessingException {
        ServiceSpec serviceSpec = servicePersistencePort.getServiceById(serviceName).orElseThrow(() -> new ServiceNotFoundException("service not found", serviceName));
        boolean yaml = format.equals("yaml");
        boolean merged = type.equals("merged");

        if (yaml) {
            String yamlFile = getFile.getFileAsYaml(serviceSpec, branchName, profile, merged);
            return ResponseEntity.ok(yamlFile);
        } else {
            JsonNode jsonFile = getFile.getFileAsJson(serviceSpec, branchName, profile, merged);
            return ResponseEntity.ok(jsonFile);
        }

    }

    @Override
    public ResponseEntity getFilesByProfile(String serviceName, String branchName, String profile) throws InterruptedException, ExecutionException, JsonProcessingException {
        ServiceSpec serviceSpec = servicePersistencePort.getServiceById(serviceName).orElseThrow(() -> new ServiceNotFoundException("service not found", serviceName));
        List<FileDetail> FileDetailList = getFile.getFileMapAsJson(serviceSpec, branchName, profile);
        List<FileDetailResponseTO> fileDetailResponseTOList = fileDetailMapper.convertFileDetailListToFileDetailResponseTOList(FileDetailList);
        return ResponseEntity.ok(fileDetailResponseTOList);
    }
}
