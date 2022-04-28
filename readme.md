# Jadde Framework (based on Vert.X)

[Vert.X official documentation](https://vertx.io)

Jadde Framework (*Java Framework*) is based exclusively on the Vert.X library. This Framework allows to provide an
abstraction of the library, and also provides a framework allowing to get rid of the configuration part, creation of
resources etc...

The framework is intended to be light and to specialize in the creation of microservice type applications.

For the moment the project is still under development, feel free to contribute. This project is open source.

# Usage

Jadde Framework is as mentioned above based on VertX. The mechanics are very simple. You just have to get the Maven
dependency (fr.jadde:jadde-app:x.x.x-RELEASE).

Getting this dependency will allow you to do two things:

- Add a custom Jadde bundle
- Create your application (SOA, MicroService or other)

## Rest API

Creating an API Rest is very simple.

Start by creating a Maven project, and add the following two dependencies:

    <dependencies>
        <!-- The Jadde Core application -->
        <dependency>
            <groupId>fr.jadde</groupId>
            <artifactId>jadde-app</artifactId>
            <version>x.x.x-RELEASE</version>
            <scope>provided</scope>
        </dependency>
        <!-- The Jadde Web bundle -->
        <dependency>
            <groupId>fr.jadde</groupId>
            <artifactId>jadde-web-bundle</artifactId>
            <version>x.x.x-RELEASE</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

Once the two dependencies are created, you can simply declare your first Rest API as follows:

    package fr.jadde.demo.mypackage
    
    import fr.jadde.fmk.bundle.web.annotation.BodyParam;
    import fr.jadde.fmk.bundle.web.annotation.RestController;
    import io.vertx.core.json.JsonObject;
    
    import javax.ws.rs.*;
    import javax.ws.rs.core.MediaType;
    
    @RestController
    @Path("/prefix-path")
    public class MyFirstController {
    
        @Path("/my-path/{id}/{name}")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        @POST
        public JsonObject myAction(final @PathParam("id") String id, final @PathParam("name") String name, final @BodyParam JsonObject body) {
            return body.put("id", id).put("name", name);
        }
    
    
        @Path("/hello/{name}")
        @Produces(MediaType.APPLICATION_JSON)
        @GET
        public JsonObject hello(final @PathParam("name") String name) {
            return new JsonObject().put("text", "hello " + name);
        }
    
    }

**It is important to note that the supported annotations are those provided by JAX-RS (JSR 339). The support of most of
the annotations is provided by the Framework**

Serialization is done by our friend Jackson (annotations like @JsonIgnore, @JsonCreator etc...) are available by default
without the need to get any additional dependencies. Since the Jadde Framework is based on VertX, you can inject the
RoutingContext containing all the raw information collected by VertX into a method of a controller.

Here is an example:

    package fr.jadde.demo.mypackage
    
    import fr.jadde.fmk.bundle.web.annotation.BodyParam;
    import fr.jadde.fmk.bundle.web.annotation.RestController;
    import io.vertx.core.json.JsonObject;
    
    import javax.ws.rs.*;
    import javax.ws.rs.core.MediaType;
    
    @RestController
    @Path("/prefix-path")
    public class MyFirstController {
    
        @Path("/my-path/{id}/{name}")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        @POST
        public JsonObject myAction(final @PathParam("id") String id, final @PathParam("name") String name, final RoutingContext context) {
            return context.getBodyAsJson().put("id", id).put("name", name);
        }
    
    }
