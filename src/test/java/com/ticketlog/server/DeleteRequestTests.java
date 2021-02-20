package com.ticketlog.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticketlog.server.model.Cidade;

import org.hamcrest.core.Is;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteRequestTests {

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
        void itShouldDeleteCidadeById() throws Exception {

                JSONObject my_obj = new JSONObject();

                my_obj.put("uf", "SC");
                my_obj.put("nome", "Joinville");
                my_obj.put("populacao", 590400);

                MvcResult result = mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                Cidade cidadeResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);

                mockMvc.perform(delete("/api/v1/cidade/delete/" + cidadeResponse.getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Is.is("Operação realizada com sucesso")))
                                .andExpect(status().isOk()).andReturn();

        }

        @Test
        void itShouldDeleteCidadeFromRs() throws Exception {

                JSONObject my_obj = new JSONObject();

                my_obj.put("uf", "RS");
                my_obj.put("nome", "Farroupilha");
                my_obj.put("populacao", 72331);

                MvcResult result = mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                Cidade cidadeResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);

                mockMvc.perform(delete("/api/v1/cidade/delete/" + cidadeResponse.getId()))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError",
                                                Is.is("Cidades do Rio Grande do Sul não podem ser removidas")))
                                .andExpect(status().isForbidden()).andReturn();

        }
}