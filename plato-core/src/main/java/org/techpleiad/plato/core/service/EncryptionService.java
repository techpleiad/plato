package org.techpleiad.plato.core.service;

import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.port.in.IEncryptionServiceUseCase;

@Service
public class EncryptionService implements IEncryptionServiceUseCase {
    public GitRepository encryptGitRepository(GitRepository gitRepository){
        return gitRepository;
    }

    @Override
    public GitRepository decryptGitRepository(GitRepository gitRepository) {
        return gitRepository;
    }

}
