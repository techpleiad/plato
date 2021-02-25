package org.techpleiad.plato.core.service;

import org.springframework.stereotype.Service;
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

    Cipher cipher;

    public EncryptionService() throws NoSuchAlgorithmException, NoSuchPaddingException {

        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        secretKey = keyGenerator.generateKey();

        final byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);

        final String algorithm = "AES/CBC/PKCS5Padding";

        cipher = Cipher.getInstance(algorithm);
    }

    @Override
    public String encrypt(final String input) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        final byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    @Override
    public String decrypt(final String cipherText) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        final byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }
}
