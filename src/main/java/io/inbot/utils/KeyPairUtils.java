package io.inbot.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Helper class for dealing with ECPublic and ECPrivate key pairs.
 */
public class KeyPairUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * @return a new EC key pair with ar reasonable curve .
     */
    public static KeyPair generateECKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
            // secp384r1 is a common curve, secp521r1 is stronger but you may run into issues with e.g. Chrome: https://security.stackexchange.com/questions/100991/why-is-secp521r1-no-longer-supported-in-chrome-others
            kpg.initialize(new ECGenParameterSpec("secp384r1"), SECURE_RANDOM);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException("problem " + e.getMessage(),e);
        }
    }

    /**
     * Put this in a safe place obviously.
     * @param key the key
     * @return base 64 encoded string of the PKCS8 spec
     */
    public static String privateKey2String(PrivateKey key) {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key.getEncoded());
        return new String(Base64.getEncoder().encode(spec.getEncoded()), StandardCharsets.UTF_8);
    }

    /**
     * This is what you can share.
     * @param key the key
     * @return base 64 encoded string of the X509 spec
     */
    public static String publicKey2String(PublicKey key) {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getEncoded());
        return new String(Base64.getEncoder().encode(spec.getEncoded()), StandardCharsets.UTF_8);
    }


    /**
     * @param keyString base64 encoded X509 spec
     * @return the reconstructed public key
     */
    public static ECPublicKey decodePublicKey(String keyString) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyString.getBytes(StandardCharsets.UTF_8)));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return (ECPublicKey) keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("problem " + e.getMessage(),e);
        }
    }

    /**
     * @param keyString base64 encoded PKCS8 spec
     * @return the reconstructed private key
     */    public static ECPrivateKey decodePrivateKey(String keyString) {
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyString.getBytes(StandardCharsets.UTF_8)));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return (ECPrivateKey) keyFactory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("problem " + e.getMessage(),e);
        }
    }
}
