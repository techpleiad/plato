package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.convert.SortingNodeFactory;
import org.techpleiad.plato.core.domain.*;
import org.techpleiad.plato.core.exceptions.BranchNotSupportedException;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IFilterSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetAlteredPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossBranchUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossProfileUseCase;
import org.techpleiad.plato.core.port.in.IValidateBranchUseCase;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ValidateService implements IValidateAcrossProfileUseCase, IValidateBranchUseCase, IValidateAcrossBranchUseCase {

    @Autowired
    private IFileServiceUserCase fileService;

    @Autowired
    private IGitServiceUseCase gitService;

    @Autowired
    private IGetSuppressPropertyUseCase getSuppressPropertyUseCase;

    @Autowired
    private IGetAlteredPropertyUseCase getAlteredPropertyUseCase;

    @Autowired
    private IFilterSuppressPropertyUseCase filterSuppressPropertyUseCase;

    @Setter
    private Map<String, HashSet<String>> globalObjectProperty;
    private Map<String, String> propertyToAlteredProperty;

    @Override
    public ConsistencyAcrossBranchesReport validateProfilesAcrossBranch(
            final ServiceBranchData fromServiceBranchData,
            final ServiceBranchData toServiceBranchData,
            final ServiceSpec serviceSpec,
            final boolean checkPropertyValue) throws ExecutionException, InterruptedException {

        final List<ServiceBranchData> serviceBranchList = Arrays.asList(fromServiceBranchData, toServiceBranchData);

        final HashMap<String, TreeMap<String, File>> branchToProfileMap = new HashMap<>();
        for (final ServiceBranchData serviceBranch : serviceBranchList) {
            final CompletableFuture<TreeMap<String, File>> profileToFileMap = fileService.getYamlFiles(
                    serviceBranch.getDirectory(),
                    serviceSpec.getService()
            );
            branchToProfileMap.put(serviceBranch.getBranch(), profileToFileMap.get());
        }

        final String fromBranch = serviceBranchList.get(0).getBranch();
        final String toBranch = serviceBranchList.get(1).getBranch();

        final List<BranchProfileReport> branchProfileReportList = new LinkedList<>();

        serviceSpec.getProfiles().forEach(profile -> {
            final File fromFile = branchToProfileMap.get(fromBranch).get(profile.getName());
            final File toFile = branchToProfileMap.get(toBranch).get(profile.getName());

            if (Objects.isNull(fromFile) || Objects.isNull(toFile)) {
                log.error("from file {}, toFile {}", fromFile, toFile);
            } else {
                final String fromOriginal = fileService.getFileToString(fromFile);
                final String toOriginal = fileService.getFileToString(toFile);

                final JsonNode sortFromOriginal = convertFileToSortedJsonNode(fromFile);
                final JsonNode sortToOriginal = convertFileToSortedJsonNode(toFile);

                final List<Document> documentList = Arrays.asList(
                        Document.builder().profile(profile.getName()).branch(fromBranch).build(),
                        Document.builder().profile(profile.getName()).branch(toBranch).build()
                );
                final boolean fileEqual = fromOriginal.equals(toOriginal);

                final Boolean propertyValueEqual = checkPropertyValue ?
                        Objects.nonNull(sortFromOriginal) &&
                        Objects.nonNull(sortToOriginal) &&
                        sortFromOriginal.toString().equals(sortToOriginal.toString())
                        : null;


                branchProfileReportList.add(
                        BranchProfileReport.builder()
                                .propertyValueEqual(propertyValueEqual)
                                .profile(profile.getName())
                                .documents(documentList)
                                .fileEqual(fileEqual)
                                .build()
                );
            }
        });

        return ConsistencyAcrossBranchesReport
                .builder()
                .service(serviceSpec.getService())
                .report(branchProfileReportList)
                .build();
    }

    @ThreadDirectory
    @Override
    public List<ConsistencyAcrossBranchesReport> validateAcrossBranchesInServiceBatch(
            final List<ServiceSpec> serviceSpecList,
            final ValidationAcrossBranchConfig validationAcrossBranchConfig) throws ExecutionException, InterruptedException {

        final List<ConsistencyAcrossBranchesReport> reportList = new LinkedList<>();
        final List<String> branchList = Arrays.asList(validationAcrossBranchConfig.getFromBranch(), validationAcrossBranchConfig.getToBranch());

        serviceSpecList.forEach(serviceSpec -> validateBranchesInService(serviceSpec, branchList));


        final Map<ServiceBranchData, ServiceBranchData> mapServiceBranchToRepository = gitService.cloneGitRepositoryByBranchInBatchAsync(
                serviceSpecList.stream().map(ServiceSpec::getGitRepository).collect(Collectors.toList()),
                branchList
        );

        for (final ServiceSpec serviceSpec : serviceSpecList) {
            final List<ServiceBranchData> serviceBranchList = new ArrayList<>();
            for (final String branch : branchList) {
                final ServiceBranchData data = ServiceBranchData.builder().repository(serviceSpec.getGitRepository().getUrl()).branch(branch).build();

                serviceBranchList.add(ServiceBranchData.builder().repository(data.getRepository())
                        .branch(data.getBranch())
                        .directory(mapServiceBranchToRepository.get(data).getDirectory())
                        .build()
                );
            }
            final ConsistencyAcrossBranchesReport report = validateProfilesAcrossBranch(serviceBranchList.get(0), serviceBranchList.get(1), serviceSpec, validationAcrossBranchConfig.isPropertyValueEqual());
            reportList.add(report);
        }
        return reportList;
    }

    @ThreadDirectory
    @Override
    public List<ConsistencyAcrossProfilesReport> validateAcrossProfilesInServiceBatch(
            final List<ServiceSpec> serviceSpecList,
            final String branchName,
            final boolean isSuppressed) throws ExecutionException, InterruptedException {

        serviceSpecList.forEach(serviceSpec -> validateBranchInService(serviceSpec, branchName));

        final List<ConsistencyAcrossProfilesReport> reportList = new LinkedList<>();

        final Map<ServiceBranchData, ServiceBranchData> mapServiceBranchToRepository = gitService.cloneGitRepositoryByBranchInBatchAsync(
                serviceSpecList.stream().map(ServiceSpec::getGitRepository).collect(Collectors.toList()),
                Arrays.asList(branchName)
        );

        for (final ServiceSpec serviceSpec : serviceSpecList) {

            final ServiceBranchData data = ServiceBranchData.builder().repository(serviceSpec.getGitRepository().getUrl()).branch(branchName).build();
            final CompletableFuture<List<Pair<String, File>>> mapProfileToFileContent = fileService.getYamlFiles(
                    mapServiceBranchToRepository.get(data).getDirectory(),
                    serviceSpec.getService(),
                    serviceSpec.getProfiles().stream().map(Profile::getName).collect(Collectors.toCollection(TreeSet::new))
            );

            final Map<String, List<String>> suppressedPropertiesMap = isSuppressed ?
                    Collections.emptyMap() : getSuppressPropertyUseCase.getSuppressedProperties(serviceSpec.getService());

            final List<String> alteredProperties = getAlteredPropertyUseCase.getAlteredProperties(serviceSpec.getService());
            final ConsistencyAcrossProfilesReport report = this.validateYamlKeyInFiles(
                    mapProfileToFileContent.get(), suppressedPropertiesMap,
                    alteredProperties
            );

            report.setService(serviceSpec.getService());
            reportList.add(report);
        }

        return reportList;
    }

    @Override
    public ConsistencyAcrossProfilesReport validateYamlKeyInFiles(
            final List<Pair<String, File>> mapProfileToFileContent,
            final Map<String, List<String>> suppressedProperties,
            final List<String> alteredProperties) {

        final PropertyTreeNode alteredPropertyTree = PropertyTreeNode.convertPropertiesToPropertyTree(alteredProperties);

        this.globalObjectProperty = buildGlobalObjectToPropertiesMapping(mapProfileToFileContent, alteredPropertyTree);

        final ConsistencyAcrossProfilesReport profilePropertyDetails = ConsistencyAcrossProfilesReport.builder().build();

        final List<String> commonSuppressedProperties = suppressedProperties.getOrDefault("common", Collections.emptyList());
        mapProfileToFileContent.forEach(pairProfileFile -> {

            final JsonNode rootNode = convertFileToJsonNode(pairProfileFile.getValue());
            profilePropertyDetails.addProfileDocument(pairProfileFile.getKey(),
                    fileService.convertToFormattedString(pairProfileFile.getKey(), rootNode));

            final List<String> missingProperties = new LinkedList<>();
            findMissingProfileProperties(rootNode, alteredPropertyTree,
                    missingProperties, "", "",
                    false);

            final List<String> suppressedPropertiesForProfile = suppressedProperties.getOrDefault(pairProfileFile.getKey(), new ArrayList<>());
            suppressedPropertiesForProfile.addAll(commonSuppressedProperties);
            profilePropertyDetails.addProfileToMissingProperties(
                    pairProfileFile.getKey(),
                    filterSuppressPropertyUseCase.filterSuppressedProperties(suppressedPropertiesForProfile, missingProperties)
            );
        });

        return profilePropertyDetails;
    }

    @ExecutionTime
    public void findMissingProfileProperties(final JsonNode rootNode, final PropertyTreeNode alteredPropertyTreeNode,
                                             final List<String> missingProperties,
                                             final String pathRegex, final String path,
                                             final boolean isPropertyArray) {

        final String alteredRegexPath = propertyToAlteredProperty.get(pathRegex);

        // If object, find the missing properties
        final boolean getObjectMissingProperties =
                rootNode.isObject() &&
                        (alteredPropertyTreeNode == null || !alteredPropertyTreeNode.contains("*"));

        if (getObjectMissingProperties || isPropertyArray) {
            final Set<String> propertySet = new HashSet<>();
            rootNode.fields().forEachRemaining(property -> propertySet.add(property.getKey()));

            globalObjectProperty.get(alteredRegexPath)
                    .stream()
                    .filter(property -> !propertySet.contains(property))
                    .forEach(property -> addErrorProfileNotification(missingProperties, path, property));
        } else if (isJsonNodeValueOrNull(rootNode) && globalObjectProperty.containsKey(alteredRegexPath)) {
            globalObjectProperty.get(alteredRegexPath).forEach(property ->
                    addErrorProfileNotification(missingProperties, path, property)
            );
        }

        rootNode.fields().forEachRemaining(object -> {
            final JsonNode childRootNode = object.getValue();
            final String key = object.getKey();

            if (childRootNode.isObject()) {
                findMissingProfileProperties(childRootNode,
                        Objects.isNull(alteredPropertyTreeNode) ? null : alteredPropertyTreeNode.getChild("*", key),
                        missingProperties,
                        generatePropertyPath(alteredRegexPath, key),
                        generatePropertyPath(path, key),
                        false);
            } else if (childRootNode.isArray()) {
                traverseJsonArrayElements(childRootNode, missingProperties,
                        alteredPropertyTreeNode,
                        alteredRegexPath, path,
                        key);
            }

            // Not an object or an array JsonNode
            else {
                final Pair<String, String> objectPathPrefix = getObjectPropertyToMissingPropertiesPair(alteredRegexPath, key);
                if (Objects.nonNull(objectPathPrefix) && globalObjectProperty.containsKey(objectPathPrefix.getKey())) {

                    globalObjectProperty.get(objectPathPrefix.getKey()).forEach(missingProperty ->
                            addErrorProfileNotification(missingProperties, objectPathPrefix.getValue(), missingProperty)
                    );
                }
            }
        });
    }

    @ExecutionTime
    public Map<String, HashSet<String>> buildGlobalObjectToPropertiesMapping(final List<Pair<String, File>> fileContentMap,
                                                                             final PropertyTreeNode alteredPropertyTree) {

        propertyToAlteredProperty = new HashMap<>();
        propertyToAlteredProperty.put("", "");

        final HashMap<String, HashSet<String>> objectPropertyMap = new HashMap<>();
        fileContentMap.forEach(pairProfileFile ->
                traverseObjectToPropertiesMapping(convertFileToJsonNode(pairProfileFile.getValue()), objectPropertyMap,
                        alteredPropertyTree, "", false)
        );
        return objectPropertyMap;
    }

    @Override
    public boolean validateBranchesInService(final ServiceSpec serviceSpec, final List<String> branchList) {
        final Set<String> branchSet = serviceSpec.getBranches()
                .stream()
                .map(Branch::getName)
                .collect(Collectors.toSet());

        final List<String> missingBranches = branchList
                .stream()
                .filter(branch -> !branchSet.contains(branch))
                .collect(Collectors.toList());
        if (missingBranches.isEmpty()) {
            return true;
        }
        throw new BranchNotSupportedException(
                serviceSpec.getService(),
                missingBranches
        );
    }

    @Override
    public boolean validateBranchInService(final ServiceSpec serviceSpec, final String branch) {

        final boolean validBranch = serviceSpec.getBranches()
                .stream()
                .anyMatch(x -> x.getName().equals(branch));

        if (!validBranch) {
            throw new BranchNotSupportedException(
                    serviceSpec.getService(),
                    Arrays.asList(branch)
            );
        }
        return true;
    }

    private void traverseObjectToPropertiesMapping(final JsonNode rootNode,
                                                   final HashMap<String, HashSet<String>> objectPropertyMap,
                                                   final PropertyTreeNode alteredPropertyRoot,
                                                   final String prefix,
                                                   final boolean isPropertyDefaultArray) {

        final boolean getObjectPropertiesMapped = rootNode.isObject() && (alteredPropertyRoot == null || !alteredPropertyRoot.contains("*"));

        if (getObjectPropertiesMapped || isPropertyDefaultArray) {

            objectPropertyMap.putIfAbsent(prefix, new HashSet<>());
            final Set<String> propertySet = objectPropertyMap.get(prefix);

            rootNode.fields().forEachRemaining(property -> propertySet.add(property.getKey()));
        }

        rootNode.fields().forEachRemaining(object -> {
            final JsonNode value = object.getValue();
            final String key = object.getKey();

            PropertyTreeNode childNode = null;
            boolean isPropertyAltered = false;
            if (Objects.nonNull(alteredPropertyRoot)) {
                if (alteredPropertyRoot.contains("*")) {
                    isPropertyAltered = true;
                }
                childNode = alteredPropertyRoot.getChild("*", key);
            }

            if (value.isObject()) {
                final String alterRegexPath = generatePropertyPath(prefix, isPropertyAltered ? "*" : key);

                propertyToAlteredProperty.put(generatePropertyPath(prefix, key), alterRegexPath);
                traverseObjectToPropertiesMapping(value, objectPropertyMap, childNode, alterRegexPath, false);
            } else if (value.isArray()) {
                final String regexPath = generatePropertyPath(prefix, key, "*");
                propertyToAlteredProperty.put(regexPath, regexPath);

                final PropertyTreeNode finalChildNode = childNode;
                value.forEach(subRoot -> traverseObjectToPropertiesMapping(subRoot, objectPropertyMap, finalChildNode, regexPath, true));
            }
        });
    }

    private boolean isJsonNodeValueOrNull(final JsonNode rootNode) {
        return !rootNode.isObject() && !rootNode.isArray();
    }

    private Pair<String, String> getObjectPropertyToMissingPropertiesPair(final String path, final String key) {

        final String keyObject = generatePropertyPath(path, key);
        final String keyGeneric = generatePropertyPath(path, "*");
        final String keyArray = generatePropertyPath(keyObject, "*");

        String property = null;
        String object = null;
        if (globalObjectProperty.containsKey(keyGeneric)) {
            property = keyObject;
            object = keyGeneric;
        }
        if (globalObjectProperty.containsKey(keyObject)) {
            property = keyObject;
            object = keyObject;
        }
        if (globalObjectProperty.containsKey(keyArray)) {
            property = keyArray;
            object = keyArray;
        }

        return Objects.isNull(object) ? null : Pair.<String, String>builder().key(object).value(property).build();
    }

    private void traverseJsonArrayElements(final JsonNode rootNode, final List<String> missingProperties,
                                           final PropertyTreeNode alteredPropertyTreeNode, final String alteredRegexPath,
                                           final String path, final String key) {
        final AtomicInteger index = new AtomicInteger();
        rootNode.forEach(subRoot -> {
            findMissingProfileProperties(subRoot,
                    Objects.isNull(alteredPropertyTreeNode) ? null : alteredPropertyTreeNode.getChild(key),
                    missingProperties,
                    generatePropertyPath(alteredRegexPath, key, "*"),
                    generatePropertyPath(path, key, Integer.toString(index.get())),
                    true);
            index.getAndIncrement();
        });
    }

    private void addErrorProfileNotification(final List<String> missingProperties, final String path, final String property) {
        missingProperties.add(generatePropertyPath(path, property));
    }

    private String generatePropertyPath(final String... property) {
        return Arrays.stream(property).filter(x -> !x.isEmpty()).collect(Collectors.joining("."));
    }

    private JsonNode convertFileToSortedJsonNode(final File file) {
        final ObjectMapper sortedMapper = JsonMapper.builder().nodeFactory(new SortingNodeFactory()).build();
        try {
            return sortedMapper.readTree(convertFileToJsonNode(file).toString());
        } catch (final Exception exception) {
            log.error(exception.getMessage());
        }
        return null;
    }

    private JsonNode convertFileToJsonNode(final File file) {
        JsonNode root = null;
        try {
            root = file.getName().endsWith(".yml") ?
                    new YAMLMapper().readTree(file) : new ObjectMapper().readTree(file);
        } catch (final Exception exception) {
            log.error(exception.getMessage());
        }
        return root;
    }
}
