package com.shdatalink.sip.server.media;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.shdatalink.sip.server.config.SipConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Date;

@ApplicationScoped
public class MediaSignService {
    @Inject
    SipConfigProperties sipConfigProperties;

    public String sign(String stream) {
        String secret = sipConfigProperties.media().secret();

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(sipConfigProperties.server().id())
                .withSubject(stream)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 86400)) // 5分钟有效期
                .sign(algorithm);
    }

    public boolean verify(String streamId, String token) {
        String secret = sipConfigProperties.media().secret();
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier build = JWT.require(algorithm)
                .withIssuer(sipConfigProperties.server().id())
                .withSubject(streamId)
                .build();
        try {
            DecodedJWT decodedJWT = build.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
