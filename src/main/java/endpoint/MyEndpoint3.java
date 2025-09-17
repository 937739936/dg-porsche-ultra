package endpoint;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;


@Path("hello")
public class MyEndpoint3 {

    @Path("{name}/{age:\\d+}")
    @GET
    public String personalisedHello(String name, int age) {
        return "Hello " + name + " is your age really " + age + "?";
    }

    @GET
    public String genericHello() {
        return "Hello stranger";
    }

}
