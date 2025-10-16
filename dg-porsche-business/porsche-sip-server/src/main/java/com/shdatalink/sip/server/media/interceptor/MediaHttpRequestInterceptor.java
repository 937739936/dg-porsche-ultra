package com.shdatalink.sip.server.media.interceptor;

import com.shdatalink.sip.server.config.SipConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MediaHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final String REQUEST_HEAD_TOKEN_NAME = "secret";

    @Autowired
    private SipConfigProperties sipConfigProperties;


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String reqeustHost = request.getURI().getHost();
        int requestPort = request.getURI().getPort();
        String mediaIp = sipConfigProperties.getMedia().getIp();
        int mediaPort = sipConfigProperties.getMedia().getPort();
        if (reqeustHost.equals(mediaIp) && requestPort == mediaPort) {
            HttpHeaders headers = request.getHeaders();
            String token = sipConfigProperties.getMedia().getSecret();
            headers.add(REQUEST_HEAD_TOKEN_NAME, token);
        }
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }
}
