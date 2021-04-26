package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.in.ICustomValidateUseCase;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;

import java.io.File;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class CustomValidateService implements ICustomValidateUseCase {

    @Autowired
    private IGitServiceUseCase gitService;

    @Autowired
    private IFileServiceUserCase fileService;

    @ThreadDirectory
    @Override
    public void customValidateYamlFile(ServiceSpec serviceSpec, String service, String branch, String profile) throws ExecutionException, InterruptedException {
        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branch);

        final CompletableFuture<TreeMap<String, File>> profileToFileMap = fileService.getYamlFiles(
                serviceBranchData.getDirectory(),
                serviceSpec.getService()
        );
        File yamlByProfile = profileToFileMap.get().get(profile);
        log.info(yamlByProfile.getName());

    }
}
