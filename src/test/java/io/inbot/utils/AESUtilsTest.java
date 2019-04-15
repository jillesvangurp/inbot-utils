package io.inbot.utils;

import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@Test
public class AESUtilsTest {

    public void shouldConvertToFromHex() {
        byte[] iv = new byte[20];
        new SecureRandom().nextBytes(iv);
        String hexString = AESUtils.byteArrayToHexString(iv);
        byte[] bytes = AESUtils.hexStringToByteArray(hexString);
        assertThat(bytes).isEqualTo(iv);
    }

    public void shouldEncryptAndDecryptUsingSaltAndPassword() {
        assertThat(AESUtils.decrypt("salt", "password", AESUtils.encrypt("salt", "password", "secretsecret"))).isEqualTo("secretsecret");
    }

    public void shouldEncryptAndDecryptUsing256BitKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        String plainText = "secretsecret";
        String encrypted = AESUtils.encrypt(key, plainText);
        assertThat(AESUtils.decrypt(key, encrypted)).isEqualTo(plainText);
    }

    public void shouldEncryptAndDecryptUsingGenerated256BitBase64Key() {
        String plainText = "secretsecret";
        String key = AESUtils.generateAesKey();
        String encrypted = AESUtils.encrypt(key, plainText);
        assertThat(AESUtils.decrypt(key, encrypted)).isEqualTo(plainText);
    }

    public void shouldPreserveCompatibilityWithPreJdk101AES256() {
        String encrypted="B8B32CCF0471B3FA305C3DF04D879E79$5or2YFCkt50EtCnqakG4KyMRItErJyIHhflVlNZTAVLxLwSWFEHDeWfpvyZ4z8lG";
        String decrypted = AESUtils.decrypt("salt", "password", encrypted);
        String expected = "secretsecret";
        assertThat(decrypted.length()).isEqualTo(expected.length());
        assertThat(decrypted).isEqualTo(expected);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldPreserveCompatibilityWithPreJdk101AES256WrongPassword() {
        String encrypted="B8B32CCF0471B3FA305C3DF04D879E79$5or2YFCkt50EtCnqakG4KyMRItErJyIHhflVlNZTAVLxLwSWFEHDeWfpvyZ4z8lG";
        AESUtils.decrypt("salt", "wrongpassword", encrypted);
    }

    public void shouldNotGenerateSameEncryptedEver() {
        Set<String> encrypted= new HashSet<>();
        for(int i=0;i<100;i++) {
            // each encrypted value is hashed
            encrypted.add(AESUtils.encrypt("salt", "password", "secretsecret"));
        }
        assertThat(encrypted.size()).isEqualTo(100);
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldThrowExecptionForIncorrectPassword() {
        AESUtils.decrypt("salt", "passwd", AESUtils.encrypt("salt", "password", "secretsecret"));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void shouldThrowExecptionForIncorrectSalt() {
        AESUtils.decrypt("slt", "password", AESUtils.encrypt("salt", "password", "secretsecret"));
    }
}
