package org.techpleiad.plato.core.service;

import org.eclipse.jgit.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.exceptions.GitBranchNotFoundException;
import org.techpleiad.plato.core.exceptions.GitRepositoryNotFoundException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

;

@ExtendWith(MockitoExtension.class)
class GitServiceTest {

    private static final String VALID_REPOSITORY = "https://github.com/stencila/test.git";
    private static final String INVALID_REPOSITORY = "https://github.com/stencila/test.g";
    private static final List<String> BRANCHES = Arrays.asList("master", "nokome-patch-1");

    @InjectMocks
    private GitService gitServiceMock;

    @Mock
    private FileService fileServiceMock;

    @Mock
    private GitCloneRequestService gitServiceHandler;

    //@Test // TODO
    void givenGitRepositoriesBranchesList_whenCloneGitRepositoryByBranchInBatch_thenReturnRepoToDirectoryMap() throws Exception {

        final List<GitRepository> gitRepositoryList = Arrays.asList(
                GitRepository.builder().url(VALID_REPOSITORY).build(),
                GitRepository.builder().url(VALID_REPOSITORY).build()
        );

        Mockito.when(fileServiceMock.generateDirectory(Mockito.any(), Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
            final List<String> path = Arrays.stream(invocation.getArguments())
                    .map(Object::toString)
                    .collect(Collectors.toList());
            return StringUtils.join(path, "/");
        });
        Mockito.when(fileServiceMock.generateFileFromLocalDirectoryPath(Mockito.any())).thenAnswer(invocation -> new File((String) invocation.getArgument(0)));
        Mockito.doNothing().when(gitServiceHandler).cloneGitRepositoryByBranchSync(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

        final HashMap<ServiceBranchData, ServiceBranchData> mapRepoToDirectory = gitServiceMock.cloneGitRepositoryByBranchInBatch(gitRepositoryList, BRANCHES);

        Assertions.assertEquals(2, mapRepoToDirectory.size());
    }


    @Test
    void givenGitRepository_whenValidateUrl_thenThrowGitRepoNotFoundException() throws Exception {
        final GitRepository gitRepositoryWithValidUrl = GitRepository.builder().url(VALID_REPOSITORY).build();
        Assertions.assertDoesNotThrow(() -> gitServiceMock.validateGitUrl(gitRepositoryWithValidUrl));

        final GitRepository gitRepositoryWithInvalidUrl = GitRepository.builder().url(INVALID_REPOSITORY).build();
        Assertions.assertThrows(GitRepositoryNotFoundException.class, () -> gitServiceMock.validateGitUrl(gitRepositoryWithInvalidUrl));
    }


    @Test
    void givenGitRepository_whenValidateBranchesInGitRepository_thenGitBranchNotFoundException() throws Exception {
        final GitRepository gitRepositoryWithValidUrl = GitRepository.builder().url(VALID_REPOSITORY).build();
        final List<String> serviceBranches = new ArrayList<>(BRANCHES);

        Assertions.assertDoesNotThrow(() -> gitServiceMock.validateGitUrlAndBranches(gitRepositoryWithValidUrl, serviceBranches));

        serviceBranches.add("dev");
        final GitBranchNotFoundException exception = Assertions.assertThrows(GitBranchNotFoundException.class, () ->
                gitServiceMock.validateGitUrlAndBranches(gitRepositoryWithValidUrl, serviceBranches)
        );
        Assertions.assertEquals(1, exception.getBranches().size());
        Assertions.assertTrue(exception.getBranches().contains("dev"));

        final GitRepository gitRepositoryWithInValidUrl = GitRepository.builder().url(INVALID_REPOSITORY).build();
        Assertions.assertThrows(GitRepositoryNotFoundException.class, () ->
                gitServiceMock.validateGitUrlAndBranches(gitRepositoryWithInValidUrl, serviceBranches)
        );

    }
}
