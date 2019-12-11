package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.*;

/**
 * Test class for the {@link VetController}
 */
@Disabled
public class VetControllerTests {

    private VetRepository vets;

    @BeforeEach
    public void setup() {
        Vet james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setId(1);
        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setId(2);
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        helen.addSpecialty(radiology);
        //given(this.vets.findAll()).willReturn(Lists.newArrayList(james, helen));
    }

    @Test
    public void testShowVetListHtml() throws Exception {
//        mockMvc.perform(get("/vets.html"))
//            .andExpect(status().isOk())
//            .andExpect(model().attributeExists("vets"))
//            .andExpect(view().name("vets/vetList"));
    }

    @Test
    public void testShowResourcesVetList() throws Exception {
//        ResultActions actions = mockMvc.perform(get("/vets.json").accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//        actions.andExpect(content().contentType("application/json;charset=UTF-8"))
//            .andExpect(jsonPath("$.vetList[0].id").value(1));
    }

    @Test
    public void testShowVetListXml() throws Exception {
//        mockMvc.perform(get("/vets.xml").accept(MediaType.APPLICATION_XML))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_XML_VALUE))
//            .andExpect(content().node(hasXPath("/vets/vetList[id=1]/id")));
    }

}
