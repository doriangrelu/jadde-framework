package fr.jadde.test.fmk.bundle.security.mock.services;

import fr.jadde.fmk.bundle.security.annotation.authorization.AnyAuthenticated;
import fr.jadde.fmk.bundle.security.annotation.authorization.AnyRole;
import fr.jadde.fmk.bundle.web.annotation.BodyParam;
import fr.jadde.fmk.bundle.web.annotation.RestController;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RestController
@AnyAuthenticated
@Path("/my-root/")
public class MyFirstController {

    @Path("/my-path/{id}/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AnyAuthenticated
    @POST
    public JsonObject myAction(final @PathParam("id") String id, final @PathParam("uuid") String uuid, final @BodyParam JsonObject body) {
        return body.put("id", id).put("uuid", uuid);
    }

    @Path("/hello/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @AnyRole({"user-default", "user-admin"})
    public JsonObject hello(final @PathParam("name") String name) {
        return new JsonObject().put("text", "hello " + name);
    }

}
