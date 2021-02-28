package com.ticketlog.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
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

        my_obj.put("uf", "SC");
        my_obj.put("nome", "JoinviLle");
        my_obj.put("populacao", 590400);

        MvcResult result = mockMvc
                .perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        Cidade response = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);
        assertThat(response.getNome()).isEqualTo("joinville");
        assertThat(response.getPopulacao()).isEqualTo(590400);
        assertThat(response.getIdEstado().toString()).isEqualTo("SC");
        assertThat(response.getCustoCidadeUs()).isEqualTo(64.6792E6, within(100.0));

        my_obj = new JSONObject();

        my_obj.put("uf", "PR");
        my_obj.put("nome", "joinville");
        my_obj.put("populacao", 590400);

        result = mockMvc
                .perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        response = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);
        assertThat(response.getNome()).isEqualTo("joinville");
        assertThat(response.getPopulacao()).isEqualTo(590400);
        assertThat(response.getIdEstado().toString()).isEqualTo("PR");
        assertThat(response.getCustoCidadeUs()).isEqualTo(64.6792E6, within(100.0));
    }


    @Test
    void itShouldSaveCidade_withExtraSpaces() throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj.put("uf", "SC");
        my_obj.put("nome", "JoinviLle   ");
        my_obj.put("populacao", 590400);

        MvcResult result = mockMvc
                .perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        Cidade response = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);
        assertThat(response.getNome()).isEqualTo("joinville");
        assertThat(response.getPopulacao()).isEqualTo(590400);
        assertThat(response.getIdEstado().toString()).isEqualTo("SC");
        assertThat(response.getCustoCidadeUs()).isEqualTo(64.6792E6, within(100.0));

        my_obj = new JSONObject();

        my_obj.put("uf", "PR");
        my_obj.put("nome", "Sao Jose     dos Pinhais");
        my_obj.put("populacao", 590400);

        result = mockMvc
                .perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        response = objectMapper.readValue(result.getResponse().getContentAsString(), Cidade.class);
        assertThat(response.getNome()).isEqualTo("sao jose dos pinhais");
        assertThat(response.getPopulacao()).isEqualTo(590400);
        assertThat(response.getIdEstado().toString()).isEqualTo("PR");
        assertThat(response.getCustoCidadeUs()).isEqualTo(64.6792E6, within(100.0));
    }

    @Test
    void itShouldNotSaveCidade_mesmoNome() throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj.put("uf", "SC");
        my_obj.put("nome", "Joinville");
        my_obj.put("populacao", 590400);

        mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.nome", Is.is("Nome da cidade ja existe no estado")))
                .andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void itShouldNotSaveCidade_populacaoInvalida() throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj.put("uf", "SC");
        my_obj.put("nome", "Joinville");
        my_obj.put("populacao", "abs");

        mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.populacao", Is.is("Valor de população invalido")))
                .andExpect(status().isBadRequest()).andReturn();

    }

}
