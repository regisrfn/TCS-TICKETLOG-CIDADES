package com.ticketlog.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticketlog.server.model.Cidade;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class PostRequestTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM cidades");
    }

    @Test
    void itShouldSaveCidade() throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj.put("idEstado", "SC");
        my_obj.put("nome", "Joinville");
        my_obj.put("populacao", 590400);
        my_obj.put("custoCidadeUs", 1.0);


        MvcResult result = mockMvc
                .perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON)
                        .content(my_obj.toString())).andExpect(status().isOk())
                .andReturn();

        Cidade response = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);
        assertThat(response.getNome()).isEqualTo("Joinville");
        assertThat(response.getPopulacao()).isEqualTo(590400);
        assertThat(response.getIdEstado().toString()).isEqualTo("SC");
    }
}
