# Jadde Framework (based on Vert.X) -- WIP

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

## IoC Container

### Dependency management

The framework provides a simple and lightweight IoC container. It has simple features like registering bean in the
container. It also provides injection of registered beans into the container.

It also offers a conflict management during the injection of bean (if we find two beans corresponding to the request, a
system of qualifier or bean by default exists)

Here is an example of what can be done:

    @JaddeBean
    @Qualifier("aQualifier")
    public class MySecondService {
    
        @Inject
        private MyFirstService myFirstService;

        @Inject(qualifier = "myQualifier")
        private MyConflictService myConflictService;

        @Inject(required = false)
        private MyOptionalService myOptionalService;
    
        @Inject
        private JaddeApplicationContext applicationContext;
    
        @Inject
        private List<MyFirstService> myFirstServices;

    }

    @JaddeBean
    @Default
    public class ADefaultBean {

    }

To declare a bean you must use the JaddeBean annotation. This way the POJO will be automatically instantiated and added
in the container (**it is important that the POJO has a default constructor or no argument**)

By default when using @Inject the target dependency is mandatory (**if it is optional you have to use the flat required
= false, in the annotation usage**). Also if a conflict exists the container will try to find the annotated bean with
@Default. If none of the beans have this annotation and no qualifier is used, then the application will not start

### Lifecycle

Once the container has resolved and injected all the beans, it will automatically trigger the call to methods annotated
with **@Start** within your beans:

    @JaddeBean
    public class MySecondService {
    
        @Inject
        private MyFirstService myFirstService;

        @Start
        public void start() {
            System.out.print("Hello world !")
        }

    }

The method must not take any parameter and must not return anything.

There is also a hook when the application is stopped. When the application shuts down, the container will automatically
trigger the call to the methods annotated with **@Stop**:

    @JaddeBean
    public class MySecondService {
    
        @Inject
        private MyFirstService myFirstService;

        @Stop
        public void stop() {
            System.out.print("Hello world ! It's end !")
        }

    }

The conditions in terms of signature of the method are the same as for the start method

In the future evolution it is planned to provide an autowiring on the parameters.

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
        public Future<JsonObject> myAction(final @PathParam("id") String id, final @PathParam("name") String name, final @BodyParam JsonObject body) {
            return Future.succeededFuture(body.put("id", id).put("name", name));
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

**The return type of the method can be an asynchronous wrapper (VertX future, Java CompletableFuture), but it is not
mandatory. If it is not the case the Framework will handle the asynchronous part alone**

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
