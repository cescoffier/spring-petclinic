package org.springframework.samples.petclinic;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@ApplicationScoped
public class Startup {

    private final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    void onStart(@Observes StartupEvent ev) {
        log.info("ReBot is stopping...");
    }

    void onStop(@Observes ShutdownEvent ev) {
        log.info("ReBot is stopping...");
    }

}
