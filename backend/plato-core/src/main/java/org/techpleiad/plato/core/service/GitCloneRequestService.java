package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.exceptions.GitBranchNotFoundException;
import org.techpleiad.plato.core.port.in.IGitCloneRequestUseCase;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class GitCloneRequestService implements IGitCloneRequestUseCase {

    private static final String REF_HEAD = "refs/heads/";

    @Async("AsyncExecutor")
    @Override
    public CompletableFuture<Boolean> cloneGitRepositoryByBranchAsync(
            final CloneCommand command,
            final GitRepository gitRepository,
            final String branchName,
            final File file) {

        cloneGitRepositoryByBranchSync(command, gitRepository, branchName, file);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void cloneGitRepositoryByBranchSync(final CloneCommand command, final GitRepository gitRepository, final String branchName, final File file) {

        log.info("clone repo : {} | branch : {}", gitRepository.getUrl(), branchName);
        try (final Git git = command
                .setBranchesToClone(Collections.singletonList(REF_HEAD + branchName))
                .setBranch(REF_HEAD + branchName)
                .setDirectory(file)
                .call()) {
            git.getRepository().close();
            log.info("cloned repo : {} | branch : {}", gitRepository.getUrl(), branchName);
        } catch (final Exception exception) {
            throw new GitBranchNotFoundException(
                    exception.getMessage(),
                    gitRepository.getUrl(),
                    Collections.singletonList(branchName)
            );
        }
    }
}
