package io.inbot.utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Helper methods to assist with encryption/decryption using AES that implements https://tools.ietf.org/html/rfc2898 style encryption/decryption with
 * some sane defaults.
 *
 * The plaintext is encrypted with a secure random salt so that every encrypted value is unique.
 *
 * You can choose between using your own 256 bit key or using a salt + password from which a 256 bit key is constructed using PBKDF2WithHmacSHA1.
 *
 * The encrypted value includes a md5 content hash that is used by the decrypt to verify the value is correct. This
 * guarantees the decrypt fails with an IllegalArgumentException if the key/password is incorrect and prevents the
 * algorithm from returning random garbage instead.
 */
public class AESUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\$");

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String generateAesKey() {
        byte[] newKey = new byte[32];
        SECURE_RANDOM.nextBytes(newKey);
        return Base64.encodeBase64String(newKey);
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.
     *
     * @param salt
     *            salt is used together with the password to construct a 256 bit SecretKey
     * @param password
     *            the password
     * @param input
     *            the iv as a hex string followed by '$' followed by the encrypted text
     * @return plain text.
     */
    public static String decrypt(String salt, String password, String input) {
        return decryptBouncyCastle(getKey(salt, password), input);
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.
     *
     * @param key256Bits
     *            256 bit key
     * @param encrypted
     *            encrypted text
     * @return plain text
     */
    public static String decrypt(byte[] key256Bits, String encrypted) {
        SecretKey secretKey = new SecretKeySpec(key256Bits, "AES");
        return decryptBouncyCastle(secretKey, encrypted);
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.
     *
     * @param keyBase64
     *            256 bit key base64 encoded
     * @param encrypted
     *            encrypted text
     * @return plain text
     */
    public static String decrypt(String keyBase64, String encrypted) {
        return decrypt(Base64.decodeBase64(keyBase64.getBytes(StandardCharsets.UTF_8)), encrypted);
    }

    private static String encryptBouncyCastle(SecretKey secret, String plainText) {
        try {
            // prepending with md5 hash allows us to do an integrity check on decrypt to prevent returning garbage if the decrypt key is incorrect
            String md5 = HashUtils.md5(plainText);
            plainText = md5 + plainText;

            // the iv acts as a per use salt, this ensures things encrypted with the same key always have a unique salt
            // 128 bit iv because NIST AES is standardized with 128 bit blocks and iv needs to match block size, even when using 256 bit key
            byte[] iv = new byte[16];
            SECURE_RANDOM.nextBytes(iv);

            // setup cipher parameters with key and IV
            byte[] key = secret.getEncoded();


            // setup AES cipher in CBC mode with PKCS7 padding
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());

            cipher.reset();
            cipher.init(true, new ParametersWithIV(new KeyParameter(key), iv));

            byte[] plainTextBuf = plainText.getBytes(StandardCharsets.UTF_8);

            byte[] buf = new byte[cipher.getOutputSize(plainTextBuf.length)];

            int len = cipher.processBytes(plainTextBuf, 0, plainTextBuf.length, buf, 0);
            len += cipher.doFinal(buf, len);

            // copy the encrypted part of the buffer to out
            byte[] out = new byte[len];
            System.arraycopy(buf, 0, out, 0, len);

            // iv$encrypted
            return byteArrayToHexString(iv) + "$" + new String(Base64.encodeBase64URLSafe(out), StandardCharsets.UTF_8);
        } catch (DataLengthException | InvalidCipherTextException e) {
            throw new IllegalStateException("cannot encrypt", e);
        }
    }

    private static String decryptBouncyCastle(SecretKey secret, String input) {
        try {
            // Convert url-safe base64 to normal base64, remove carriage returns
            input = input.replaceAll("-", "+").replaceAll("_", "/").replaceAll("\r", "").replaceAll("\n", "");

            String[] splitInput = SPLIT_PATTERN.split(input);
            byte[] iv = hexStringToByteArray(splitInput[0]);
            byte[] encrypted = Base64.decodeBase64(splitInput[1]);

            // get raw key from password and salt
            byte[] key = secret.getEncoded();

            // setup cipher parameters with key and IV
            KeyParameter keyParam = new KeyParameter(key);
            CipherParameters params = new ParametersWithIV(keyParam, iv);

            // setup AES cipher in CBC mode with PKCS7 padding
            BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());

            cipher.reset();
            cipher.init(false, params);

            // create a temporary buffer to decode into (it'll include padding)
            byte[] buf = new byte[cipher.getOutputSize(encrypted.length)];
            int len = cipher.processBytes(encrypted, 0, encrypted.length, buf, 0);
            len += cipher.doFinal(buf, len);

            // lose the padding
            byte[] out = new byte[len];
            System.arraycopy(buf, 0, out, 0, len);
            // lose the salt

            String plaintext = new String(out, StandardCharsets.UTF_8);
            String md5Hash = plaintext.substring(0, 22);
            String plainTextWithoutHash = plaintext.substring(22);
            if (md5Hash.equals(HashUtils.md5(plainTextWithoutHash))) {
                return plainTextWithoutHash;
            } else {
                // it's possible to decrypt to garbage with the wrong key; the md5 check helps detecting that
                throw new IllegalArgumentException("wrong aes key - incorrect content hash");
            }
        } catch (DataLengthException e) {
            throw new IllegalStateException("buffer not big enough",e);
        } catch (InvalidCipherTextException e) {
            throw new IllegalArgumentException("wrong password");
        }
    }

    /**
     * Encrypt the plain text.
     *
     * @param key256Bits
     *            256 bit key
     * @param plainText
     *            text that needs to be encrypted
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    public static String encrypt(byte[] key256Bits, String plainText) {
        SecretKey secretKey = new SecretKeySpec(key256Bits, "AES");
        return encryptBouncyCastle(secretKey, plainText);
//        return encryptLegacyJavaSecurity(secretKey, plainText);
    }

    /**
     * Encrypt the plain text.
     *
     * @param key
     *            256 bit key base64 encoded
     * @param plainText
     *            text that needs to be encrypted
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    public static String encrypt(String key, String plainText) {
        SecretKey secret = new SecretKeySpec(Base64.decodeBase64(key.getBytes(StandardCharsets.UTF_8)), "AES");
        return encryptBouncyCastle(secret, plainText);
    }

    /**
     * Encrypt the plain text.
     *
     * @param salt
     *            salt is used together with the password to construct a 256 bit SecretKey
     * @param password
     *            the secret key
     * @param plainText
     *            unencrypted text
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    public static String encrypt(String salt, String password, String plainText) {
        SecretKey secret = getKey(salt, password);
        return encryptBouncyCastle(secret, plainText);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format(Locale.ROOT,"%02X", b));
        }
        return sb.toString();
    }

    private static SecretKey getKey(String salt, String password) {
        try {
            // https://tools.ietf.org/html/rfc2898
            // sha1 with 1000 iterations and 256 bits is good enough here http://stackoverflow.com/questions/6126061/pbekeyspec-what-do-the-iterationcount-and-keylength-parameters-influence
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 1000, 256);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("cannot create key: " + e.getMessage(), e);
        }
    }
}
