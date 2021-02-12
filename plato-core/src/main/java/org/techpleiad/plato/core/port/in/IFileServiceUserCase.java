package org.techpleiad.plato.core.port.in;

import com.fasterxml.jackson.databind.JsonNode;
import org.techpleiad.plato.core.domain.Pair;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public interface IFileServiceUserCase {

    File generateFileFromLocalDirectoryPath(String... file);

    String generateDirectory(String... file);

    void deleteDirectory(File file);

    String convertToFormattedString(String path, JsonNode root);

    CompletableFuture<List<Pair<String, File>>> getYamlFiles(File directory, String serviceName, Set<String> profiles);

    CompletableFuture<TreeMap<String, File>> getYamlFiles(File directory, String serviceName);

    String getFileToString(final File file);
}
