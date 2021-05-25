package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.exceptions.FileDeleteException;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IFileThreadServiceUseCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService implements IFileServiceUserCase, IFileThreadServiceUseCase {

    public static final ThreadLocal<File> WORKING_DIRECTORY = new ThreadLocal<>();

    private static final String SEPARATOR = "/";

    @Override
    public File generateFileFromLocalDirectoryPath(final String... files) {
        final List<String> path = Arrays.stream(files).filter(e -> !StringUtils.isEmptyOrNull(e)).collect(Collectors.toList());
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
        Arrays.stream(Objects.requireNonNull(directory.listFiles())).forEach(files ->
                validateFileAndGetProfile(files, serviceName).ifPresent(profile -> {
                    if (profiles.contains(profile)) {
                        profileToFileList.add(Pair.of(profile, files));
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
            formatString = new YAMLMapper().writeValueAsString(root);
            formatString = formatString.replace("\"", "");
        } catch (final Exception parseError) {
            log.error(parseError.getMessage());
        }
        return formatString;
    }

    @Override
    public CompletableFuture<TreeMap<String, File>> getYamlFiles(final File directory, final String serviceName) {

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
    public void overWriteFiles(Map<String, String> fileNameToUpdatedFileContentMap, File directory) throws IOException {
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
}
