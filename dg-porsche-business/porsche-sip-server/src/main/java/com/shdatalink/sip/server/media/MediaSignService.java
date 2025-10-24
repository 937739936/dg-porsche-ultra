package com.shdatalink.sip.server.media;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.shdatalink.sip.server.config.SipConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

import static com.shdatalink.sip.server.common.constants.CommonConstants.PLAY_DEFAULT_EXPIRE;

@ApplicationScoped
public class MediaSignService {
    @Inject
    SipConfigProperties sipConfigProperties;

    public String sign(String stream) {
        return sign(stream, PLAY_DEFAULT_EXPIRE);
    }

    public String sign(String stream, int expire) {
        long expireAt = System.currentTimeMillis() / 1000 + expire;
        return DigestUtils.md2Hex(String.format("%s:%d:%s", stream, expireAt, sipConfigProperties.media().secret())) + "&expire=" + expireAt;
    }

    public boolean verify(String streamId, String token, Integer expire) {
        if (token == null) {
            return false;
        }
        return token.equals(sign(streamId, expire));
    }
}
