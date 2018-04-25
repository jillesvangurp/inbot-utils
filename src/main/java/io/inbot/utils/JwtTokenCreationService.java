package io.inbot.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Simple service class to make creating tokens easy.
 */
public class JwtTokenCreationService {

    private final ECPrivateKey ecPrivateKey;
    private final ECPublicKey ecPublicKey;
    private String issuer;
    private int defaultExpirationMs;

    /**
     *
     * @param ecPrivateKey private key
     * @param ecPublicKey public key
     * @param issuer issuer String
     * @param defaultExpirationMs default expiration time from now in ms.
     */
    public JwtTokenCreationService(ECPrivateKey ecPrivateKey, ECPublicKey ecPublicKey, String issuer, int defaultExpirationMs) {
        this.ecPrivateKey = ecPrivateKey;
        this.ecPublicKey = ecPublicKey;
        this.issuer = issuer;
        this.defaultExpirationMs = defaultExpirationMs;
    }

    /**
     * Creates a JWT token.
     * @param jwtBuilderConsumer a way for you to customise the token using a lambda function
     * @return token with the issuer and default expiration set.
     */
    public String create(Consumer<JWTCreator.Builder> jwtBuilderConsumer) {
        Date issueTime = new Date();
        Date expirationTime = new Date(issueTime.getTime() + defaultExpirationMs);
        JWTCreator.Builder builder = JWT.create()
                // opinionated creation, we enforce using an expiration
                .withIssuer(issuer)
                .withIssuedAt(issueTime)
                .withExpiresAt(expirationTime);
        // apply any additional token customisation
        jwtBuilderConsumer.accept(builder);
        // use the strongest algirithm;
        return builder.sign(Algorithm.ECDSA512(ecPublicKey, ecPrivateKey));
    }
}
