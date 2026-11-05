package com.jseed.web.starter.encryption;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

/**
 * AES key for this application instance
 *
 * @author cyrus
 * @date: 2024-07-12
 */
public class ApplicationAes {

    private final AES AES;

    public ApplicationAes(String aesKey) {
        AES = SecureUtil.aes(Base64.decode(aesKey));
    }

    /**
     * Encrypts using AES.
     *
     * @param content
     * @return
     */
    public String encrypt(String content) {
        return AES.encryptHex(content);
    }

    /**
     * Decrypts using AES.
     *
     * @param ciphertext
     * @return
     */
    public String decrypt(String ciphertext) {
        return AES.decryptStr(ciphertext);
    }

    /**
     * Generates an AES key.
     *
     * @return
     */
    public static String getAesKey() {
        return Base64.encode(SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded());
    }
}
