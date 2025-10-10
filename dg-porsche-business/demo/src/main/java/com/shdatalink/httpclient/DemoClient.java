package com.shdatalink.httpclient;


import com.shdatalink.httpclient.config.DemoClientHeaderFactory;
import com.shdatalink.utils.ResultWrapperResponseHandler;
import io.quarkus.rest.client.reactive.ClientBasicAuth;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/demo")
@RegisterRestClient
@RegisterClientHeaders(DemoClientHeaderFactory.class)
@RegisterProvider(ResultWrapperResponseHandler.class)
public interface DemoClient {

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    @ClientQueryParam(name = "other-param", value = "other")
    String hello(@RestQuery String id);
}
