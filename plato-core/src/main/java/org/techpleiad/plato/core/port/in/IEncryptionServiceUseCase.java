package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.GitRepository;

public interface IEncryptionServiceUseCase {
    GitRepository encryptGitRepository(GitRepository gitRepository);

    GitRepository decryptGitRepository(GitRepository gitRepository);


}
