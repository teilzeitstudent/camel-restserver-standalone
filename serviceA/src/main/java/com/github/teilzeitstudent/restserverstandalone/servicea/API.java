package com.github.teilzeitstudent.restserverstandalone.servicea;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/") // automatically prepends blueprint setup of "/greeting".
public interface API {
    
    @Path("/hello/{name}")
    @GET
    @Produces({"application/xml", "application/json"})
    public void getHello(@PathParam("name") String name);
    
    @Path("/hello")
    @POST
    public void postHello(String payload);
}
