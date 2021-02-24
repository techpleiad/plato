package org.techpleiad.plato.core.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techpleiad.plato.core.domain.GitRepository;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {

    @InjectMocks
    private EncryptionService encryptionService;

    @Test
    void givenGitRepository_whenEncryptionDecryption_thenEqual()throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        String url = "url";
        String password = "password";
        String username = "username";

        GitRepository encryptGitRepository= encryptionService.encryptGitRepository(GitRepository.builder()
                .username(username)
                .password(password)
                .url(url)
                .build());

        GitRepository decryptGitRepository = encryptionService.decryptGitRepository(encryptGitRepository);

        Assertions.assertEquals(url, decryptGitRepository.getUrl());
        Assertions.assertEquals(password, decryptGitRepository.getPassword());
        Assertions.assertEquals(username, decryptGitRepository.getUsername());

    }
}