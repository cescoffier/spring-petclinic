package org.springframework.samples.petclinic.system;

import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * For any RuntimeException, render a Qute template showing the error message.
 */
@Provider
public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {

    @ResourcePath("error.html")
    Template error;

    @Override
    public Response toResponse(RuntimeException exception) {

        exception.printStackTrace();

        return Response.ok() // ok needed to properly render HTML response
                .entity(error.data("message", exception.getMessage()).render())
                .type(MediaType.TEXT_HTML)
                .build();
    }
}
