package org.springframework.samples.petclinic.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.http.HttpServerRequest;
import org.jboss.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 * This is a convenience to convert HTTP POST requests and convert the body of the request from parameters to
 * JSON content. This allows the easy implementation of form data corresponding to a Java object, and is similar to
 * how Spring maps forms to objects. It uses Jackson for the conversion. Your mileage may vary.
 *
 * Use PUT as the alternative if you do not need to map the request to an object.
 *
 * If this solution does not work for you, please consider using JAX-RS form handling mechanisms.
 *
 * Example:
 * <pre>
 *   @PostMapping(path = "/owners/{ownerId}/edit", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE })
 *   public ResponseEntity processUpdateOwnerForm(@Valid @RequestBody Owner owner, @PathVariable int ownerId) {
 * </pre>
 */
@Provider
public class FormDataToJsonFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(FormDataToJsonFilter.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Override
    public void filter(ContainerRequestContext context) {
        final String method = context.getMethod();

        // For all POST requests
        if(method.equals("POST") && context.getMediaType().equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
            // Pull out the POST query string (the entity) from the request body
           String entity = convertStreamToString(context.getEntityStream());
           // Decode the query string and convert to a Map
           Map<String, List<String>> params = new QueryStringDecoder(entity, false).parameters();
           // Create an ObjectMapper that flattens 1 item arrays when serialized
           ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

            try { //  Write out the JSON string from the Map
                String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(params);
                // Write the bytes for the JSON back into the request
                context.setEntityStream(new ByteArrayInputStream(jsonResult.getBytes()));
                // Change the Content-Type so the request can be correctly consumed
                context.getHeaders().putSingle("Content-Type", MediaType.APPLICATION_JSON);

            } catch (JsonProcessingException e) {
                LOG.error("Error converting POST body to JSON object.", e);
                context.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
            }
        }
    }
}