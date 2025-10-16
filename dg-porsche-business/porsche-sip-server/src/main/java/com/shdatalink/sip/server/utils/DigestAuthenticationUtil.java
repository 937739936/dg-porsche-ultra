package com.shdatalink.sip.server.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.sip.address.URI;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.WWWAuthenticateHeader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Random;

import static com.shdatalink.sip.server.gb28181.core.bean.constants.SipConstant.REGISTER_DIGEST_MD5_ALGORITHM;


@Slf4j
@UtilityClass
public class DigestAuthenticationUtil {
    public static final String DEFAULT_SCHEME = "Digest";
    private static final MessageDigest messageDigest;
    /**
     * to hex converter
     */
    private static final char[] toHex = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Default constructor.
     * @throws NoSuchAlgorithmException
     */
    static {
        try {
            messageDigest = MessageDigest.getInstance(REGISTER_DIGEST_MD5_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHexString(byte[] b) {
        int pos = 0;
        char[] c = new char[b.length * 2];
        for (byte value : b) {
            c[pos++] = toHex[(value >> 4) & 0x0F];
            c[pos++] = toHex[value & 0x0f];
        }
        return new String(c);
    }

    /**
     * Generate the challenge string.
     *
     * @return a generated nonce.
     */
    private static String generateNonce() {
        long time = Instant.now().toEpochMilli();
        Random rand = new Random();
        long pad = rand.nextLong();
        String nonceString = Long.valueOf(time).toString() + Long.valueOf(pad).toString();
        byte[] mdBytes = messageDigest.digest(nonceString.getBytes());
        return toHexString(mdBytes);
    }

    @SneakyThrows
    public static WWWAuthenticateHeader generateChallenge(String realm, boolean auth, String algorithm) {
        WWWAuthenticateHeader proxyAuthenticate = SipUtil.getHeaderFactory().createWWWAuthenticateHeader(DEFAULT_SCHEME);
        if (auth) {
            proxyAuthenticate.setParameter("qop", "auth");
        }
        proxyAuthenticate.setParameter("realm", realm);
        proxyAuthenticate.setParameter("nonce", generateNonce());
        proxyAuthenticate.setParameter("algorithm", algorithm);
        return proxyAuthenticate;
    }

    /*


     认证标准公式:
       A1 = username : realm : password
       A2 = method : uri
       response = MD5( MD5(A1) : nonce : MD5(A2) )



      带Qoq的认证公式(多了nc 和 cnonce两个字段):
       A1 = username : realm : password
       A2 = method : uri
       response = MD5( MD5(A1) : nonce : nc : cnonce : qop : MD5(A2) )

           cnonce: 客户端随机数
           nc: nonce 使用次数（第一次为 00000001）
     */
    public static boolean doAuthenticatePlainTextPassword(String method, AuthorizationHeader authorizationHeader, String devicePassword) {
        String realm = authorizationHeader.getRealm().trim();
        String username = authorizationHeader.getUsername().trim();
        URI uri = authorizationHeader.getURI();
        if (uri == null) {
            return false;
        }
        // 服务器端挑战生成的随机数
        String nonce = authorizationHeader.getNonce();
        // qop 保护质量 包含auth（默认的）和auth-int（增加了报文完整性检测）两种策略
        String qop = authorizationHeader.getQop();
        // 客户端随机数，这是一个不透明的字符串值，由客户端提供，并且客户端和服务器都会使用，以避免用明文文本。
        // 这使得双方都可以查验对方的身份，并对消息的完整性提供一些保护
        String cnonce = authorizationHeader.getCNonce();

        // nonce计数器，是一个16进制的数值，表示同一nonce下客户端发送出请求的数量
        int nc = authorizationHeader.getNonceCount();

        String ncStr = String.format("%08x", nc).toUpperCase();
        String a1 = StringUtils.joinWith(":", username, realm, devicePassword);
        String a2 = StringUtils.joinWith(":", method.toUpperCase(), uri.toString());
        byte[] mdbytes = messageDigest.digest(a1.getBytes());
        String ha1 = toHexString(mdbytes);
        mdbytes = messageDigest.digest(a2.getBytes());
        String ha2 = toHexString(mdbytes);
        log.info("客户端Authorization: {}", authorizationHeader);
        String f1 = toHexString(messageDigest.digest(StringUtils.joinWith(":", ha1, nonce, ncStr, cnonce, qop, ha2).getBytes()));
        String response = authorizationHeader.getResponse();
        if (f1.equals(response)) {
            log.info("Qop公式认证成功");
            return true;
        } else if (toHexString(messageDigest.digest(StringUtils.joinWith(":", ha1, nonce, ha2).getBytes())).equals(response)) {
            log.info("标准认证公式成功");
            return true;
        } else {
            log.error("设备认证失败");
            return false;
        }
    }

}
