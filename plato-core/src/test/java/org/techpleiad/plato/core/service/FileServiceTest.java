package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    private static final String PATH = "classpath:validation/repo/dev/";
    private static final String SERVICE_NAME = "custom-manager";

    @InjectMocks
    private FileService fileService;

    //@Test //TODO
    void givenLocalDirectoryPath_whenGenerateFileFromLocalDirectoryPath_thenGenerateFile() {

        //when directory given
        Assertions.assertNotNull(fileService.generateFileFromLocalDirectoryPath("as", "as"));

        //when directory empty
        Assertions.assertNotNull(fileService.generateFileFromLocalDirectoryPath());
    }

    @Test
    void givenFile_whenDeleteDirectory_thenDeleteFile() {

        final File file = Mockito.spy(new File("temp"));
        Mockito.when(file.exists()).thenReturn(true);
        Assertions.assertDoesNotThrow(() -> fileService.deleteDirectory(file));
    }

    @Test
    void givenDirectoryServiceNameProfiles_whenGetYamlFiles_thenReturnJsonNodeProfileFilesList() throws FileNotFoundException, ExecutionException, InterruptedException {

        final File directory = ResourceUtils.getFile(PATH);

        final Set<String> profiles = new HashSet<>(Arrays.asList("dev", "prod", "test"));

        final CompletableFuture<List<Pair<String, File>>> profileToFileList = fileService.getYamlFiles(directory, SERVICE_NAME);
        Assertions.assertEquals(4, profileToFileList.get().size());
        for (final Pair<String, File> item : profileToFileList.get()) {
            Assertions.assertNotNull(item.getSecond());
            Assertions.assertNotNull(item.getFirst());
        }

        final CompletableFuture<List<Pair<String, File>>> profileToFileListEmpty = fileService.getYamlFiles(directory, "custom-mana");
        Assertions.assertTrue(profileToFileListEmpty.get().isEmpty());
    }

    @Test
    void givenJsonNode_whenConvertToFormattedString_thenReturnFormattedYaml() throws IOException {

        final JsonNode jsonNode = new YAMLMapper()
                .readTree(ResourceUtils.getFile(PATH + SERVICE_NAME + "-dev.yml"));

        Assertions.assertNotNull(fileService.convertToFormattedString("as", jsonNode));
    }

    @Test
    void givenDirectoryServiceNameProfiles_whenGetYamlFiles_thenReturnProfileFilesList() throws FileNotFoundException, ExecutionException, InterruptedException {

        final File directory = ResourceUtils.getFile(PATH);
        final CompletableFuture<TreeMap<String, File>> profileToFileList = fileService.getYamlFileTree(directory, SERVICE_NAME);

        Assertions.assertEquals(4, profileToFileList.get().size());
        profileToFileList.get().forEach((key, value) -> {
            System.out.println("profile - '" + key + "' > file - " + value.getName());
            Assertions.assertNotNull(value);
            Assertions.assertNotNull(key);
        });
    }
}
