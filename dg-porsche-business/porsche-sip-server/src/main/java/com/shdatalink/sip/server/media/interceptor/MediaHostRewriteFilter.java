package com.shdatalink.sip.server.media.interceptor;

import com.shdatalink.sip.server.config.SipConfigProperties;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.net.URI;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class MediaHostRewriteFilter implements ClientRequestFilter {
    @Inject
    SipConfigProperties sipConfigProperties;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        URI oldUri = requestContext.getUri();
        URI newUri = UriBuilder.fromUri(oldUri.getPath())
                .scheme(oldUri.getScheme())
                .host(sipConfigProperties.media().ip())
                .port(sipConfigProperties.media().port())
                .build();
        requestContext.setUri(newUri);
    }
}