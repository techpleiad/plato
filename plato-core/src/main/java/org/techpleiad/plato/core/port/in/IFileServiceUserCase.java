package org.techpleiad.plato.core.port.in;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.util.Pair;
import org.techpleiad.plato.core.domain.ServiceBranchData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface IFileServiceUserCase {

    File generateFileFromLocalDirectoryPath(String... file);

    String generateDirectory(String... file);

    void deleteDirectory(File file);

    String convertToFormattedString(String path, JsonNode root);

    CompletableFuture<List<Pair<String, File>>> getYamlFiles(File directory, String serviceName, Set<String> profiles);

    CompletableFuture<TreeMap<String, File>> getYamlFileTree(File directory, String serviceName);

    String getFileToString(final File file);

    void overWriteFiles(Map<String, String> fileNameToUpdatedFileContentMap, File directory) throws IOException;

    JsonNode getMergedYamlFiles(TreeMap<String, File> serviceProfileToFileMap, ServiceBranchData serviceBranchData, String profile, String service) throws ExecutionException, InterruptedException;

}
