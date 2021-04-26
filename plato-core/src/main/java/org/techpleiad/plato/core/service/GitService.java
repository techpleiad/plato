package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.Branch;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.exceptions.GitBranchNotFoundException;
import org.techpleiad.plato.core.exceptions.GitRepositoryNotFoundException;
import org.techpleiad.plato.core.port.in.IFileServiceUserCase;
import org.techpleiad.plato.core.port.in.IGitCloneRequestUseCase;
import org.techpleiad.plato.core.port.in.IGitServiceUseCase;
import org.techpleiad.plato.core.port.out.IGetGitCredentialsPort;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GitService implements IGitServiceUseCase {

    @Autowired
    private IFileServiceUserCase fileService;

    @Autowired
    private IGetGitCredentialsPort getCredentialUseCase;

    @Autowired
    private IGitCloneRequestUseCase gitCloneRequestUseCase;

    @ExecutionTime
    @Override
    public HashMap<ServiceBranchData, ServiceBranchData> cloneGitRepositoryByBranchInBatch(final List<GitRepository> gitRepositoryList, final List<String> branches) {

        final Map<String, GitRepository> gitRepositoryMap = new TreeMap<>();
        gitRepositoryList.forEach(gitRepository ->
                gitRepositoryMap.putIfAbsent(gitRepository.getUrl(), gitRepository)
        );

        final HashMap<ServiceBranchData, ServiceBranchData> mapRepoToDirectory = new HashMap<>();
        gitRepositoryMap.values().forEach(gitRepository -> {
            validateGitUrlAndBranches(gitRepository, branches);
            branches.forEach(branch -> {

                final String directory = fileService.generateDirectory(getGitRepositoryName(gitRepository.getUrl()), branch);
                final File directoryFile = fileService.generateFileFromLocalDirectoryPath(directory);

                final ServiceBranchData serviceBranchData = ServiceBranchData.builder()
                        .repository(gitRepository.getUrl())
                        .branch(branch)
                        .directory(directoryFile)
                        .build();

                gitCloneRequestUseCase.cloneGitRepositoryByBranchSync(
                        createCloneCommand(gitRepository),
                        gitRepository,
                        branch,
                        directoryFile
                );
                mapRepoToDirectory.put(serviceBranchData, serviceBranchData);
            });
        });
        return mapRepoToDirectory;
    }

    @ExecutionTime
    @Override
    public HashMap<ServiceBranchData, ServiceBranchData> cloneGitRepositoryByBranchInBatchAsync(final List<GitRepository> gitRepositoryList, final List<String> branches) throws ExecutionException, InterruptedException {

        final Map<String, GitRepository> gitRepositoryMap = new TreeMap<>();
        gitRepositoryList.forEach(gitRepository ->
                gitRepositoryMap.putIfAbsent(gitRepository.getUrl(), gitRepository)
        );

        final HashMap<ServiceBranchData, ServiceBranchData> mapRepoToDirectory = new HashMap<>();

        final List<CompletableFuture<Boolean>> completableFutures = new LinkedList<>();
        for (final GitRepository gitRepository : gitRepositoryMap.values()) {
            validateGitUrlAndBranches(gitRepository, branches);
            for (final String branch : branches) {
                final String directory = fileService.generateDirectory(getGitRepositoryName(gitRepository.getUrl()), branch);
                final File directoryFile = fileService.generateFileFromLocalDirectoryPath(directory);

                final ServiceBranchData serviceBranchData = ServiceBranchData.builder()
                        .repository(gitRepository.getUrl())
                        .branch(branch)
                        .directory(directoryFile)
                        .build();

                completableFutures.add(
                        gitCloneRequestUseCase.cloneGitRepositoryByBranchAsync(
                                createCloneCommand(gitRepository),
                                gitRepository,
                                branch,
                                directoryFile
                        )
                );
                mapRepoToDirectory.put(serviceBranchData, serviceBranchData);
            }
        }

        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
        final CompletableFuture<List<Boolean>> allCompletableFutures = allFutures.thenApply(future ->
                completableFutures.stream().map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
        allCompletableFutures.thenApply(ArrayList::new).get();

        return mapRepoToDirectory;
    }

    @ExecutionTime
    @Override
    public ServiceBranchData cloneGitRepositoryByBranchAsync(final GitRepository gitRepository, final String branch) throws ExecutionException, InterruptedException {

        List<String> branches = Collections.singletonList(branch);
        final List<CompletableFuture<Boolean>> completableFutures = new LinkedList<>();
        validateGitUrlAndBranches(gitRepository, branches);
        final String directory = fileService.generateDirectory(getGitRepositoryName(gitRepository.getUrl()), branch);
        final File directoryFile = fileService.generateFileFromLocalDirectoryPath(directory);

        final ServiceBranchData serviceBranchData = ServiceBranchData.builder()
                .repository(gitRepository.getUrl())
                .branch(branch)
                .directory(directoryFile)
                .build();

        completableFutures.add(
                gitCloneRequestUseCase.cloneGitRepositoryByBranchAsync(
                        createCloneCommand(gitRepository),
                        gitRepository,
                        branch,
                        directoryFile
                )
        );

        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
        final CompletableFuture<List<Boolean>> allCompletableFutures = allFutures.thenApply(future ->
                completableFutures.stream().map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
        allCompletableFutures.thenApply(ArrayList::new).get();
        return serviceBranchData;
    }


    @ExecutionTime
    @Override
    public HashMap<ServiceBranchData, ServiceBranchData> cloneGitRepositoryByBranchInBatchAsyncDifferentBranches(final List<ServiceSpec> serviceSpecs) throws ExecutionException, InterruptedException {

        final Map<String, ServiceSpec> gitRepositoryMap = new TreeMap<>();

        for (final ServiceSpec serviceSpec : serviceSpecs) {
            gitRepositoryMap.putIfAbsent(serviceSpec.getGitRepository().getUrl(), serviceSpec);
        }

        final HashMap<ServiceBranchData, ServiceBranchData> mapRepoToDirectory = new HashMap<>();

        final List<CompletableFuture<Boolean>> completableFutures = new LinkedList<>();
        for (final ServiceSpec serviceSpec : gitRepositoryMap.values()) {
            validateGitUrlAndBranches(serviceSpec.getGitRepository(), serviceSpec.getBranches().stream().map(Branch::getName).collect(Collectors.toList()));
            for (final Branch branch : serviceSpec.getBranches()) {
                final String directory = fileService.generateDirectory(getGitRepositoryName(serviceSpec.getGitRepository().getUrl()), branch.getName());
                final File directoryFile = fileService.generateFileFromLocalDirectoryPath(directory);

                final ServiceBranchData serviceBranchData = ServiceBranchData.builder()
                        .repository(serviceSpec.getGitRepository().getUrl())
                        .branch(branch.getName())
                        .directory(directoryFile)
                        .build();

                completableFutures.add(
                        gitCloneRequestUseCase.cloneGitRepositoryByBranchAsync(
                                createCloneCommand(serviceSpec.getGitRepository()),
                                serviceSpec.getGitRepository(),
                                branch.getName(),
                                directoryFile
                        )
                );
                mapRepoToDirectory.put(serviceBranchData, serviceBranchData);
            }
        }

        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
        final CompletableFuture<List<Boolean>> allCompletableFutures = allFutures.thenApply(future ->
                completableFutures.stream().map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );
        allCompletableFutures.thenApply(ArrayList::new).get();

        return mapRepoToDirectory;
    }

    @Override
    public Collection<Ref> validateGitUrl(final GitRepository gitRepository) {
        try {
            return createLsRemoteCommand(gitRepository).call();
        } catch (final GitAPIException exception) {
            throw new GitRepositoryNotFoundException(
                    exception.getMessage(),
                    gitRepository.getUrl()
            );
        }
    }

    @Override
    public void validateGitUrlAndBranches(final GitRepository gitRepository, final List<String> serviceBranches) {
        final Collection<Ref> refs = validateGitUrl(gitRepository);
        final Set<String> remoteBranchList = refs
                .stream().map(branchRef -> extractRemoteBranchName(branchRef.getName())
                ).collect(Collectors.toSet());

        final List<String> missingBranches = serviceBranches.stream()
                .filter(branch -> !remoteBranchList.contains(branch)).collect(Collectors.toList());

        if (!missingBranches.isEmpty()) {
            throw new GitBranchNotFoundException("remote branches missing",
                    gitRepository.getUrl(), missingBranches
            );
        }
    }

    private String getGitRepositoryName(final String repository) {
        return repository.substring(repository.lastIndexOf("/") + 1, repository.lastIndexOf("."));
    }

    private CloneCommand createCloneCommand(GitRepository gitRepository) {
        gitRepository = populateGitCredentials(gitRepository);
        final CloneCommand cloneCommand = Git.cloneRepository().setURI(gitRepository.getUrl());
        if (!StringUtils.isEmptyOrNull(gitRepository.getPassword()) && !StringUtils.isEmptyOrNull(gitRepository.getUsername())) {
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRepository.getUsername(), gitRepository.getPassword()));
        }
        return cloneCommand;
    }

    private LsRemoteCommand createLsRemoteCommand(GitRepository gitRepository) {
        gitRepository = populateGitCredentials(gitRepository);
        final LsRemoteCommand lsRemoteCommand = Git.lsRemoteRepository().setRemote(gitRepository.getUrl());
        if (!StringUtils.isEmptyOrNull(gitRepository.getPassword()) && !StringUtils.isEmptyOrNull(gitRepository.getUsername())) {
            lsRemoteCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRepository.getUsername(), gitRepository.getPassword()));
        }
        return lsRemoteCommand;
    }

    private String extractRemoteBranchName(final String branch) {
        return branch.substring(branch.lastIndexOf("/") + 1);
    }

    private GitRepository populateGitCredentials(final GitRepository gitRepository) {
        if (gitRepository.isUseDefault()) {
            final String defaultUsername = getCredentialUseCase.getUsername();
            final String defaultGitPassword = getCredentialUseCase.getPassword();
            return GitRepository.builder()
                    .url(gitRepository.getUrl())
                    .username(defaultUsername)
                    .password(defaultGitPassword)
                    .build();
        }

        return gitRepository;

    }
}
