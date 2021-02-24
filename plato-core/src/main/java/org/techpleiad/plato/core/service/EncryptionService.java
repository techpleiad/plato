package org.techpleiad.plato.core.service;

import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.domain.GitRepository;
import org.techpleiad.plato.core.port.in.IEncryptionServiceUseCase;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService implements IEncryptionServiceUseCase {

    private final SecretKey secretKey;

    private final IvParameterSpec ivParameterSpec;

    private final String algorithm;

    public EncryptionService() throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        secretKey = keyGenerator.generateKey();

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);

        algorithm = "AES/CBC/PKCS5Padding";
    }

    @Override
    public GitRepository encryptGitRepository(GitRepository gitRepository) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return GitRepository.builder()
                .url(encrypt(algorithm, gitRepository.getUrl(), secretKey, ivParameterSpec))
                .username(encrypt(algorithm, gitRepository.getUsername(), secretKey, ivParameterSpec))
                .password(encrypt(algorithm, gitRepository.getPassword(), secretKey, ivParameterSpec))
                .build();
    }

    @Override
    public GitRepository decryptGitRepository(GitRepository gitRepository) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return GitRepository.builder()
                .url(decrypt(algorithm, gitRepository.getUrl(), secretKey, ivParameterSpec))
                .username(decrypt(algorithm, gitRepository.getUsername(), secretKey, ivParameterSpec))
                .password(decrypt(algorithm, gitRepository.getPassword(), secretKey, ivParameterSpec))
                .build();
    }

    private String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    private String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }
}
