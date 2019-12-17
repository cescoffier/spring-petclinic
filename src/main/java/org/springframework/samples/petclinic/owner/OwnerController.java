/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.api.ResourcePath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Set;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@RestController
public class OwnerController {

    private final OwnerRepository owners;


    @Autowired
    public OwnerController(OwnerRepository clinicService) {
        this.owners = clinicService;
    }
/* TODO: Generates non-specific error
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }
*/
/*
    @GetMapping("/owners/new")
    public String initCreationForm(Map<String, Object> model) {
        Owner owner = new Owner();
        model.put("owner", owner);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/owners/new")
    public String processCreationForm(@Valid Owner owner, BindingResult result) {
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            this.owners.save(owner);
            return "redirect:/owners/" + owner.getId();
        }
    }
*/

// TODO: No mapping for Map<String, Object> model
// RESTEASY003200: Could not find message body reader for type: interface java.util.Map of content type: */

    @Inject
    Validator validator;

    @ResourcePath("owners/findOwners.html")
    Template findOwnersTemplate;

    @ResourcePath("owners/ownersList.html")
    Template ownersListTemplate;

    @ResourcePath("owners/ownerDetails.html")
    Template ownerDetailsTemplate;

    @ResourcePath("owners/createOrUpdateOwnerForm")
    Template createOrUpdateOwnerForm;

    @GetMapping(path = "/owners/find", produces= MediaType.TEXT_HTML_VALUE)
    public TemplateInstance initFindForm(@CookieValue String result) {
        return findOwnersTemplate.data("active", "findOwners").data("result", result);
    }

    @GetMapping(path = "/owners", produces= MediaType.TEXT_HTML_VALUE)
    public ResponseEntity processFindForm(@RequestParam String lastName) {

        Iterable<Owner> results;

        // allow parameterless GET request for /owners to return all records
        if (lastName == null || lastName.trim().isEmpty()) {
            results = this.owners.findAll();
        }
        else {
            // find owners by last name
            results = this.owners.findByLastNameContaining(lastName);
        }

        if (results.iterator().hasNext() == false) {
            // no owners found
            String forwardPath = "/owners/find";

            // Store feedback for user in a temporary cookie
            HttpCookie cookie = ResponseCookie.from("result", "No owners were found! Please try again.")
                    .path(forwardPath)
                    .maxAge(30)
                    .build();

            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, forwardPath)
                    .header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .build();

        } else if (results instanceof Collection && ((Collection)results).size() == 1) {
            // 1 owner found
            Owner owner = results.iterator().next();
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, "/owners/" + owner.getId())
                    .build();

        } else {
            // multiple owners found
            return ResponseEntity.ok(
                    ownersListTemplate
                            .data("active", "findOwners")
                            .data("owners", results).render() // Render the response and return it
            );
        }
    }

    @GetMapping(path = "/owners/{ownerId}/edit", produces= MediaType.TEXT_HTML_VALUE)
    public TemplateInstance initUpdateOwnerForm(@PathVariable("ownerId") int ownerId) {
        Owner owner = this.owners.findById(ownerId).get();
        return createOrUpdateOwnerForm.data("owner", owner);
    }

    /**
     * Because this method consumes both JSON and form data, the form request data will be converted to JSON by the
     * FormDataToJsonFilter and processed according to the object mapping implementation.
     * @param owner
     * @param ownerId
     * @return
     */
    @PostMapping(path = "/owners/{ownerId}/edit",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE },
            produces= MediaType.TEXT_HTML_VALUE)
    public ResponseEntity processUpdateOwnerForm(Owner owner, @PathVariable int ownerId) {

        Set<ConstraintViolation<Owner>> results = validator.validate(owner);
        TemplateInstance templateInstance = createOrUpdateOwnerForm.instance();

        if(!results.isEmpty()) {
            templateInstance.data("hasErrors", true);

            for (ConstraintViolation<Owner> violation : results) {
                String property = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                templateInstance.data(property + "Error", message);
            }

            templateInstance.data("owner", owner);
            templateInstance.data("active", "findOwners");
            return ResponseEntity.ok(templateInstance.render());
        }
        else {
            owner.setId(ownerId);
            this.owners.save(owner);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, "/owners/" + owner.getId())
                    .build();
        }
    }

    /**
     * Custom handler for displaying an owner.
     *
     * @param ownerId the ID of the owner to display
     * @return a ModelMap with the model attributes for the view
     */
    @GetMapping(path = "/owners/{ownerId}", produces= MediaType.TEXT_HTML_VALUE)
    public TemplateInstance showOwner(@PathVariable("ownerId") int ownerId) {
        Owner owner = this.owners.findById(ownerId).get();
        return ownerDetailsTemplate
                .data("active", "findOwners")
                .data("owner",owner);
    }
}
