package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello1() {
        return "Hello from Quarkus REST";
    }


    @GET
    @Path("/hello2")
    @Produces(MediaType.APPLICATION_JSON)
    public Map hello2() {
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",22);
        map.put("birthday", LocalDateTime.now());
        return map;
    }
}
