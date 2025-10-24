package com.shdatalink.sip.server.media;

import com.shdatalink.sip.server.config.SipConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;

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
        return DigestUtils.md5Hex(String.format("%s:%d:%s", stream, expireAt, sipConfigProperties.media().secret())).substring(8, 24) + "&expire=" + expireAt;
    }

    public boolean verify(String streamId, String token, Integer expire) {
        if (token == null) {
            return false;
        }
        String sign = DigestUtils.md5Hex(String.format("%s:%d:%s", streamId, expire, sipConfigProperties.media().secret())).substring(8, 24);
        return token.equals(sign);
    }
}
