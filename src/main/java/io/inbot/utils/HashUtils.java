package io.inbot.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class HashUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String base64Encode(String s) {
        byte[] bytes = Base64.encodeBase64(s.getBytes(StandardCharsets.UTF_8));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String base64Decode(String s) {
        byte[] bytes = Base64.decodeBase64(s.getBytes(UTF_8));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String sha1(Object... values) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            for (Object value : values) {
                if (value != null) {
                    String v = value.toString();
                    if (StringUtils.isNotBlank(v)) {
                        md.update(v.getBytes(UTF_8));
                    }
                }
            }

            return Base64.encodeBase64URLSafeString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String md5(Collection<?> values) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (Object value : values) {
                if (value != null) {
                    md.update(value.toString().getBytes(UTF_8));
                }
            }

            return Base64.encodeBase64URLSafeString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String md5(Object... values) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (Object value : values) {
                if (value != null) {
                    md.update(value.toString().getBytes(UTF_8));
                }
            }

            return Base64.encodeBase64URLSafeString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String md5(byte[]... values) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (byte[] value : values) {
                if (value != null) {
                    md.update(value);
                }
            }

            return Base64.encodeBase64URLSafeString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String md5Hex(Object... values) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (Object value : values) {
                if (value != null) {
                    md.update(value.toString().getBytes(UTF_8));
                }
            }

            return hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String hex(byte[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String createId() {
        // don't return the full uuid, md5 it to get something shorter.
        String id;
        id = md5(UUID.randomUUID().toString());
        // don't generate ids that start with _ (messes up couchdb) or contain the string null (messes up our tests).
        while(id.startsWith("_") || id.toLowerCase(Locale.ENGLISH).contains("null")) {
            id = md5(UUID.randomUUID().toString());
        }
        return id;
    }

    /**
     * Get a String representations of the bit in a 64 bit long.
     * @param number the long you want rendered as bits
     * @return formatted string with raw bits for the long number
     */
    public static String bitString(long number) {
        return String.format(Locale.ROOT,"%064d", new BigInteger(Long.toBinaryString(number)));
    }

    public static String secureToken() {
        String tok;
        while((tok = new BigInteger(128, SECURE_RANDOM).toString(32)).toLowerCase(Locale.ENGLISH).contains("null")) {
            ;
        }
        return tok;
    }
}
