package io.inbot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Verification;

import java.security.interfaces.ECPublicKey;
import java.util.function.Consumer;

/**
 * Simple service class around JWT to make verifying tokens easier. This class should be easy to turn into a injected bean.
 */
public class JwtVerificationService {
    private final ECPublicKey ecPublicKey;
    private final String issuer;

    public JwtVerificationService(ECPublicKey ecPublicKey, String issuer) {
        this.ecPublicKey = ecPublicKey;
        this.issuer = issuer;
    }

    /**
     * @param token a jwt token
     * @param verificationConsumer a way for you to verify claims by passing in a lambda function that accepts a Verification on which you can call e.g. withSubject, withClaim, etc.
     * @return true if the token is valid
     * @throws IllegalArgumentException if token is malformed
     */
    public boolean isValid(String token, Consumer<Verification> verificationConsumer) throws IllegalArgumentException {
        try {
            verify(token, verificationConsumer);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * Verifies the token.
     * @param token a jwt token.
     * @param verificationConsumer a way for you to verify claims by passing in a lambda function that accepts a Verification on which you can call e.g. withSubject, withClaim, etc.
     * @throws JWTVerificationException if not valid
     * @throws IllegalArgumentException if token is malformed
     */
    public void verify(String token, Consumer<Verification> verificationConsumer) throws JWTVerificationException, IllegalArgumentException {
        Verification verifierBuilder = JWT.require(Algorithm.ECDSA512(ecPublicKey, null))
                .withIssuer(issuer);
        // apply any custom verififaction
        verificationConsumer.accept(verifierBuilder);
        JWTVerifier verifier = verifierBuilder.build();
        verifier.verify(token);
    }
}
