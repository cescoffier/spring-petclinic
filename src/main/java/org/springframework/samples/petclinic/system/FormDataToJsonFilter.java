package org.springframework.samples.petclinic.system;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.http.HttpServerRequest;
import org.jboss.logging.Logger;
import org.jboss.resteasy.core.ResourceMethodInvoker;

import javax.inject.Inject;
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
 * This is a convenience filter to modify HTTP POST form requests and convert the body of the request from form data
 * to JSON data. This allows the easy implementation of form data corresponding to a Java object, which allows a method
 * to be dual purpose for JSON and form data of the same properties. Jackson is used for the JSON mapping.
 *
 * Why is this needed? Using legacy form APIs in JAX-RS and Spring Web are either more cumbersome or simply don't work
 * in Quarkus at this time. This provides a simplified approach that works for basic form data POST requests.
 *
 * This work similar to the @ModelAttribute in Spring
 *
 * Example (in Spring Web):
 * <pre>
 *   @PostMapping(path = "/owners/{ownerId}/edit", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE })
 *   public ResponseEntity processUpdateOwnerForm(Owner owner, @PathVariable int ownerId) {
 * </pre>
 *
 * Feature Request: https://github.com/quarkusio/quarkus/issues/6234
 */
@Provider
public class FormDataToJsonFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(FormDataToJsonFilter.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext context) {
        // Get a handle on the Method to be invoked from this HTTP request
        ResourceMethodInvoker resourceMethodInvoker = (ResourceMethodInvoker)context.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");

        // Filter based on the method consuming both Form and JSON
        boolean hasForm = false;
        boolean hasJson = false;

        // Cycle through consumes MediaTypes
        for(MediaType mediaType : resourceMethodInvoker.getConsumes()) {
            if(mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
                hasForm = true;
            }
            else if(mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
                hasJson = true;
            }
        }

        // For all form POST requests that have consumes both Form and JSON and content of APPLICATION_FORM_URLENCODED_TYPE
        if(context.getMethod().equals("POST")
                && hasForm && hasJson
                && context.getMediaType().equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {

            // Pull out the POST query string (the entity) from the request body
           String entity = convertStreamToString(context.getEntityStream());

           // Decode the query string and convert to a Map
           Map<String, List<String>> params = new QueryStringDecoder(entity, false).parameters();

           // Make sure arrays are unwrapped appropriately with global objectMapper when JSON is converted to POJO
           objectMapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

           try { //  Write out the JSON string from the Map
                String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(params);

                // Write the bytes for the JSON back into the request
                context.setEntityStream(new ByteArrayInputStream(jsonResult.getBytes()));

                // Change the Content-Type so the request can be correctly consumed
                context.getHeaders().putSingle("Content-Type", MediaType.APPLICATION_JSON);

                // Now the HTTP POST request will be processed as JSON data rather than form data.
            } catch (JsonProcessingException e) {
                LOG.error("Error converting POST body to JSON object.", e);
                context.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
            }
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}