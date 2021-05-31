package org.techpleiad.plato.core.service;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.exceptions.GitBranchNotFoundException;

import java.io.File;

@ExtendWith(MockitoExtension.class)
class GitServiceHandlerTest {

    private static final String VALID_REPOSITORY = "https://github.com/stencila/test.git";

    private static final GitRepository gitRepositoryWithValidUrl = GitRepository.builder().url(VALID_REPOSITORY).build();

    private static final String validBranch = "master";
    private static final String invalidBranch = "dev";
    private static final CloneCommand command = Git.cloneRepository().setURI(gitRepositoryWithValidUrl.getUrl());

    @InjectMocks
    GitCloneRequestService gitServiceHandler;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Test
    void cloneGitRepositoryByBranchSync() throws Exception {

        final File file = new File("git-clone");
        try {
            Assertions.assertThrows(GitBranchNotFoundException.class, () ->
                    gitServiceHandler.cloneGitRepositoryByBranchSync(
                            command,
                            gitRepositoryWithValidUrl,
                            invalidBranch,
                            file
                    )
            );

            Assertions.assertDoesNotThrow(() ->
                    gitServiceHandler.cloneGitRepositoryByBranchSync(
                            command,
                            gitRepositoryWithValidUrl,
                            validBranch,
                            file
                    )
            );
        } finally {
            FileUtils.deleteDirectory(file);
        }
    }

    @Test
    void cloneGitRepositoryByBranchAsync() throws Exception {
        final File file = new File("git-clone");
        try {
            gitServiceHandler.cloneGitRepositoryByBranchAsync(
                    command,
                    gitRepositoryWithValidUrl,
                    validBranch,
                    file);
        } finally {
            FileUtils.deleteDirectory(file);
        }
    }
}
