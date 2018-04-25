package io.inbot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@Test
public class JwtTest {

    public static final String INBOT_TEST = "Inbot-Test";

    JwtTokenCreationService jwtService;
    JwtVerificationService verificationService;

    @BeforeMethod
    public void before() {
        KeyPair keyPair = KeyPairUtils.generateECKeyPair();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();

        jwtService = new JwtTokenCreationService(privateKey, publicKey, INBOT_TEST,10000);
        verificationService = new JwtVerificationService(publicKey, INBOT_TEST);

    }

    public void shouldVerifyATokenWithSubject()  {
        String jwtToken = jwtService.create(jwt->jwt.withSubject("Alice"));

        assertThat(verificationService.isValid(jwtToken, verification -> {})).isTrue();
        assertThat(verificationService.isValid(jwtToken, verification -> verification.withSubject("Alice"))).isTrue();
        assertThat(verificationService.isValid(jwtToken, verification -> verification.withSubject("Bob"))).isFalse();
    }

    public void shouldRejectExpiredToken() {
        String jwtToken = jwtService.create(jwt->jwt
                .withIssuedAt(new Date(System.currentTimeMillis()-100000))
                .withExpiresAt(new Date(System.currentTimeMillis()-10000))
        );
        assertThat(verificationService.isValid(jwtToken, verification -> {})).isFalse();

    }

    public void shouldRejectTokensThatHaveBeenTamperedWith() {
        String jwtToken = jwtService.create(jwt->jwt.withSubject("Alice"));

        DecodedJWT decode = JWT.decode(jwtToken);
        String header = decode.getHeader();
        String payload = decode.getPayload();
        String token = decode.getToken();
        String signature = decode.getSignature();

        String reconstructed = header + "." + payload + "." + signature;
        assertThat(reconstructed).isEqualTo(jwtToken);
        // both tokens are valid
        assertThat(verificationService.isValid(jwtToken, verification -> {})).isTrue();
        assertThat(verificationService.isValid(reconstructed, verification -> {})).isTrue();

        // if we mess with the payload, the signature is no longer valid
        String tampered=header+"."+HashUtils.base64Encode(HashUtils.base64Decode(payload).replace("Alice","Bob")) + "." + signature;
        assertThat(verificationService.isValid(tampered, verification -> {})).isFalse();
    }
}
