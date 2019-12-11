package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.Disabled;
import org.springframework.samples.petclinic.vet.VetRepository;

@Disabled
public class ProductionConfigurationTests {

    private VetRepository vets;
/*
    @Test
    public void testFindAll() throws Exception {
        vets.findAll();
        vets.findAll(); // served from cache
    }
 */
}
