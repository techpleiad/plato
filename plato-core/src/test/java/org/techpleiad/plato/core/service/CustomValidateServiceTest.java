package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;
import org.techpleiad.plato.core.convert.SortingNodeFactory;
import org.techpleiad.plato.core.domain.PropertyTreeNode;
import org.techpleiad.plato.core.exceptions.FileConvertException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomValidateServiceTest {

    @InjectMocks
    private CustomValidateService customValidateService;

    private static final String DEV = "dev";
    private static final String PATH = "classpath:validation/";
    private PropertyTreeNode alteredPropertyTree;
    private JsonNode jsonNode;
    private HashMap<String, HashSet<String>> objectPropertyMap = new HashMap<>();

    private void init(final int profileTestIndex, final List<String> alteredProperties) throws IOException {
        File file = getFileContent(
                "global-missing-alter/profile-" + DEV +
                        "-" + profileTestIndex +
                        ".yml");
        jsonNode = convertFileToJsonNode(file, false);
        alteredPropertyTree = PropertyTreeNode.convertPropertiesToPropertyTree(alteredProperties);
    }

    private File getFileContent(final String fileName) throws IOException {
        return ResourceUtils.getFile(PATH + fileName);
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
}