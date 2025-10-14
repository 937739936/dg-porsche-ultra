package com.shdatalink.jwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * JWT工具类
 **/
public class JwtUtil {

	// 过期时间一天
	public static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

	/**
	 * 校验token是否正确
	 *
	 * @param token  密钥
	 * @param secret 用户的密码
	 * @return 是否正确
	 */
	public static boolean verify(String token, String userInfo, String secret) {
		try {
			// 根据密码生成JWT效验器
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withClaim("userInfo", userInfo).build();
			// 效验TOKEN
			DecodedJWT jwt = verifier.verify(token);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	/**
	 * 获得token中的信息无需secret解密也能获得
	 *
	 * @return token中包含的用户名
	 */
	public static String getValueByKey(String token, String key) {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getClaim(key).asString();
		} catch (JWTDecodeException e) {
			return null;
		}
	}

	public static String sign(String userInfo, String secret) {
		return sign(userInfo, secret, EXPIRE_TIME);
	}

	/**
	 * 生成签名
	 *
	 * @param secret   用户的密码
	 * @return 加密的token
	 */
	public static String sign(String userInfo, String secret, Long expireTime) {
		Date date = new Date(System.currentTimeMillis() + expireTime);
		Algorithm algorithm = Algorithm.HMAC256(secret);
		return JWT.create().withClaim("userInfo", userInfo).withExpiresAt(date).sign(algorithm);

	}



	public static void main(String[] args) {
		 String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NjUzMzY1MTMsInVzZXJuYW1lIjoiYWRtaW4ifQ.xjhud_tWCNYBOg_aRlMgOdlZoWFFKB_givNElHNw3X0";
		 System.out.println(JwtUtil.getValueByKey(token, "username"));
	}
}
