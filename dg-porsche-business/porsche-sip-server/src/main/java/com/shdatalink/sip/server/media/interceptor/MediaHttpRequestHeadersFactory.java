package com.shdatalink.sip.server.media.interceptor;

import com.shdatalink.sip.server.config.SipConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import java.util.UUID;

@ApplicationScoped
public class MediaHttpRequestHeadersFactory implements ClientHeadersFactory {
    private static final String REQUEST_HEAD_TOKEN_NAME = "secret";
    @Inject
    SipConfigProperties sipConfigProperties;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> clientOutgoingHeaders) {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        String token = sipConfigProperties.media().secret();
        result.add(REQUEST_HEAD_TOKEN_NAME, token);
        return result;
    }
}
