// SPDX-License-Identifier: Apache-2.0
package com.jseed.crypto.starter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-GCM envelope v1: version byte + 12-byte random IV + authenticated ciphertext.
 *
 * @author cyrus
 */
public class CryptoService {
    private final SecureRandom random = new SecureRandom();
    private final BCryptPasswordEncoder passwords;
    private final String configuredKey;

    public CryptoService(int strength, String configuredKey) {
        passwords = new BCryptPasswordEncoder(strength);
        if (configuredKey != null) {
            key(configuredKey);
        }
        this.configuredKey = configuredKey;
    }

    public String encrypt(String plaintext) {
        return encrypt(plaintext, requireConfiguredKey());
    }

    public String decrypt(String ciphertext) {
        return decrypt(ciphertext, requireConfiguredKey());
    }

    private String requireConfiguredKey() {
        if (configuredKey == null) {
            throw new IllegalStateException("Set jseed.crypto.aes-key to use the configured key");
        }
        return configuredKey;
    }

    public String generateKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, random);
            return Base64.getEncoder().encodeToString(generator.generateKey().getEncoded());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public String encrypt(String plaintext, String base64Key) {
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key(base64Key), new GCMParameterSpec(128, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(
                            ByteBuffer.allocate(13 + encrypted.length)
                                    .put((byte) 1)
                                    .put(iv)
                                    .put(encrypted)
                                    .array());
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("Encryption failed", e);
        }
    }

    public String decrypt(String ciphertext, String base64Key) {
        byte[] value = Base64.getUrlDecoder().decode(ciphertext);
        if (value.length < 29 || value[0] != 1)
            throw new IllegalArgumentException("Invalid ciphertext envelope");
        ByteBuffer input = ByteBuffer.wrap(value, 1, value.length - 1);
        byte[] iv = new byte[12];
        input.get(iv);
        byte[] encrypted = new byte[input.remaining()];
        input.get(encrypted);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key(base64Key), new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("Ciphertext authentication failed", e);
        }
    }

    private SecretKeySpec key(String key) {
        byte[] bytes = Base64.getDecoder().decode(key);
        if (bytes.length != 16 && bytes.length != 24 && bytes.length != 32)
            throw new IllegalArgumentException("AES key must contain 16, 24 or 32 bytes");
        return new SecretKeySpec(bytes, "AES");
    }

    public String sha256(String text) {
        try {
            return HexFormat.of()
                    .formatHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    public String hmacSha256(String text, String base64Key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(Base64.getDecoder().decode(base64Key), "HmacSHA256"));
            return Base64.getEncoder()
                    .encodeToString(mac.doFinal(text.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("Invalid signing key", e);
        }
    }

    public boolean verifyHmac(String text, String signature, String base64Key) {
        try {
            return MessageDigest.isEqual(
                    Base64.getDecoder().decode(signature),
                    Base64.getDecoder().decode(hmacSha256(text, base64Key)));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String hashPassword(String password) {
        return passwords.encode(password);
    }

    public boolean matchesPassword(String password, String encoded) {
        return passwords.matches(password, encoded);
    }
}
