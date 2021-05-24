package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.convert.SortingNodeFactory;
import org.techpleiad.plato.core.domain.ResolveConsistencyAcrossProfiles;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.FileConvertException;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;
import org.techpleiad.plato.core.port.in.IResolveInconsistencyUseCase;
import org.techpleiad.plato.core.port.in.IValidateBranchUseCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
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
    public String resolveInconsistencyAcrossProfiles(ServiceSpec serviceSpec, String branchName, ResolveConsistencyAcrossProfiles updatedFilesReport) throws ExecutionException, InterruptedException, GitAPIException, IOException {

        validateBranchService.validateBranchInService(serviceSpec, branchName);

        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branchName);

        CompletableFuture<TreeMap<String, File>> treeMap = fileService.getYamlFiles(serviceBranchData.getDirectory(), serviceSpec.getService());
        TreeMap<String, File> profileFileMap = treeMap.get();

        Map<String, String> fileNameToUpdatedFileContentMap = profileJsonNodeToProfileFileContentMap(updatedFilesReport, profileFileMap);

        gitCheckout(serviceBranchData);

        overWriteFiles(fileNameToUpdatedFileContentMap, serviceBranchData.getDirectory());

        gitAddCommit(serviceBranchData);

        //push
        pushCode(serviceBranchData, serviceSpec);

        //MR

        return null;
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

    private void overWriteFiles(Map<String, String> fileNameToUpdatedFileContentMap, File directory) throws IOException {
        File[] files = Objects.requireNonNull(directory.listFiles());
        for (Map.Entry<String, String> fileNameToContent : fileNameToUpdatedFileContentMap.entrySet()) {
            for (File file : files) {
                if (file.getName().equals(fileNameToContent.getKey())) {
                    FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(fileNameToContent.getValue());
                    bw.close();
                }
            }
        }


    }

    private JsonNode convertFileToJsonNode(final File file, final boolean isSorted) {
        final ObjectMapper sortedMapper = JsonMapper.builder().nodeFactory(new SortingNodeFactory()).build();
        try {
            final JsonNode root = file.getName().endsWith(".yml") ?
                    new YAMLMapper().readTree(file) : new ObjectMapper().readTree(file);

            return isSorted ? sortedMapper.readTree(root.toString()) : root;
        } catch (final Exception exception) {
            throw new FileConvertException(exception.getMessage());
        }
    }

    private void gitAddCommit(ServiceBranchData serviceBranchData) throws IOException, GitAPIException {
        String preFix = "Changes made through Plato";
        Git git = Git.open(serviceBranchData.getDirectory());
        git.add().addFilepattern(".").call();
        git.commit().setMessage(preFix).call();
        git.getRepository().getBranch();
        git.close();
    }


    private void pushCode(ServiceBranchData serviceBranchData, ServiceSpec serviceSpec) throws IOException, GitAPIException {
        Git git = Git.open(serviceBranchData.getDirectory());
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(serviceSpec.getGitRepository().getUsername(), serviceSpec.getGitRepository().getPassword()));
        pushCommand.call();
        git.close();
    }

    private void gitCheckout(ServiceBranchData serviceBranchData) throws IOException, GitAPIException {
        String preFix = "Plato/" + serviceBranchData.getBranch() + "/";
        Git git = Git.open(serviceBranchData.getDirectory());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        preFix = preFix + dtf.format(now);

        git.checkout()
                .setCreateBranch(true)
                .setName(preFix)
                .call();

        git.close();
    }
}
