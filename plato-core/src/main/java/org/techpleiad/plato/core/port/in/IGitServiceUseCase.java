package org.techpleiad.plato.core.port.in;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IGitServiceUseCase {

    Collection<Ref> validateGitUrl(final GitRepository gitRepository);

    void validateGitUrlAndBranches(final GitRepository gitRepository, final List<String> branches);

    HashMap<ServiceBranchData, ServiceBranchData> cloneGitRepositoryByBranchInBatch(final List<GitRepository> repositoryList, final List<String> branches);

    HashMap<ServiceBranchData, ServiceBranchData> cloneGitRepositoryByBranchInBatchAsync(final List<GitRepository> repositoryList, final List<String> branches) throws ExecutionException, InterruptedException;

    ServiceBranchData cloneGitRepositoryByBranchAsync(final GitRepository gitRepository, final String branch) throws ExecutionException, InterruptedException;

    HashMap<ServiceBranchData, ServiceBranchData> cloneGitRepositoryByBranchInBatchAsyncDifferentBranches(final List<ServiceSpec> serviceSpecs) throws ExecutionException, InterruptedException;

    void gitCheckout(ServiceBranchData serviceBranchData) throws IOException, GitAPIException;

    void gitAddCommit(ServiceBranchData serviceBranchData) throws IOException, GitAPIException;

    List<String> pushCode(ServiceBranchData serviceBranchData, ServiceSpec serviceSpec) throws IOException, GitAPIException;


}
