package fr.jadde.test.fmk.bundle.validation.mock.controller;

import fr.jadde.fmk.bundle.validation.annotation.Validated;
import fr.jadde.fmk.bundle.web.annotation.BodyParam;
import fr.jadde.fmk.bundle.web.annotation.RestController;
import fr.jadde.test.fmk.bundle.validation.mock.domain.MyEntity;
import io.vertx.core.Future;
import jakarta.validation.Valid;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@RestController
@Validated
public class MyWebValidatedController {

    @Path("/home")
    @POST
    public Future<MyEntity> home(final @BodyParam @Valid MyEntity body) {
        return Future.succeededFuture(body);
    }

}
