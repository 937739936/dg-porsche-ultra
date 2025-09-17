package endpoint;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.reactive.*;


/**
 *  http  -vf POST  ":8080/cheeses2/myType;variant=1?age=20"     smell=aaa    X-Cheese-Secret-Handshake:222    Cookie:'level=20000'
 */
@Path("/cheeses2/{type}")
public class MyEndpoint2 {


    public static class Parameters {
        @RestPath
        String type;

        @RestMatrix
        String variant;

        @RestQuery
        String age;

        @RestCookie
        String level;

        @RestHeader("X-Cheese-Secret-Handshake")
        String secretHandshake;

        @RestForm
        String smell;
    }
    @POST
    public String allParams(@BeanParam Parameters parameters) {
        return parameters.type + "/" + parameters.variant + "/" + parameters.age
                + "/" + parameters.level + "/" + parameters.secretHandshake
                + "/" + parameters.smell;
    }

}
