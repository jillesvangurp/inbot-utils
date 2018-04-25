package io.inbot.utils;

import org.testng.annotations.Test;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class KeyPairUtilsTest {

    public void shouldCreateKeyPairAndSerializeAndParse() {
        KeyPair keyPair = KeyPairUtils.generateECKeyPair();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();


        ECPrivateKey decodedPrivateKey = KeyPairUtils.decodePrivateKey(KeyPairUtils.privateKey2String(privateKey));
        ECPublicKey decodedPublicKey = KeyPairUtils.decodePublicKey(KeyPairUtils.publicKey2String(publicKey));
        assertThat(privateKey.getEncoded()).isEqualTo(decodedPrivateKey.getEncoded());
        assertThat(publicKey.getEncoded()).isEqualTo(decodedPublicKey.getEncoded());
    }
}