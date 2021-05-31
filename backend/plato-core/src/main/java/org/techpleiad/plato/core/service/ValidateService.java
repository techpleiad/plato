package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.advice.ThreadDirectory;
import org.techpleiad.plato.core.convert.SortingNodeFactory;
import org.techpleiad.plato.core.domain.*;
import org.techpleiad.plato.core.exceptions.BranchNotSupportedException;
import org.techpleiad.plato.core.exceptions.FileConvertException;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IFilterSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetAlteredPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;
import org.techpleiad.plato.core.port.in.IValidateAcrossBranchConsistencyLevelUseCase;
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
public class ValidateService implements IValidateAcrossProfileUseCase, IValidateBranchUseCase, IValidateAcrossBranchUseCase, IValidateAcrossBranchConsistencyLevelUseCase {

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
            final CompletableFuture<TreeMap<String, File>> profileToFileMap = fileService.getYamlFileTree(
                    serviceBranch.getDirectory(),
                    serviceSpec.getService()
            );
            branchToProfileMap.put(serviceBranch.getBranch(), profileToFileMap.get());
        }

        final String fromBranch = serviceBranchList.get(0).getBranch();
        final String toBranch = serviceBranchList.get(1).getBranch();

        final List<BranchProfileReport> branchProfileReportList = new LinkedList<>();

        final List<Profile> profileList = new ArrayList<>(serviceSpec.getProfiles());
        profileList.add(Profile.builder().name("").build());

        profileList.forEach(profile -> {
            final File fromFile = branchToProfileMap.get(fromBranch).get(profile.getName());
            final File toFile = branchToProfileMap.get(toBranch).get(profile.getName());

            if (Objects.isNull(fromFile) || Objects.isNull(toFile)) {
                log.error("from file {}, toFile {}", fromFile, toFile);
            } else {
                final String profileName = profile.getName().isEmpty() ? "default" : profile.getName();

                final String fromOriginal = fileService.getFileToString(fromFile);
                final String toOriginal = fileService.getFileToString(toFile);

                final JsonNode sortFromOriginal = convertFileToJsonNode(fromFile, true);
                final JsonNode sortToOriginal = convertFileToJsonNode(toFile, true);

                final List<Document> documentList = Arrays.asList(
                        Document.builder().profile(profileName).branch(fromBranch).build(),
                        Document.builder().profile(profileName).branch(toBranch).build()
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
                                .profile(profileName)
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

    @Override
    public ConsistencyAcrossBranchesReport validateProfilesAcrossBranchDownflow(
            final ServiceBranchData fromServiceBranchData,
            final ServiceBranchData toServiceBranchData,
            final ServiceSpec serviceSpec,
            final boolean checkPropertyValue) throws ExecutionException, InterruptedException {

        final List<ServiceBranchData> serviceBranchList = Arrays.asList(fromServiceBranchData, toServiceBranchData);

        final HashMap<String, TreeMap<String, File>> branchToProfileMap = new HashMap<>();
        for (final ServiceBranchData serviceBranch : serviceBranchList) {
            final CompletableFuture<TreeMap<String, File>> profileToFileMap = fileService.getYamlFileTree(
                    serviceBranch.getDirectory(),
                    serviceSpec.getService()
            );
            branchToProfileMap.put(serviceBranch.getBranch(), profileToFileMap.get());
        }

        final String fromBranch = serviceBranchList.get(0).getBranch();
        final String toBranch = serviceBranchList.get(1).getBranch();

        final List<BranchProfileReport> branchProfileReportList = new LinkedList<>();

        final List<Profile> profileList = new ArrayList<>(serviceSpec.getProfiles());
        profileList.add(Profile.builder().name("").build());

        profileList.forEach(profile -> {
            final File fromFile = branchToProfileMap.get(fromBranch).get(profile.getName());
            final File toFile = branchToProfileMap.get(toBranch).get(profile.getName());

            if (Objects.isNull(fromFile) || Objects.isNull(toFile)) {
                log.error("from file {}, toFile {}", fromFile, toFile);
            } else {
                final String profileName = profile.getName().isEmpty() ? "default" : profile.getName();

                final String fromOriginal = fileService.getFileToString(fromFile);
                final String toOriginal = fileService.getFileToString(toFile);

                final JsonNode sortFromOriginal = convertFileToJsonNode(fromFile, true);
                final JsonNode sortToOriginal = convertFileToJsonNode(toFile, true);

                final List<Pair<String, String>> downFlowMissingData = generateDownFlowBetweenBranch(profileName, fromFile, toFile);

                final List<Document> documentList = Arrays.asList(
                        Document.builder().profile(profileName).branch(fromBranch).build(),
                        Document.builder().profile(profileName).branch(toBranch).build()
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
                                .profile(profileName)
                                .documents(documentList)
                                .propertyValuePair(downFlowMissingData)
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
            final ValidationAcrossBranchProperties validationAcrossBranchProperties) throws ExecutionException, InterruptedException {

        final List<ConsistencyAcrossBranchesReport> reportList = new LinkedList<>();
        final List<String> branchList = Arrays.asList(validationAcrossBranchProperties.getFromBranch(), validationAcrossBranchProperties.getToBranch());

//        serviceSpecList.forEach(serviceSpec -> validateBranchesInService(serviceSpec, branchList));


        final Map<ServiceBranchData, ServiceBranchData> mapServiceBranchToRepository = gitService.cloneGitRepositoryByBranchInBatchAsync(
                serviceSpecList.stream().map(ServiceSpec::getGitRepository).collect(Collectors.toList()),
                branchList
        );

        for (final ServiceSpec serviceSpec : serviceSpecList) {
            try {
                final List<ServiceBranchData> serviceBranchList = new ArrayList<>();
                for (final String branch : branchList) {
                    final ServiceBranchData data = ServiceBranchData.builder().repository(serviceSpec.getGitRepository().getUrl()).branch(branch).build();

                    serviceBranchList.add(ServiceBranchData.builder().repository(data.getRepository())
                            .branch(data.getBranch())
                            .directory(mapServiceBranchToRepository.get(data).getDirectory())
                            .build()
                    );
                }
                final ConsistencyAcrossBranchesReport report = validateProfilesAcrossBranch(serviceBranchList.get(0), serviceBranchList
                        .get(1), serviceSpec, validationAcrossBranchProperties.isPropertyValueEqual());
                reportList.add(report);

            } catch (final Exception e) {
                reportList.add(ConsistencyAcrossBranchesReport.builder()
                        .service(serviceSpec.getService())
                        .report(Arrays.asList(BranchProfileReport.builder()
                                .fileEqual(false)
                                .propertyValueEqual(false)
                                .profile("Invalid")
                                .build()))
                        .build());
                log.error("Exception while validating service : {}, {}", serviceSpec.getService(), e);
            }

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

            try {
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

                final List<String> profiles = serviceSpec.getProfiles().stream().map(Profile::getName).collect(Collectors.toList());

                for (final String profile : profiles) {
                    report.getMissingProperty().putIfAbsent(profile, null);
                }

                report.setService(serviceSpec.getService());
                reportList.add(report);
            } catch (final Exception e) {
                log.error("Exception while processing : {}, {}", serviceSpec.getService(), e);
                final ConsistencyAcrossProfilesReport report = ConsistencyAcrossProfilesReport.builder()
                        .service(serviceSpec.getService())
                        .build();
                reportList.add(report);
            }
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

            final JsonNode rootNode = convertFileToJsonNode(pairProfileFile.getSecond(), false);
            profilePropertyDetails.addProfileDocument(pairProfileFile.getFirst(),
                    rootNode);

            final List<String> missingProperties = new LinkedList<>();
            findMissingProfileProperties(rootNode, alteredPropertyTree, missingProperties);

            final List<String> suppressedPropertiesForProfile = suppressedProperties.getOrDefault(pairProfileFile.getFirst(), new ArrayList<>());
            suppressedPropertiesForProfile.addAll(commonSuppressedProperties);
            profilePropertyDetails.addProfileToMissingProperties(
                    pairProfileFile.getFirst(),
                    filterSuppressPropertyUseCase.filterSuppressedProperties(suppressedPropertiesForProfile, missingProperties)
            );
        });

        return profilePropertyDetails;
    }

    @ExecutionTime
    public void findMissingProfileProperties(final JsonNode rootNode, final PropertyTreeNode alteredPropertyTreeNode,
                                             final List<String> missingProperties) {

        final Queue<MissingPropertyDetail> list = new LinkedList<>();

        list.add(MissingPropertyDetail.builder().rootNode(rootNode)
                .actualPath("").pathRegex("")
                .isPropertyArray(false)
                .alteredPropertyRoot(alteredPropertyTreeNode)
                .build()
        );

        while (!list.isEmpty()) {
            final MissingPropertyDetail obj = list.remove();

            final String alteredRegexPath = propertyToAlteredProperty.get(obj.getPathRegex());
            final boolean getObjectMissingProperties =
                    obj.getRootNode().isObject() &&
                            (obj.getAlteredPropertyRoot() == null || !obj.getAlteredPropertyRoot().contains("*"));

            if (getObjectMissingProperties || obj.isPropertyArray()) {
                final Set<String> propertySet = new HashSet<>();
                obj.getRootNode().fields().forEachRemaining(property -> {
                    propertySet.add(property.getKey());
                });
                globalObjectProperty.get(alteredRegexPath)
                        .stream()
                        .filter(property -> !propertySet.contains(property))
                        .forEach(property -> addErrorProfileNotification(missingProperties, obj.getActualPath(), property));
            } else if (isJsonNodeValueOrNull(obj.getRootNode()) && globalObjectProperty.containsKey(alteredRegexPath)) {
                globalObjectProperty.get(alteredRegexPath).forEach(property -> {
                    addErrorProfileNotification(missingProperties, obj.getActualPath(), property);
                });
            }

            obj.getRootNode().fields().forEachRemaining(object -> {
                final JsonNode childRootNode = object.getValue();
                final String key = object.getKey();

                if (childRootNode.isObject()) {
                    list.add(MissingPropertyDetail.builder().rootNode(childRootNode)
                            .actualPath(generatePropertyPath(obj.getActualPath(), key))
                            .pathRegex(generatePropertyPath(alteredRegexPath, key))
                            .isPropertyArray(false)
                            .alteredPropertyRoot(Objects.isNull(obj.getAlteredPropertyRoot()) ? null : obj.getAlteredPropertyRoot().getChild("*", key))
                            .build()
                    );
                } else if (childRootNode.isArray()) {
                    final AtomicInteger index = new AtomicInteger();
                    childRootNode.forEach(subRoot -> {
                        list.add(MissingPropertyDetail.builder().rootNode(subRoot)
                                .actualPath(generatePropertyPath(obj.getActualPath(), key, Integer.toString(index.get())))
                                .pathRegex(generatePropertyPath(alteredRegexPath, key, "*"))
                                .isPropertyArray(true)
                                .alteredPropertyRoot(Objects.isNull(obj.getAlteredPropertyRoot()) ? null : obj.getAlteredPropertyRoot().getChild(key))
                                .build()
                        );
                        index.getAndIncrement();
                    });
                } else {
                    final Pair<String, String> objectPathPrefix = getObjectPropertyToMissingPropertiesPair(alteredRegexPath, key);
                    if (Objects.nonNull(objectPathPrefix)) {
                        globalObjectProperty.get(objectPathPrefix.getFirst()).forEach(missingProperty -> {
                            addErrorProfileNotification(missingProperties, objectPathPrefix.getSecond(), missingProperty);
                        });
                    }
                }
            });
        }
    }

    @ExecutionTime
    public Map<String, HashSet<String>> buildGlobalObjectToPropertiesMapping(final List<Pair<String, File>> fileContentMap,
                                                                             final PropertyTreeNode alteredPropertyTree) {

        propertyToAlteredProperty = new HashMap<>();
        propertyToAlteredProperty.put("", "");

        final HashMap<String, HashSet<String>> objectPropertyMap = new HashMap<>();
        fileContentMap.forEach(pairProfileFile ->
                traverseObjectToPropertiesMapping(convertFileToJsonNode(pairProfileFile.getSecond(), false), objectPropertyMap,
                        alteredPropertyTree)
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

    @ExecutionTime
    private void traverseObjectToPropertiesMapping(final JsonNode rootNode,
                                                   final HashMap<String, HashSet<String>> objectPropertyMap,
                                                   final PropertyTreeNode alteredPropertyRoot) {

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

                objectPropertyMap.putIfAbsent(propertyNodeDetail.getPathRegex(), new HashSet<>());
                final Set<String> propertySet = objectPropertyMap.get(propertyNodeDetail.getPathRegex());

                propertyNodeDetail.getRootNode().fields().forEachRemaining(property -> propertySet.add(property.getKey()));
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

                    propertyToAlteredProperty.put(generatePropertyPath(propertyNodeDetail.getPathRegex(), key), alterRegexPath);
                    queue.add(PropertyNodeDetail.builder().alteredPropertyRoot(childNode)
                            .rootNode(value).pathRegex(alterRegexPath)
                            .isPropertyArray(false)
                            .build()
                    );
                } else if (value.isArray()) {
                    final String regexPath = generatePropertyPath(propertyNodeDetail.getPathRegex(), key, "*");
                    propertyToAlteredProperty.put(regexPath, regexPath);

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
    }

    private List<Pair<String, String>> generateDownFlowBetweenBranch(final String profileName, final File fromFile, final File toFile) {
        // lower branch
        final Map<String, HashMap<String, String>> fromGlobalProperty =
                buildGlobalObjectToPropertiesMappingForBranch(
                        Collections.singletonList(Pair.of(profileName, fromFile)),
                        PropertyTreeNode.builder().build()
                );
        // higher branch
        final Map<String, HashMap<String, String>> toGlobalProperty = // preprod
                buildGlobalObjectToPropertiesMappingForBranch(
                        Collections.singletonList(Pair.of(profileName, toFile)),
                        PropertyTreeNode.builder().build()
                );

        final List<Pair<String, String>> downFlowMissingData = new LinkedList<>();
        toGlobalProperty.forEach((path, propertyValue) -> {
            final HashMap<String, String> fromPropertyValue = fromGlobalProperty.getOrDefault(path, null);
            if (Objects.isNull(fromPropertyValue)) {
                propertyValue.forEach((key, value) -> System.out.println("MISSING : " + path + "." + key));
            } else {
                propertyValue.forEach((property, value) -> {
                    final String val = fromPropertyValue.getOrDefault(property, null);
                    final String invalidProperty = path + "." + property;
                    if (Objects.isNull(val)) {
                        downFlowMissingData.add(Pair.of(invalidProperty, "MISSING"));
                    } else if (!value.equalsIgnoreCase(val)) {
                        downFlowMissingData.add(Pair.of(invalidProperty, "MISMATCH"));
                    }
                });
            }
        });

        return downFlowMissingData;
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

        return Objects.isNull(object) ? null : Pair.of(object, property);
    }

    private void addErrorProfileNotification(final List<String> missingProperties, final String path, final String property) {
        missingProperties.add(generatePropertyPath(path, property));
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

    @ExecutionTime
    public Map<String, HashMap<String, String>> buildGlobalObjectToPropertiesMappingForBranch(
            final List<Pair<String, File>> fileContentMap,
            final PropertyTreeNode alteredPropertyTree) {

        propertyToAlteredProperty = new HashMap<>();
        propertyToAlteredProperty.put("", "");

        final HashMap<String, HashMap<String, String>> objectPropertyMap = new HashMap<>();
        fileContentMap.forEach(pairProfileFile ->
                traverseObjectToPropertiesMappingForBranch(convertFileToJsonNode(pairProfileFile.getSecond(), false), objectPropertyMap,
                        alteredPropertyTree)
        );
        return objectPropertyMap;
    }

    private boolean checkIfJsonNodeValue(final JsonNode node) {
        if (Objects.isNull(node)) return false;

        return node.isValueNode();
    }

    @ExecutionTime
    private void traverseObjectToPropertiesMappingForBranch(final JsonNode rootNode,
                                                            final HashMap<String, HashMap<String, String>> objectPropertyMap,
                                                            final PropertyTreeNode alteredPropertyRoot) {

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

                objectPropertyMap.putIfAbsent(propertyNodeDetail.getPathRegex(), new HashMap<>());
                final HashMap<String, String> propertySet = objectPropertyMap.get(propertyNodeDetail.getPathRegex());

                propertyNodeDetail.getRootNode().fields().forEachRemaining(property -> {
                    propertySet.put(property.getKey(), checkIfJsonNodeValue(property.getValue()) ? property.getValue().toString() : "$");
                });
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

                    propertyToAlteredProperty.put(generatePropertyPath(propertyNodeDetail.getPathRegex(), key), alterRegexPath);
                    queue.add(PropertyNodeDetail.builder().alteredPropertyRoot(childNode)
                            .rootNode(value).pathRegex(alterRegexPath)
                            .isPropertyArray(false)
                            .build()
                    );
                } else if (value.isArray()) {
                    final String regexPath = generatePropertyPath(propertyNodeDetail.getPathRegex(), key, "*");
                    propertyToAlteredProperty.put(regexPath, regexPath);

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
    }

    @ThreadDirectory
    @Override
    public List<ConsistencyLevelAcrossBranchesReport> validateAcrossBranchesConsistencyLevelServiceBatch(final List<ServiceSpec> serviceSpecList, final boolean isPropertyValueEqual, final String targetBranch) throws ExecutionException, InterruptedException {

        final Map<ServiceBranchData, ServiceBranchData> mapServiceBranchToRepository = gitService.cloneGitRepositoryByBranchInBatchAsyncDifferentBranches(serviceSpecList);

        final List<ConsistencyLevelAcrossBranchesReport> consistencyLevelAcrossBranchesReports = new ArrayList<>();

        for (final ServiceSpec serviceSpec : serviceSpecList) {
            final List<BranchReport> branchReports = new ArrayList<>();

            final List<ServiceBranchData> serviceBranchList = new ArrayList<>();

            final List<Branch> branches = sortByPriority(serviceSpec.getBranches());

            int x = 0;
            for (int i = 0; i < branches.size(); i++) {
                if (branches.get(i).getName().equals(targetBranch)) {
                    x = i;
                    break;
                }
            }

            for (int i = 0; i <= x; i++) {
                final ServiceBranchData data = ServiceBranchData.builder().repository(serviceSpec.getGitRepository().getUrl()).branch(branches.get(i).getName()).build();

                serviceBranchList.add(ServiceBranchData.builder().repository(data.getRepository())
                        .branch(data.getBranch())
                        .directory(mapServiceBranchToRepository.get(data).getDirectory())
                        .build()
                );
            }


            for (int i = 0; i < serviceBranchList.size() - 1; i++) {

                final ConsistencyAcrossBranchesReport report = validateProfilesAcrossBranchDownflow(serviceBranchList.get(i), serviceBranchList
                        .get(i + 1), serviceSpec, isPropertyValueEqual);

                branchReports.add(BranchReport.builder()
                        .consistencyAcrossBranchesReport(report)
                        .fromBranch(serviceBranchList.get(i).getBranch())
                        .toBranch(serviceBranchList.get(i + 1).getBranch())
                        .build());
            }

            consistencyLevelAcrossBranchesReports.add(ConsistencyLevelAcrossBranchesReport.builder()
                    .branchReports(branchReports)
                    .service(serviceSpec.getService())
                    .build());
        }

        return consistencyLevelAcrossBranchesReports;
    }

    public List<Branch> sortByPriority(final List<Branch> branches) {
        return branches.stream().sorted(this::compareBranchPriority).collect(Collectors.toList());
    }

    private int compareBranchPriority(final Branch a, final Branch b) {
        return b.getPriority() - a.getPriority();
    }
}
