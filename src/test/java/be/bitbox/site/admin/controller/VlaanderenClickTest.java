package be.bitbox.site.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
class VlaanderenClickTest {

  @MockitoBean
  private VlaanderenClick controller;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  void testRegister() throws Exception {
    String jsonBody = """
            {
                "id": "12345",
                "voornaam": "John",
                "achternaam": "Doe",
                "email": "john.doe@example.com",
                "dienst": "consultancy"
            }""";

    mockMvc.perform(MockMvcRequestBuilders.post("/vlaanderen.click/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}