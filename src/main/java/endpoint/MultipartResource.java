package endpoint;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;


/**
 * http -vf POST :8080/multipart    description=hello   person='{"firstName":"zhangsan","lastName":"aaaa"}'   image@"~/Downloads/a.yaml"
 */
@Path("multipart")
public class MultipartResource {

    @POST
    public void multipart(@RestForm String description,
                          @RestForm("image") File file,
                          @RestForm @PartType(MediaType.APPLICATION_JSON) Person person) {
        // do something
        System.out.println(description);
        System.out.println(file.length());
        System.out.println(person);
    }

    public static class Person {
        public String firstName;
        public String lastName;

        @Override
        public String toString() {
            return "Person{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
        }
    }
}
