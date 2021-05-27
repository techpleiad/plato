package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.techpleiad.plato.core.domain.Branch;
import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.domain.Profile;
import org.techpleiad.plato.core.domain.PropertyTreeNode;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.BranchNotSupportedException;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IFilterSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetAlteredPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ValidateServiceTest {

    @InjectMocks
    private ValidateService validateService;

    @Mock
    private IFileServiceUserCase fileService;

    @Mock
    private IGitServiceUseCase gitService;

    @Mock
    private IGetSuppressPropertyUseCase getSuppressPropertyUseCase;

    @Mock
    private IGetAlteredPropertyUseCase getAlteredPropertyUseCase;

    @Mock
    private IFilterSuppressPropertyUseCase filterSuppressPropertyUseCase;

    private static final String DEV = "dev", PREPROD = "preprod", TEST = "test", MASTER = "master", COMMON = "common";
    private static final String[] profiles = {DEV, PREPROD};
    private static final String PATH = "classpath:validation/";
    private static final String SERVICE = "custom-manager";

    private List<Pair<String, File>> fileContentMap;
    private PropertyTreeNode alteredPropertyTree;

    @Test
    void given_ServiceSpecAndBranch_validateBranchInService() {

        final ServiceSpec serviceSpec = ServiceSpec.builder()
                .service(SERVICE)
                .branches(Arrays.asList(
                        Branch.builder().name(MASTER).build(),
                        Branch.builder().name(TEST).build(),
                        Branch.builder().name(DEV).build()
                )).build();

        Assertions.assertTrue(validateService.validateBranchInService(serviceSpec, DEV));

        final BranchNotSupportedException exception = Assertions.assertThrows(BranchNotSupportedException.class, () -> {
            validateService.validateBranchInService(serviceSpec, "dev-1");
        });
        Assertions.assertEquals(SERVICE, exception.getService());
        Assertions.assertEquals(1, exception.getBranches().size());
        Assertions.assertTrue(exception.getBranches().contains("dev-1"));
    }

    @Test
    void given_serviceSpecAndBranchList_validateBranchesInService() {

        final ServiceSpec serviceSpec = ServiceSpec.builder()
                .branches(
                        Arrays.asList(Branch.builder().name(DEV).build(),
                                Branch.builder().name(TEST).build())
                ).build();

        Assertions.assertTrue(validateService.validateBranchesInService(serviceSpec, Arrays.asList(DEV, TEST)));

        final BranchNotSupportedException exception = Assertions.assertThrows(BranchNotSupportedException.class, () -> {
            validateService.validateBranchesInService(serviceSpec, Arrays.asList(PREPROD, DEV));
        });
        Assertions.assertTrue(exception.getBranches().contains(PREPROD));
    }

    private static void AssertTrueMissingProperties(final String profile, final List<String> missingProperties,
                                                    final List<String> properties) {

        log.info("{} : missing prop > {}", profile, missingProperties);
        properties.forEach(prop -> Assertions.assertTrue(missingProperties.contains(prop)));
    }

    private File getFileContent(final String fileName) throws IOException {
        return ResourceUtils.getFile(PATH + fileName);
    }

    private JsonNode convertToJsonNode(final File file) throws IOException {
        return new YAMLMapper().readTree(file);
    }

    private void init(final int profileTestIndex, final List<String> alteredProperties) throws IOException {
        fileContentMap = new LinkedList<>();
        for (final String profile : profiles) {
            fileContentMap.add(
                    Pair.of(profile, getFileContent(
                            new StringBuilder()
                                    .append("global-missing-alter/profile-").append(profile)
                                    .append("-").append(profileTestIndex)
                                    .append(".yml")
                                    .toString()
                    ))
            );
        }
        alteredPropertyTree = PropertyTreeNode.convertPropertiesToPropertyTree(alteredProperties);
    }

    private void getMissingProperties(final JsonNode convertToJsonNode, final PropertyTreeNode alteredPropertyTree, final List<String> missingProperties) {
        validateService.findMissingProfileProperties(
                convertToJsonNode,
                alteredPropertyTree,
                missingProperties
        );
    }

    @Test
    void given_profileFileContentAndEmptyAlteredPropertyTree_returnGlobalObjectPropertiesMapping_1() throws Exception {

        init(1, Collections.emptyList());

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        response.forEach((key, value) -> log.info("{} :: {}", key, value));

        Assertions.assertEquals("[dev, preprod, test]", new TreeSet<>(response.get("redirection")).toString());
        Assertions.assertEquals("[access, cm, jm]", new TreeSet<>(response.get("redirection.dev")).toString());
        Assertions.assertEquals("[cm, jm]", new TreeSet<>(response.get("redirection.test")).toString());
        Assertions.assertEquals("[jm]", new TreeSet<>(response.get("redirection.preprod")).toString());
    }

    @Test
    void given_profileFileContentAndAlteredPropertyTree_returnGlobalObjectPropertiesMapping_1() throws Exception {

        init(1, Collections.singletonList("redirection.*"));

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        response.forEach((key, value) -> log.info("{} :: {}", key, value));

        Assertions.assertEquals("[access, cm, jm]", new TreeSet<>(response.get("redirection.*")).toString());
    }

    @Test
    void given_fileContentAndGlobalObjectPropertiesOnEmptyAlteredPropertyTree_returnMissingProperties_1() throws Exception {

        init(1, Collections.emptyList());

        final Map<String, HashSet<String>> globalObjectToPropertiesMap = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        validateService.setGlobalObjectProperty(globalObjectToPropertiesMap);

        final ConsistencyAcrossProfilesReport serviceMissingProfileDetails = ConsistencyAcrossProfilesReport.builder().build();

        for (final Pair<String, File> pair : fileContentMap) {
            final List<String> missingProperties = new LinkedList<>();
            getMissingProperties(convertToJsonNode(pair.getSecond()), alteredPropertyTree, missingProperties);
            serviceMissingProfileDetails.addProfileToMissingProperties(pair.getFirst(), missingProperties);
        }

        AssertTrueMissingProperties(
                PREPROD,
                serviceMissingProfileDetails.getMissingProperty().get(PREPROD),
                Collections.singletonList(
                        "redirection.dev"
                )
        );
    }

    @Test
    void given_profileFileContentAndEmptyAlteredPropertyTree_returnGlobalObjectPropertiesMapping_2() throws Exception {

        init(2, Collections.emptyList());

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        response.forEach((key, value) -> log.info("{} :: {}", key, value));

        Assertions.assertEquals("[preprod, test]", new TreeSet<>(response.get("redirection")).toString());
        Assertions.assertEquals("[access, cm, jm]", new TreeSet<>(response.get("redirection.*")).toString());
        Assertions.assertEquals("[cm, jm]", new TreeSet<>(response.get("redirection.test")).toString());
        Assertions.assertEquals("[jm]", new TreeSet<>(response.get("redirection.preprod")).toString());
    }

    @Test
    void given_profileFileContentAndAlteredPropertyTree_returnGlobalObjectPropertiesMapping_2() throws Exception {

        init(2, Collections.singletonList("redirection.*"));

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        response.forEach((key, value) -> log.info("{} :: {}", key, value));

        Assertions.assertEquals("[access, cm, jm]", new TreeSet<>(response.get("redirection.*")).toString());
    }

    @Test
    void given_fileContentAndGlobalObjectPropertiesOnEmptyAlteredPropertyTree_returnMissingProperties_2() throws Exception {

        init(2, Collections.emptyList());

        final Map<String, HashSet<String>> globalObjectToPropertiesMap = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        validateService.setGlobalObjectProperty(globalObjectToPropertiesMap);
        globalObjectToPropertiesMap.forEach((key, value) -> log.info("{} :: {}", key, value));

        final ConsistencyAcrossProfilesReport serviceMissingProfileDetails = ConsistencyAcrossProfilesReport.builder().build();

        for (final Pair<String, File> pair : fileContentMap) {
            final List<String> missingProperties = new LinkedList<>();
            getMissingProperties(convertToJsonNode(pair.getSecond()), alteredPropertyTree, missingProperties);
            serviceMissingProfileDetails.addProfileToMissingProperties(pair.getFirst(), missingProperties);
        }

        AssertTrueMissingProperties(
                profiles[0],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[0]),
                Collections.singletonList(
                        "redirection.1.access"
                )
        );

        final List<String> missingPropertiesPreprod = serviceMissingProfileDetails.getMissingProperty().get(profiles[1]);
        Assertions.assertTrue(CollectionUtils.isEmpty(missingPropertiesPreprod));
    }

    @Test
    void given_fileContentAndGlobalObjectPropertiesOnAlteredPropertyTree_returnMissingProperties_2() throws Exception {

        init(2, Collections.singletonList("redirection.*"));

        final Map<String, HashSet<String>> globalObjectToPropertiesMap = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        validateService.setGlobalObjectProperty(globalObjectToPropertiesMap);
        globalObjectToPropertiesMap.forEach((key, value) -> log.info("{} :: {}", key, value));

        final ConsistencyAcrossProfilesReport serviceMissingProfileDetails = ConsistencyAcrossProfilesReport.builder().build();

        for (final Pair<String, File> pair : fileContentMap) {
            final List<String> missingProperties = new LinkedList<>();
            getMissingProperties(convertToJsonNode(pair.getSecond()), alteredPropertyTree, missingProperties);
            serviceMissingProfileDetails.addProfileToMissingProperties(pair.getFirst(), missingProperties);
        }

        AssertTrueMissingProperties(
                profiles[0],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[0]),
                Collections.singletonList(
                        "redirection.1.access"
                )
        );

        AssertTrueMissingProperties(
                profiles[1],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[1]),
                Arrays.asList(
                        "redirection.preprod.cm",
                        "redirection.preprod.access",
                        "redirection.test.access"
                )
        );
    }

    @Test
    void given_profileFileContentAndEmptyAlteredPropertyTree_returnGlobalObjectPropertiesMapping_3() throws Exception {

        init(3, Collections.emptyList());

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);

        Assertions.assertEquals("[preprod, test]", new TreeSet<>(response.get("redirection")).toString());
        Assertions.assertEquals("[cm, jm]", new TreeSet<>(response.get("redirection.test")).toString());
        Assertions.assertEquals("[jm]", new TreeSet<>(response.get("redirection.preprod")).toString());
    }

    @Test
    void given_profileFileContentAndAlteredPropertyTree_returnGlobalObjectPropertiesMapping_3() throws Exception {

        init(3, Collections.singletonList("redirection.*"));

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        response.forEach((key, value) -> log.info("{} :: {}", key, value));

        Assertions.assertEquals("[cm, jm]", new TreeSet<>(response.get("redirection.*")).toString());
    }

    @Test
    void given_fileContentAndGlobalObjectPropertiesOnEmptyAlteredPropertyTree_returnMissingProperties_3() throws Exception {

        init(3, Collections.emptyList());

        final Map<String, HashSet<String>> globalObjectToPropertiesMap = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        validateService.setGlobalObjectProperty(globalObjectToPropertiesMap);
        globalObjectToPropertiesMap.forEach((key, value) -> log.info("{} :: {}", key, value));

        final ConsistencyAcrossProfilesReport serviceMissingProfileDetails = ConsistencyAcrossProfilesReport.builder().build();
        for (final Pair<String, File> pair : fileContentMap) {
            final List<String> missingProperties = new LinkedList<>();
            getMissingProperties(convertToJsonNode(pair.getSecond()), alteredPropertyTree, missingProperties);
            serviceMissingProfileDetails.addProfileToMissingProperties(pair.getFirst(), missingProperties);
        }

        AssertTrueMissingProperties(
                profiles[0],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[0]),
                Arrays.asList(
                        "redirection.test",
                        "redirection.preprod"
                )
        );

        final List<String> missingPropertiesPreprod = serviceMissingProfileDetails.getMissingProperty().get(profiles[1]);
        Assertions.assertTrue(CollectionUtils.isEmpty(missingPropertiesPreprod));
    }

    @Test
    void given_fileContentAndGlobalObjectPropertiesOnAlteredPropertyTree_returnMissingProperties_3() throws Exception {

        init(3, Collections.singletonList("redirection.*"));

        final Map<String, HashSet<String>> globalObjectToPropertiesMap = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        validateService.setGlobalObjectProperty(globalObjectToPropertiesMap);
        globalObjectToPropertiesMap.forEach((key, value) -> log.info("{} :: {}", key, value));

        final ConsistencyAcrossProfilesReport serviceMissingProfileDetails = ConsistencyAcrossProfilesReport.builder().build();
        for (final Pair<String, File> pair : fileContentMap) {
            final List<String> missingProperties = new LinkedList<>();
            getMissingProperties(convertToJsonNode(pair.getSecond()), alteredPropertyTree, missingProperties);
            serviceMissingProfileDetails.addProfileToMissingProperties(pair.getFirst(), missingProperties);
        }

        AssertTrueMissingProperties(
                profiles[0],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[0]),
                Arrays.asList(
                        "redirection.*.jm",
                        "redirection.*.cm"
                )
        );

        AssertTrueMissingProperties(
                profiles[1],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[1]),
                Collections.singletonList(
                        "redirection.preprod.cm"
                )
        );
    }

    @Test
    void given_profileFileContentAndAlteredPropertyTree_returnGlobalObjectPropertiesMapping_4() throws Exception {

        init(4, Collections.emptyList());

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        response.forEach((key, value) -> log.info("{} :: {}", key, value));

        Assertions.assertEquals("[endpoints]", new TreeSet<>(response.get("management")).toString());
        Assertions.assertEquals("[exclude, include]", new TreeSet<>(response.get("management.endpoints")).toString());
    }

    @Test
    void given_profileFileContentAndEmptyAlteredPropertyTree_returnGlobalObjectPropertiesMapping_5() throws Exception {

        init(5, Collections.emptyList());

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);

        Assertions.assertEquals("[cz, in]", new TreeSet<>(response.get("mongo")).toString());
        Assertions.assertEquals("[dk, in]", new TreeSet<>(response.get("mongo.cz.uri")).toString());
        Assertions.assertEquals("[cz, pl]", new TreeSet<>(response.get("mongo.in.uri")).toString());
    }

    @Test
    void given_profileFileContentAndAlteredPropertyTree_returnGlobalObjectPropertiesMapping_5() throws Exception {

        init(5, Arrays.asList("mongo.*", "mongo.*.uri.*"));

        final Map<String, HashSet<String>> response = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        response.forEach((key, value) -> log.info("{} :: {}", key, value));

        Assertions.assertEquals("[uri]", new TreeSet<>(response.get("mongo.*")).toString());
        Assertions.assertEquals("[host, uri]", new TreeSet<>(response.get("mongo.*.uri.*")).toString());
    }

    @Test
    void given_fileContentAndGlobalObjectPropertiesOnEmptyAlteredPropertyTree_returnMissingProperties_5() throws Exception {

        init(5, Collections.emptyList());

        final Map<String, HashSet<String>> globalObjectToPropertiesMap = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        validateService.setGlobalObjectProperty(globalObjectToPropertiesMap);
        globalObjectToPropertiesMap.forEach((key, value) -> log.info("{} :: {}", key, value));

        final ConsistencyAcrossProfilesReport serviceMissingProfileDetails = ConsistencyAcrossProfilesReport.builder().build();
        for (final Pair<String, File> pair : fileContentMap) {
            final List<String> missingProperties = new LinkedList<>();
            getMissingProperties(convertToJsonNode(pair.getSecond()), alteredPropertyTree, missingProperties);
            serviceMissingProfileDetails.addProfileToMissingProperties(pair.getFirst(), missingProperties);
        }

        AssertTrueMissingProperties(
                profiles[0],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[0]),
                Collections.singletonList(
                        "mongo.cz"
                )
        );

        AssertTrueMissingProperties(
                profiles[1],
                serviceMissingProfileDetails.getMissingProperty().get(profiles[1]),
                Collections.singletonList(
                        "mongo.in"
                )
        );
    }

    @Test
    void given_fileContentAndGlobalObjectPropertiesOnAlteredPropertyTree_returnMissingProperties_5() throws Exception {

        init(5, Arrays.asList("mongo.*", "mongo.*.uri.*"));

        final Map<String, HashSet<String>> globalObjectToPropertiesMap = validateService.buildGlobalObjectToPropertiesMapping(fileContentMap, alteredPropertyTree);
        validateService.setGlobalObjectProperty(globalObjectToPropertiesMap);
        globalObjectToPropertiesMap.forEach((key, value) -> log.info("{} :: {}", key, value));

        for (final Pair<String, File> pair : fileContentMap) {
            final List<String> missingProperties = new LinkedList<>();
            getMissingProperties(convertToJsonNode(pair.getSecond()), alteredPropertyTree, missingProperties);
            Assertions.assertTrue(CollectionUtils.isEmpty(missingProperties));
        }
    }

    @Test
    void validateAcrossProfilesInServiceBatch() throws Exception {

        final List<Profile> profiles = Arrays.asList(Profile.builder().name(DEV).build(), Profile.builder().name(PREPROD).build());
        final List<Branch> branches = Arrays.asList(Branch.builder().name(DEV).build(), Branch.builder().name(TEST).build());

        final List<ServiceSpec> serviceSpecList = Collections.singletonList(
                ServiceSpec.builder().service(SERVICE).directory("")
                        .gitRepository(GitRepository.builder().url("url").build())
                        .branches(branches)
                        .profiles(profiles)
                        .build()
        );
        final String prefix = "repo/" + DEV + "/" + SERVICE + "-";
        final String branchName = DEV;

        final HashMap<ServiceBranchData, ServiceBranchData> mapServiceBranchToRepository = new HashMap<>();
        mapServiceBranchToRepository.put(
                ServiceBranchData.builder().branch(branchName).repository("url").build(), ServiceBranchData.builder().branch(branchName).repository("url").build()
        );

        final List<Pair<String, File>> profileFileList = Arrays.asList(
                Pair.of(DEV, getFileContent(prefix + DEV + ".yml")),
                Pair.of(PREPROD, getFileContent(prefix + PREPROD + ".yml"))
        );
        final CompletableFuture<List<Pair<String, File>>> mapProfileToFileContent = CompletableFuture.completedFuture(profileFileList);

        final HashMap<String, List<String>> suppressedPropertiesMap = new HashMap<>();
        suppressedPropertiesMap.put(COMMON, Collections.emptyList());
        suppressedPropertiesMap.put(PREPROD, Collections.emptyList());
        suppressedPropertiesMap.put(DEV, Collections.singletonList("redirection.test.access"));

        final List<String> alteredProperties = Collections.singletonList("redirection.*");

        Mockito.when(gitService.cloneGitRepositoryByBranchInBatchAsync(Mockito.anyList(), Mockito.anyList()))
                .thenReturn(mapServiceBranchToRepository);

        Mockito.when(fileService.getYamlFiles(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(mapProfileToFileContent);

        Mockito.when(getSuppressPropertyUseCase.getSuppressedProperties(Mockito.anyString())).thenReturn(suppressedPropertiesMap);

        Mockito.when(getAlteredPropertyUseCase.getAlteredProperties(Mockito.anyString())).thenReturn(alteredProperties);

        Mockito.when(filterSuppressPropertyUseCase.filterSuppressedProperties(Mockito.anyList(), Mockito.anyList())).thenReturn(Collections.emptyList());

        validateService.validateAcrossProfilesInServiceBatch(serviceSpecList, DEV, false);
        validateService.validateAcrossProfilesInServiceBatch(serviceSpecList, DEV, true);
    }

    @Test
    void validateProfilesAcrossBranch() throws Exception {

        final ServiceBranchData toServiceBranchData = ServiceBranchData.builder().branch(PREPROD).directory(getFileContent("repo/" + PREPROD)).repository("url").build();
        final ServiceBranchData fromServiceBranchData = ServiceBranchData.builder().branch(DEV).directory(getFileContent("repo/" + DEV)).repository("url").build();
        final ServiceSpec serviceSpec = ServiceSpec.builder().service(SERVICE).directory("")
                .profiles(Arrays.asList(Profile.builder().name(DEV).build(), Profile.builder().name(PREPROD).build()))
                .build();

        final File preprodFile = getFileContent("repo/" + PREPROD);
        final File devFile = getFileContent("repo/" + DEV);

        Mockito.when(fileService.getYamlFileTree(Mockito.any(), Mockito.anyString())).thenAnswer(invocation -> {
            final File file = invocation.getArgument(0);

            final TreeMap<String, File> profileToFileMap = new TreeMap<>();
            if (file.equals(devFile)) {
                final String prefix = "repo/" + DEV + "/" + SERVICE + "-";
                profileToFileMap.put(PREPROD, getFileContent(prefix + PREPROD + ".yml"));
                profileToFileMap.put(TEST, getFileContent(prefix + TEST + ".yml"));
                profileToFileMap.put(DEV, getFileContent(prefix + DEV + ".yml"));
                profileToFileMap.put("", getFileContent("repo/" + DEV + "/" + SERVICE + ".yml"));
            } else if (file.equals(preprodFile)) {
                final String prefix = "repo/" + PREPROD + "/" + SERVICE + "-";
                profileToFileMap.put(PREPROD, getFileContent(prefix + PREPROD + ".yml"));
                profileToFileMap.put(TEST, getFileContent(prefix + TEST + ".yml"));
                profileToFileMap.put(DEV, getFileContent(prefix + DEV + ".yml"));
                profileToFileMap.put("", getFileContent("repo/" + PREPROD + "/" + SERVICE + ".yml"));
            }
            return CompletableFuture.completedFuture(profileToFileMap);
        });

        Mockito.when(fileService.getFileToString(Mockito.any())).thenAnswer(invocation -> {
            final File file = invocation.getArgument(0);
            return getFileToString(file);
        });

        final ConsistencyAcrossBranchesReport report = validateService.validateProfilesAcrossBranch(fromServiceBranchData, toServiceBranchData, serviceSpec, true);

        Assertions.assertEquals(serviceSpec.getService(), report.getService());
        Assertions.assertEquals(3, report.getReport().size());

        report.getReport().forEach(profileReport -> {
            switch (profileReport.getProfile()) {
                case PREPROD:
                case "default":
                    Assertions.assertTrue(profileReport.getPropertyValueEqual());
                    Assertions.assertTrue(profileReport.isFileEqual());
                    break;

                case TEST:
                    Assertions.assertFalse(profileReport.getPropertyValueEqual());
                    Assertions.assertFalse(profileReport.isFileEqual());
                    break;

                case DEV:
                    Assertions.assertTrue(profileReport.getPropertyValueEqual());
                    Assertions.assertFalse(profileReport.isFileEqual());
                    break;

                default:
                    Assertions.fail();
            }
        });
    }

    @Test
    void testSorting() {
        final List<Branch> branches = Arrays
                .asList(Branch.builder().name("as").priority(3).build(), Branch.builder().name("bs").priority(1).build(), Branch.builder().name("cs").priority(2).build());
        validateService.sortByPriority(branches);
    }

    private String getFileToString(final File file) {
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
}
