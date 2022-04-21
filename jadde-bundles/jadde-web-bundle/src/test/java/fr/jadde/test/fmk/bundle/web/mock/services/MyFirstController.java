package fr.jadde.test.fmk.bundle.web.mock.services;

import fr.jadde.fmk.bundle.web.annotation.RestController;
import jakarta.enterprise.context.ApplicationScoped;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@ApplicationScoped
@RestController
public class MyFirstController {

    @Path("/my-path/{id}")
    @Produces("application/text")
    @POST
    public String myAction(final @PathParam("id") String id) {
        return id;
    }

}
