package com.shdatalink.web.utils;

import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.inject.spi.CDI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

@Slf4j
public class IpUtil {

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr() {
        // 通过CDI获取当前请求上下文
        HttpServerRequest request = CDI.current().select(HttpServerRequest.class).get();

        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.remoteAddress().host();
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR ", e);
        }
        if (StringUtils.isNotEmpty(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        return ip;

    }

    public static boolean validate(String rtspUrl) {
        try {
            URI uri = new URI(rtspUrl);
            String host = uri.getHost();
            int port = uri.getPort() != -1 ? uri.getPort() : 554;

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
                if (!socket.isConnected()) return false;

                OutputStream out = socket.getOutputStream();
                String request = String.format("OPTIONS %s RTSP/1.0\r\nCSeq: 1\r\n\r\n", rtspUrl);
                out.write(request.getBytes());

                InputStream in = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String response = reader.readLine();
                return response != null && response.contains("200 OK");
            }
        } catch (Exception e) {
            return false;
        }
    }

}
