package endpoint;

import jakarta.ws.rs.*;
import org.jboss.resteasy.reactive.*;


/**
 *  http  -vf POST  ":8080/cheeses/myType;variant=1?age=20"     smell=aaa    X-Cheese-Secret-Handshake:222    Cookie:'level=20000'
 */
@Path("/cheeses/{type}")
public class MyEndpoint {

    /**
     * 全部都采用@Rest的注解,省去写参数名
     * @param type
     * @param variant
     * @param age
     * @param level
     * @param secretHandshake
     * @param smell
     * @return
     */
    @POST
    public String allParams(@RestPath String type,
                            @RestMatrix String variant,
                            @RestQuery String age,
                            @RestCookie String level,
                            @RestHeader("X-Cheese-Secret-Handshake") String secretHandshake,
                            @RestForm String smell ) {
        return type + "/" + variant + "/" + age + "/" + level + "/"
                + secretHandshake + "/" + smell;
    }

}
