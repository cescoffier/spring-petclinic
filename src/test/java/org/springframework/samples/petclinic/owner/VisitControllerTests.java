package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.visit.VisitRepository;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 */
//@RunWith(SpringRunner.class)
//@WebMvcTest(VisitController.class)
//@QuarkusTest
public class VisitControllerTests {

    private static final int TEST_PET_ID = 1;

    //@Autowired
    //private MockMvc mockMvc;

    //@MockBean
    private VisitRepository visits;

    //@MockBean
    private PetRepository pets;

    @BeforeEach
    public void init() {
//        given(this.pets.findById(TEST_PET_ID)).willReturn(new Pet());
    }

    @Test
    public void testInitNewVisitForm() throws Exception {

//        mockMvc.perform(get("/owners/*/pets/{petId}/visits/new", TEST_PET_ID))
//            .andExpect(status().isOk())
//            .andExpect(view().name("pets/createOrUpdateVisitForm"));

    }

    @Test
    public void testProcessNewVisitFormSuccess() throws Exception {
//        mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID)
//            .param("name", "George")
//            .param("description", "Visit Description")
//        )
//            .andExpect(status().is3xxRedirection())
//            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    public void testProcessNewVisitFormHasErrors() throws Exception {
//        mockMvc.perform(post("/owners/*/pets/{petId}/visits/new", TEST_PET_ID)
//            .param("name", "George")
//        )
//            .andExpect(model().attributeHasErrors("visit"))
//            .andExpect(status().isOk())
//            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

}
