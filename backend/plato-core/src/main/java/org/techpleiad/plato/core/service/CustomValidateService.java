package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.domain.CustomValidateInBatchReport;
import org.techpleiad.plato.core.domain.CustomValidateReport;
import org.techpleiad.plato.core.domain.PropertyNodeDetail;
import org.techpleiad.plato.core.domain.PropertyTreeNode;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.port.in.ICustomValidateUseCase;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IGetAlteredPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;
import org.techpleiad.plato.core.port.in.IGetValidationRuleUseCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
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
    private IGetServiceUseCase getServiceUseCase;

    @Autowired
    private IFileServiceUserCase fileService;

    @Autowired
    private IGetValidationRuleUseCase getValidationRuleUseCase;

    @Autowired
    private IGetAlteredPropertyUseCase getAlteredPropertyUseCase;

    @Override
    @ThreadDirectory
    public List<CustomValidateInBatchReport> customValidateInBatch(List<String> services, List<String> branches, List<String> profiles) throws ExecutionException, InterruptedException {
        List<CustomValidateInBatchReport> customValidateInBatchReports = new ArrayList<>();
        for (String service : services) {
            for (String branch : branches) {
                for (String profile : profiles) {
                    List<CustomValidateReport> customValidateReportList = customValidate(service, branch, profile);
                    CustomValidateInBatchReport customValidateInBatchReport = CustomValidateInBatchReport.builder()
                            .service(service)
                            .branch(branch)
                            .profile(profile)
                            .customValidateReportList(customValidateReportList)
                            .build();
                    customValidateInBatchReports.add(customValidateInBatchReport);
                }
            }
        }
        return customValidateInBatchReports;
    }

    private List<CustomValidateReport> customValidate(String service, String branch, String profile) throws ExecutionException, InterruptedException {
        ServiceSpec serviceSpec = getServiceUseCase.getService(service);
        Map<String, List<JsonNode>> yamlPropertyToJsonNodeList = getYamlPropertyToJsonNodeList(serviceSpec, branch, profile);

        Map<String, ValidationRule> validationRuleMap = getValidationRuleUseCase
                .getValidationRuleMapByScope(service, branch, profile);

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

        List<CustomValidateReport> responseList = new ArrayList<>();

        for (Map.Entry<String, ValidationRule> validationRuleEntry : validationRuleMap.entrySet()) {
            if (yamlPropertyToJsonNodeList.get(validationRuleEntry.getKey()) != null) {
                List<JsonNode> jsonNodes = yamlPropertyToJsonNodeList.get(validationRuleEntry.getKey());
                for (JsonNode jsonNode : jsonNodes) {
                    JsonSchema schemaToValidationJsonSchema = factory.getSchema(validationRuleEntry.getValue().getRule());
                    Set<ValidationMessage> errors = schemaToValidationJsonSchema.validate(jsonNode);
                    if (!errors.isEmpty()) {
                        responseList.add(
                                CustomValidateReport.builder()
                                        .property(validationRuleEntry.getKey())
                                        .value(jsonNode)
                                        .validationMessages(errors.stream().map(ValidationMessage::getMessage).collect(Collectors.toSet()))
                                        .build()
                        );
                    }
                }
            }
        }
        return responseList;
    }


    private Map<String, List<JsonNode>> getYamlPropertyToJsonNodeList(final ServiceSpec serviceSpec, final String branch, final String profile) throws ExecutionException, InterruptedException {
        final ServiceBranchData serviceBranchData = gitService.cloneGitRepositoryByBranchAsync(serviceSpec.getGitRepository(), branch);

        final CompletableFuture<TreeMap<String, File>> serviceProfileToFileMap = fileService.getYamlFileTree(
                serviceBranchData.getDirectory(),
                serviceSpec.getService()
        );

        JsonNode mergedYamlAsJsonNode = fileService.getMergedYamlFiles(serviceProfileToFileMap.get(), serviceBranchData, profile, serviceSpec.getService());

        final List<String> alteredProperties = getAlteredPropertyUseCase.getAlteredProperties(serviceSpec.getService());
        final PropertyTreeNode alteredPropertyTree = PropertyTreeNode.convertPropertiesToPropertyTree(alteredProperties);
        Map<String, List<JsonNode>> yamlPropertyToJsonNodeList = traverseObjectToJsonNodeMapping(mergedYamlAsJsonNode, alteredPropertyTree);
        return yamlPropertyToJsonNodeList;
    }


    @ExecutionTime
    private Map<String, List<JsonNode>> traverseObjectToJsonNodeMapping(final JsonNode rootNode, final PropertyTreeNode alteredPropertyRoot) {

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
}
