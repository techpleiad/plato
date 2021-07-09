package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.convert.SortingNodeFactory;
import org.techpleiad.plato.core.domain.FileDetail;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.FileConvertException;
import org.techpleiad.plato.core.exceptions.FileDeleteException;
import org.techpleiad.plato.core.exceptions.ProfileNotSupportedException;
import org.techpleiad.plato.core.exceptions.ServiceNotFoundException;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IFileThreadServiceUseCase;
import org.techpleiad.plato.core.port.in.IGetFileUseCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService implements IFileServiceUserCase, IFileThreadServiceUseCase, IGetFileUseCase {

    @Autowired
    private IGitServiceUseCase gitService;

    public static final ThreadLocal<File> WORKING_DIRECTORY = new ThreadLocal<>();

    private static final String SEPARATOR = "/";

    @Override
    public File generateFileFromLocalDirectoryPath(final String... files) {
        final List<String> path = Arrays.stream(files).filter(e -> !StringUtils.isEmptyOrNull(e)).collect(Collectors.toList());
        System.out.println(WORKING_DIRECTORY.toString());
        final String directory = WORKING_DIRECTORY.get().getPath() + SEPARATOR + StringUtils.join(path, SEPARATOR);
        return new File(directory);
    }

    @Override
    public String generateDirectory(final String... file) {
        final List<String> path = Arrays.stream(file).filter(e -> !StringUtils.isEmptyOrNull(e)).collect(Collectors.toList());
        return StringUtils.join(path, SEPARATOR);
    }

    @Override
    public void createThreadRootDirectory(final String directory) {
        WORKING_DIRECTORY.set(new File(directory));
    }

    @Override
    public void deleteThreadRootDirectory() {
        deleteDirectory(WORKING_DIRECTORY.get());
        WORKING_DIRECTORY.remove();
    }

    @Override
    public void deleteDirectory(final File file) {
        if (Objects.nonNull(file) && file.exists()) {
            log.info("delete directory :: {}", file.getName());
            try {
                FileUtils.deleteDirectory(file);
            } catch (final Exception error) {
                try {
                    FileUtils.deleteQuietly(file);
                    Files.deleteIfExists(Paths.get(file.getPath()));
                } catch (final IOException exception) {
                    throw new FileDeleteException("Could not delete the file", file.getName());
                }
            }
            log.info("deleted directory :: {}", file.getName());
        }
    }

    @Override
    public CompletableFuture<List<Pair<String, File>>> getYamlFiles(final File directory, final String serviceName, final Set<String> profiles) {

        final List<Pair<String, File>> profileToFileList = new LinkedList<>();
        log.info("directory : " + directory.getPath());
        Arrays.stream(Objects.requireNonNull(directory.listFiles())).forEach(file ->
                validateFileAndGetProfile(file, serviceName).ifPresent(profile -> {
                    if (profiles.contains(profile)) {
                        profileToFileList.add(Pair.of(profile, file));
                    }
                })
        );
        log.info("directory copied : " + directory.getPath());
        return CompletableFuture.completedFuture(profileToFileList);
    }

    @Override
    public String convertToFormattedString(final String path, final JsonNode root) {

        String formatString = null;
        try {
            formatString = convertJsonNodeToYAML(root);
        } catch (final Exception parseError) {
            log.error(parseError.getMessage());
        }
        return formatString;
    }

    @Override
    public CompletableFuture<TreeMap<String, File>> getYamlFileTree(final File directory, final String serviceName) {

        final TreeMap<String, File> profileToFileList = new TreeMap<>();
        log.info("directory :: " + directory.getPath());
        Arrays.stream(Objects.requireNonNull(directory.listFiles())).forEach(files ->
                validateFileAndGetProfile(files, serviceName).ifPresent(profile ->
                        profileToFileList.put(profile, files)
                )
        );
        log.info("directory copied :: " + directory.getPath());
        return CompletableFuture.completedFuture(profileToFileList);
    }

    @Override
    public void overWriteFiles(final Map<String, String> fileNameToUpdatedFileContentMap, final File directory) throws IOException {
        final File[] files = Objects.requireNonNull(directory.listFiles());
        for (final Map.Entry<String, String> fileNameToContent : fileNameToUpdatedFileContentMap.entrySet()) {
            for (final File file : files) {
                if (file.getName().equals(fileNameToContent.getKey())) {
                    final FileWriter fw = new FileWriter(file.getAbsoluteFile());
                    final BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(fileNameToContent.getValue());
                    bw.close();
                }
            }
        }
    }


    @Override
    @ThreadDirectory
    public String getFileAsYaml(final ServiceSpec serviceSpec, final String branch, final String profile, final boolean merged) throws JsonProcessingException, ExecutionException, InterruptedException {
        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branch);

        final CompletableFuture<TreeMap<String, File>> serviceProfileToFileMap = getYamlFileTree(
                serviceBranchData.getDirectory(),
                serviceSpec.getService()
        );

        final TreeMap<String, File> treeMap = serviceProfileToFileMap.get();
        if (treeMap.get(profile) == null) {
            log.info("File linked to profile not found");
        }
        if (merged) {
            final JsonNode jsonNode = getMergedYamlFiles(treeMap, serviceBranchData, profile, serviceSpec.getService());
            return convertJsonNodeToYAML(jsonNode);
        } else {
            final File serviceYamlByProfile = treeMap.get(profile);
            if (serviceYamlByProfile == null) {
                throw new ProfileNotSupportedException(serviceSpec.getService(), profile);
            }
            final String fileContents = getFileToString(serviceYamlByProfile);
            return fileContents;
        }
    }

    @ThreadDirectory
    @Override
    public JsonNode getFileAsJson(final ServiceSpec serviceSpec, final String branch, final String profile, final boolean merged) throws ExecutionException, InterruptedException {
        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branch);

        final CompletableFuture<TreeMap<String, File>> serviceProfileToFileMap = getYamlFileTree(
                serviceBranchData.getDirectory(),
                serviceSpec.getService()
        );

        final TreeMap<String, File> treeMap = serviceProfileToFileMap.get();
        if (treeMap.get(profile) == null) {
            log.info("File linked to profile not found");
        }
        final JsonNode jsonNode;
        if (merged) {
            jsonNode = getMergedYamlFiles(treeMap, serviceBranchData, profile, serviceSpec.getService());
        } else {
            final File serviceYamlByProfile = treeMap.get(profile);
            if (serviceYamlByProfile == null) {
                throw new ProfileNotSupportedException(serviceSpec.getService(), profile);
            }
            jsonNode = convertFileToJsonNode(serviceYamlByProfile, false);
        }
        return jsonNode;
    }

    @ThreadDirectory
    @Override
    public List<FileDetail> getFileMapAsJson(final ServiceSpec serviceSpec, final String branch, final String profile) throws ExecutionException, InterruptedException, JsonProcessingException {
        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branch);

        final CompletableFuture<TreeMap<String, File>> serviceProfileToFileMap = getYamlFileTree(
                serviceBranchData.getDirectory(),
                serviceSpec.getService()
        );

        final CompletableFuture<TreeMap<String, File>> applicationProfileToFileMap = getYamlFileTree(
                serviceBranchData.getDirectory(),
                "application"
        );

        final TreeMap<String, File> serviceNameFileMap = serviceProfileToFileMap.get();
        final TreeMap<String, File> applicationFileMap = applicationProfileToFileMap.get();

        final List<FileDetail> FileDetailList = new ArrayList<>();

        final File serviceYamlByProfile = serviceNameFileMap.get(profile);
        final File serviceYaml = serviceNameFileMap.get("");
        final File applicationYamlByProfile = applicationFileMap.get(profile);
        final File applicationYaml = applicationFileMap.get("");

        if (applicationYaml != null) {
            final FileDetail fileDetail = createFileDetail("application", "default", applicationYaml);
            FileDetailList.add(fileDetail);
        }
        if (serviceYaml != null) {
            final FileDetail fileDetail = createFileDetail(serviceSpec.getService(), "default", serviceYaml);
            FileDetailList.add(fileDetail);
        }
        if (applicationYamlByProfile != null) {
            final FileDetail fileDetail = createFileDetail("application", profile, applicationYamlByProfile);
            FileDetailList.add(fileDetail);
        }
        if (serviceYamlByProfile != null) {
            final FileDetail fileDetail = createFileDetail(serviceSpec.getService(), profile, serviceYamlByProfile);
            FileDetailList.add(fileDetail);
        }

        if (FileDetailList.isEmpty()) {
            log.info("Files linked to profile not found");
            throw new ServiceNotFoundException("Service Configs not found", serviceSpec.getService());
        }
        return FileDetailList;
    }

    private FileDetail createFileDetail(final String service, final String profile, final File file) throws JsonProcessingException {
        final JsonNode jsonNode = convertFileToJsonNode(file, false);
        final String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNode);
        final FileDetail fileDetail = FileDetail.builder()
                .service(service)
                .profile(profile)
                .jsonNode(jsonNode)
                .yaml(jsonAsYaml)
                .build();
        return fileDetail;
    }


    @Override
    public JsonNode getMergedYamlFiles(final TreeMap<String, File> serviceProfileToFileMap, final ServiceBranchData serviceBranchData, final String profile, final String service) throws ExecutionException, InterruptedException {
        final CompletableFuture<TreeMap<String, File>> applicationProfileToFileMap = getYamlFileTree(
                serviceBranchData.getDirectory(),
                "application"
        );
        final List<File> files = new ArrayList<>();

        final File serviceYamlByProfile = serviceProfileToFileMap.get(profile);
        final File serviceYaml = serviceProfileToFileMap.get("");
        final File applicationYamlByProfile = applicationProfileToFileMap.get().get(profile);
        final File applicationYaml = applicationProfileToFileMap.get().get("");

        if (applicationYaml != null)
            files.add(applicationYaml);
        if (serviceYaml != null)
            files.add(serviceYaml);
        if (applicationYamlByProfile != null)
            files.add(applicationYamlByProfile);
        if (serviceYamlByProfile != null)
            files.add(serviceYamlByProfile);

        if (files.isEmpty())
            throw new ServiceNotFoundException("Service Configs not found", service);

        final List<JsonNode> jsonNodes = new ArrayList<>();

        for (final File file : files) {
            jsonNodes.add(convertFileToJsonNode(file, false));
        }

        final JsonNode mergedYamlAsJsonNode = mergeYaml(jsonNodes);
        return mergedYamlAsJsonNode;
    }

    public String getFileToString(final File file) {
        final StringBuilder sb = new StringBuilder();
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(file, "UTF-8");
            while (it.hasNext()) {
                final String line = it.nextLine();
                sb.append(line).append("\n");
            }
        } catch (final Exception exception) {
            log.error(exception.getMessage());
        } finally {
            if (it != null) LineIterator.closeQuietly(it);
        }
        return sb.toString();
    }

    private Optional<String> validateFileAndGetProfile(final File file, final String serviceName) {
        if (file.isFile() && file.getName().startsWith(serviceName)) {
            return extractProfileName(file.getName(), serviceName);
        }
        return Optional.empty();
    }

    private Optional<String> extractProfileName(final String filename, final String serviceName) {

        final String expectedProfile = filename.substring(serviceName.length(), filename.lastIndexOf("."));
        if (!expectedProfile.isEmpty() && expectedProfile.charAt(0) == '-') {
            return Optional.of(expectedProfile.substring(1));
        } else if (expectedProfile.isEmpty()) {
            return Optional.of(expectedProfile);
        }
        return Optional.empty();
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

    private JsonNode mergeYaml(final List<JsonNode> jsonNodes) {
        final JsonNode mergedJsonNode = jsonNodes.get(0);
        for (int i = 1; i < jsonNodes.size(); i++) {
            merge(mergedJsonNode, jsonNodes.get(i));
        }
        return mergedJsonNode;
    }

    private JsonNode merge(final JsonNode mainNode, final JsonNode updateNode) {
        final Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {
            final String updateNodeFieldName = fieldNames.next();
            final JsonNode nodeFromMainNode = mainNode.get(updateNodeFieldName);
            final JsonNode nodeFromUpdateNode = updateNode.get(updateNodeFieldName);
            // If the node is an @ArrayNode replace existing Array Node property in main node
            if (nodeFromMainNode != null && nodeFromMainNode.isArray() &&
                    nodeFromUpdateNode.isArray()) {
                if (mainNode instanceof ObjectNode) {
                    ((ObjectNode) mainNode).replace(updateNodeFieldName, nodeFromUpdateNode);
                }
                // if the Node is an @ObjectNode
            } else if (nodeFromMainNode != null && nodeFromMainNode.isObject()) {
                merge(nodeFromMainNode, nodeFromUpdateNode);//
            }
            //Else if node is a property node then we simply replace, or if main node doesn't exist then we simply add
            else {
                if (mainNode instanceof ObjectNode) {
                    ((ObjectNode) mainNode).replace(updateNodeFieldName, nodeFromUpdateNode);
                }
            }
        }
        return mainNode;
    }

    private String convertJsonNodeToYAML(final JsonNode jsonNode) throws JsonProcessingException {
        final String yaml = new YAMLMapper()
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .writeValueAsString(jsonNode);

        return yaml;
    }


}
