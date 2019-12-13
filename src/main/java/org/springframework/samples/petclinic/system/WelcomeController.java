package org.springframework.samples.petclinic.system;


import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @Autowired
    Template welcome;

    @GetMapping(produces= MediaType.TEXT_HTML_VALUE)
    public TemplateInstance welcome() {
        return welcome.data("active", "welcome");
    }
}
