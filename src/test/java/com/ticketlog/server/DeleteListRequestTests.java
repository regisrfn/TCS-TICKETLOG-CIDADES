package com.ticketlog.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticketlog.server.model.Cidade;

import org.json.JSONArray;
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
public class DeleteListRequestTests {

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
    void itShouldDeleteCidades() throws Exception {

        Cidade joinville, floripa;

        MvcResult result = mockMvc.perform(get("/api/v1/cidade/sc")).andExpect(status().isOk()).andReturn();

        List<Cidade> cidadeList = Arrays
                .asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));

        assertThat(cidadeList.size()).isEqualTo(0);

        JSONObject my_obj = new JSONObject();

        my_obj.put("uf", "SC");
        my_obj.put("nome", "Joinville");
        my_obj.put("populacao", 590400);

        result = mockMvc
                .perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();
        joinville = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);

        my_obj = new JSONObject();
        my_obj.put("uf", "SC");
        my_obj.put("nome", "Florianópolis");
        my_obj.put("populacao", 508826);

        result = mockMvc
                .perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();
        floripa = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);

        result = mockMvc.perform(get("/api/v1/cidade/sc")).andExpect(status().isOk()).andReturn();
        cidadeList = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));

        assertThat(cidadeList.size()).isEqualTo(2);

        JSONArray cidadesListJson = new JSONArray();
        my_obj = new JSONObject();
        my_obj.put("id", joinville.getId().toString());
        cidadesListJson.put(my_obj);
        my_obj = new JSONObject();
        my_obj.put("id", floripa.getId().toString());
        cidadesListJson.put(my_obj);

        result = mockMvc.perform(put("/api/v1/cidade/deletelist").contentType(MediaType.APPLICATION_JSON)
                .content(cidadesListJson.toString())).andExpect(status().isOk()).andReturn();

        cidadeList = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));

        assertThat(cidadeList.size()).isEqualTo(2);

    }
}