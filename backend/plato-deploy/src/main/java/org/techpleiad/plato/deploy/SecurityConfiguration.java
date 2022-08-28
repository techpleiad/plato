package org.techpleiad.plato.deploy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Component
@Slf4j
public class SecurityConfiguration {

    public SSLContext getSslContext(final String trustStorePath, final String trustStorePass) {
        log.info("Validating SSL Context from TrustStorePath");
        final String path = trustStorePath.toLowerCase().startsWith("file")
                ? trustStorePath.toLowerCase()
                : "file:" + trustStorePath.toLowerCase();
        try {
            return org.apache.http.ssl.SSLContextBuilder.create()
                    .loadTrustMaterial(new URL(path), trustStorePass.toCharArray()).build();
        } catch (final NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | KeyManagementException exception) {
            log.error("Exception Occured {}", exception);
            throw new RuntimeException();
        }
    }
}
