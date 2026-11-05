package com.jseed.core.starter.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class MD5 {
    /**
     * Create a MessageDigest for MD5.
     */
    private static MessageDigest mdInst = null;
    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static MessageDigest getMdInst() {
        if (mdInst == null) {
            try {
                mdInst = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return mdInst;
    }

    public static String encode(String s) {
        try {
            byte[] btInput = s.getBytes();
            // Update the digest with the supplied bytes.
            getMdInst().update(btInput);
            // Compute the digest.
            byte[] md = getMdInst().digest();
            // Convert the digest to a hexadecimal string.
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
