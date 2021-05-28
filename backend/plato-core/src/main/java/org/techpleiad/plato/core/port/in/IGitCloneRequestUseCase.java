package org.techpleiad.plato.core.port.in;

import org.eclipse.jgit.api.CloneCommand;
import org.techpleiad.plato.core.domain.GitRepository;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface IGitCloneRequestUseCase {

    CompletableFuture<Boolean> cloneGitRepositoryByBranchAsync(
            final CloneCommand command,
            final GitRepository gitRepository,
            final String branchName,
            final File file);

    void cloneGitRepositoryByBranchSync(
            final CloneCommand command,
            final GitRepository gitRepository,
            final String branchName,
            final File file);
}
