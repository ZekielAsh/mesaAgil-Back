package com.ttip.mesa_agil.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeyConfig {

    @Value("${rsa.public.key:}")
    private String publicKeyPem;

    @Value("${rsa.private.key:}")
    private String privateKeyPem;

    @Bean
    public KeyPair rsaKeyPair() throws Exception {
        boolean hasPublicKey = hasText(publicKeyPem);
        boolean hasPrivateKey = hasText(privateKeyPem);

        if (hasPublicKey != hasPrivateKey) {
            throw new IllegalStateException(
                    "Both rsa.public.key and rsa.private.key must be configured, or neither of them."
            );
        }

        if (!hasPublicKey) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }

        return new KeyPair(parsePublicKey(publicKeyPem), parsePrivateKey(privateKeyPem));
    }

    @Bean
    public RSAPublicKey rsaPublicKey(KeyPair rsaKeyPair) {
        return (RSAPublicKey) rsaKeyPair.getPublic();
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey(KeyPair rsaKeyPair) {
        return (RSAPrivateKey) rsaKeyPair.getPrivate();
    }

    private RSAPublicKey parsePublicKey(String keyPem) throws Exception {
        String cleanKey = cleanPem(
                keyPem,
                "-----BEGIN PUBLIC KEY-----",
                "-----END PUBLIC KEY-----"
        );

        byte[] decoded = Base64.getDecoder().decode(cleanKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);

        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private RSAPrivateKey parsePrivateKey(String keyPem) throws Exception {
        String cleanKey = cleanPem(
                keyPem,
                "-----BEGIN PRIVATE KEY-----",
                "-----END PRIVATE KEY-----"
        );

        byte[] decoded = Base64.getDecoder().decode(cleanKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private String cleanPem(String keyPem, String beginMarker, String endMarker) {
        return keyPem
                .replace("\\n", "\n")
                .replace(beginMarker, "")
                .replace(endMarker, "")
                .replaceAll("\\s+", "");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
