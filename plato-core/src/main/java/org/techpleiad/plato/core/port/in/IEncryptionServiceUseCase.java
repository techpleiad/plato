package org.techpleiad.plato.core.port.in;

public interface IEncryptionServiceUseCase {
    String encrypt(String input);

    String decrypt(String cipherText);
}
