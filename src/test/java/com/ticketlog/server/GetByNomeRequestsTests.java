package com.ticketlog.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class GetByNomeRequestsTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM cidades");
    }

    @Test
    public void itShouldGetCidadesByNome() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cidades.csv", "text/csv",
                new FileInputStream(new File("cidades.csv")));

        mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file)).andExpect(status().isOk()).andReturn();

        JSONObject my_obj = new JSONObject();
        my_obj.put("uf", "SC");
        my_obj.put("nome", "Londrina");
        my_obj.put("populacao", 590400);

        mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/cidade/search?nome=londrina")).andExpect(status().isOk())
                .andReturn();

        List<Cidade> cidadeList = Arrays
                .asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));
        assertThat(cidadeList.size()).isEqualTo(2);

    }

    @Test
    public void itShouldGetCidadesByNome_porEstado() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cidades.csv", "text/csv",
                new FileInputStream(new File("cidades.csv")));

        mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file)).andExpect(status().isOk()).andReturn();

        MvcResult result = mockMvc.perform(get("/api/v1/cidade/pr/search?nome=procopio")).andExpect(status().isOk())
                .andReturn();

        List<Cidade> cidadeList = Arrays
                .asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));
        assertThat(cidadeList.size()).isEqualTo(1);

    }
}