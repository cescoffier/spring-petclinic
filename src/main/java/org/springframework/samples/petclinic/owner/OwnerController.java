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
import org.jboss.resteasy.annotations.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MultivaluedMap;
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

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
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

            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .header(HttpHeaders.LOCATION, forwardPath)
                    .header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .build();

        } else if (results instanceof Collection && ((Collection)results).size() == 1) {
            // 1 owner found
            Owner owner = results.iterator().next();
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
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

    @PostMapping(path = "/owners/{ownerId}/edit", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public ResponseEntity processUpdateOwnerForm(@Valid @RequestBody Owner owner, @PathVariable int ownerId) {

        try {
            validator.validate(owner);
            //return new Result("Book is valid! It was validated by service method validation.");
        } catch (ConstraintViolationException e) {
            Set<ConstraintViolation<?>> exceptions = e.getConstraintViolations();

            //exceptions.iterator().next().
            //return new Result(e.getConstraintViolations());
        }


        System.out.println(owner.getAddress());

       /* if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            Owner owner = new Owner();
            owner.setId(ownerId);
            this.owners.save(owner);
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                    .header(HttpHeaders.LOCATION, "/owners/" + owner.getId())
                    .build();*/

            return ResponseEntity.ok().build();
       // }
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
