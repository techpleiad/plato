package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.convert.SortingNodeFactory;
import org.techpleiad.plato.core.domain.PropertyNodeDetail;
import org.techpleiad.plato.core.domain.PropertyTreeNode;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.FileConvertException;
import org.techpleiad.plato.core.port.in.ICustomValidateUseCase;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IGetAlteredPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomValidateService implements ICustomValidateUseCase {

    @Autowired
    private IGitServiceUseCase gitService;

    @Autowired
    private IFileServiceUserCase fileService;

    @Autowired
    private IGetAlteredPropertyUseCase getAlteredPropertyUseCase;

    @ThreadDirectory
    @Override
    public Map<String, List<JsonNode>> customValidateYamlFile(final ServiceSpec serviceSpec, final String service, final String branch, final String profile) throws ExecutionException, InterruptedException {
        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branch);

        final CompletableFuture<TreeMap<String, File>> profileToFileMap = fileService.getYamlFiles(
                serviceBranchData.getDirectory(),
                serviceSpec.getService()
        );
        final File yamlByProfile = profileToFileMap.get().get(profile);
        log.info(yamlByProfile.getName());
        final List<String> alteredProperties = getAlteredPropertyUseCase.getAlteredProperties(serviceSpec.getService());
        final PropertyTreeNode alteredPropertyTree = PropertyTreeNode.convertPropertiesToPropertyTree(alteredProperties);
        JsonNode yamlAsJsonNode = convertFileToJsonNode(yamlByProfile, false);
        Map<String, List<JsonNode>> yamlPropertyToJsonNodeList = traverseObjectToJsonNodeMapping(yamlAsJsonNode, alteredPropertyTree);
        return yamlPropertyToJsonNodeList;
    }


    @ExecutionTime
    public Map<String, List<JsonNode>> traverseObjectToJsonNodeMapping(final JsonNode rootNode, final PropertyTreeNode alteredPropertyRoot) {

        TreeMap<String, List<Pair<String, JsonNode>>> treeMap = new TreeMap<>();

        final Queue<PropertyNodeDetail> queue = new LinkedList<>();
        queue.add(PropertyNodeDetail.builder().alteredPropertyRoot(alteredPropertyRoot)
                .rootNode(rootNode).pathRegex("")
                .isPropertyArray(false)
                .build()
        );

        while (!queue.isEmpty()) {
            final PropertyNodeDetail propertyNodeDetail = queue.remove();

            final boolean getObjectPropertiesMapped = propertyNodeDetail.getRootNode().isObject() && (propertyNodeDetail.getAlteredPropertyRoot() == null || !propertyNodeDetail
                    .getAlteredPropertyRoot().contains("*"));

            if (getObjectPropertiesMapped || propertyNodeDetail.isPropertyArray()) {

                treeMap.putIfAbsent(propertyNodeDetail.getPathRegex(), new ArrayList<>());

                final List<Pair<String, JsonNode>> jsonNodeList = treeMap.get(propertyNodeDetail.getPathRegex());

                propertyNodeDetail.getRootNode().fields().forEachRemaining(property -> jsonNodeList.add(Pair.of(property.getKey(), property.getValue())));
            }

            propertyNodeDetail.getRootNode().fields().forEachRemaining(object -> {
                final JsonNode value = object.getValue();
                final String key = object.getKey();

                PropertyTreeNode childNode = null;
                boolean isPropertyAltered = false;

                if (Objects.nonNull(propertyNodeDetail.getAlteredPropertyRoot())) {
                    if (propertyNodeDetail.getAlteredPropertyRoot().contains("*")) {
                        isPropertyAltered = true;
                    }
                    childNode = propertyNodeDetail.getAlteredPropertyRoot().getChild("*", key);
                }

                if (value.isObject()) {
                    final String alterRegexPath = generatePropertyPath(propertyNodeDetail.getPathRegex(), isPropertyAltered ? "*" : key);

                    queue.add(PropertyNodeDetail.builder().alteredPropertyRoot(childNode)
                            .rootNode(value).pathRegex(alterRegexPath)
                            .isPropertyArray(false)
                            .build()
                    );
                } else if (value.isArray()) {
                    final String regexPath = generatePropertyPath(propertyNodeDetail.getPathRegex(), key, "*");

                    final PropertyTreeNode finalChildNode = childNode;
                    value.forEach(subRoot ->
                            queue.add(PropertyNodeDetail.builder().alteredPropertyRoot(finalChildNode)
                                    .rootNode(subRoot).pathRegex(regexPath)
                                    .isPropertyArray(true)
                                    .build()
                            ));
                }
            });
        }
        Map<String, List<JsonNode>> mapTree = mapTree(treeMap);
        return mapTree;
    }

    private Map<String, List<JsonNode>> mapTree(Map<String, List<Pair<String, JsonNode>>> originalTree) {
        Map<String, List<JsonNode>> newTree = new TreeMap<>();
        for (Map.Entry<String, List<Pair<String, JsonNode>>> entry : originalTree.entrySet()) {
            for (Pair<String, JsonNode> pair : entry.getValue()) {
                List<JsonNode> jsonNodes = newTree.get(entry.getKey() + "." + pair.getFirst());
                if (jsonNodes == null) {
                    ArrayList<JsonNode> jsonNodeArrayList = new ArrayList<>();
                    jsonNodeArrayList.add(pair.getSecond());
                    newTree.put(entry.getKey() + "." + pair.getFirst(), jsonNodeArrayList);
                } else {
                    jsonNodes.add(pair.getSecond());
                }
            }
        }
        return newTree;
    }

    private String generatePropertyPath(final String... property) {
        return Arrays.stream(property).filter(x -> !x.isEmpty()).collect(Collectors.joining("."));
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
