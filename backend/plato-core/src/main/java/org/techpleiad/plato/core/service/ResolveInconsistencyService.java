package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.domain.ResolveConsistencyAcrossProfiles;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;
import org.techpleiad.plato.core.port.in.IResolveInconsistencyUseCase;
import org.techpleiad.plato.core.port.in.IValidateBranchUseCase;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ResolveInconsistencyService implements IResolveInconsistencyUseCase {

    @Autowired
    private IValidateBranchUseCase validateBranchService;

    @Autowired
    private IGitServiceUseCase gitService;

    @Autowired
    private IFileServiceUserCase fileService;

    @Override
    @ThreadDirectory
    public List<String> resolveInconsistencyAcrossProfiles(ServiceSpec serviceSpec, String branchName, ResolveConsistencyAcrossProfiles updatedFilesReport) throws ExecutionException, InterruptedException, GitAPIException, IOException {

        validateBranchService.validateBranchInService(serviceSpec, branchName);

        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branchName);

        CompletableFuture<TreeMap<String, File>> treeMap = fileService.getYamlFileTree(serviceBranchData.getDirectory(), serviceSpec.getService());
        TreeMap<String, File> profileFileMap = treeMap.get();

        Map<String, String> fileNameToUpdatedFileContentMap = profileJsonNodeToProfileFileContentMap(updatedFilesReport, profileFileMap);

        gitService.gitCheckout(serviceBranchData);

        fileService.overWriteFiles(fileNameToUpdatedFileContentMap, serviceBranchData.getDirectory());

        gitService.gitAddCommit(serviceBranchData);

        List<String> pushResult = gitService.pushUpdates(serviceBranchData, serviceSpec);

        return pushResult;
    }


    private Map<String, String> profileJsonNodeToProfileFileContentMap(final ResolveConsistencyAcrossProfiles resolveConsistencyAcrossProfiles, final TreeMap<String, File> profileFileMap) throws JsonProcessingException {
        TreeMap<String, String> fileContentTreeMap = new TreeMap<>();
        for (Map.Entry<String, String> jsonNodeEntry : resolveConsistencyAcrossProfiles.getProfileDocument().entrySet()) {
            String fileName = profileFileMap.get(jsonNodeEntry.getKey()).getName();
            String yamlFileContent = jsonNodeEntry.getValue();
            fileContentTreeMap.put(fileName, yamlFileContent);
        }
        return fileContentTreeMap;
    }

}
