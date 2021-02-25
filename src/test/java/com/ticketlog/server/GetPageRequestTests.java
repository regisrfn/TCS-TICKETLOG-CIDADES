package com.ticketlog.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.PageResponse;

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
public class GetPageRequestTests {

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
        void itShouldGetCidadesByUf() throws Exception {

                MvcResult result = mockMvc.perform(get("/api/v1/cidade/sc/page")).andExpect(status().isOk())
                                .andReturn();

                PageResponse pageResponse = (objectMapper.readValue(result.getResponse().getContentAsString(),
                                PageResponse.class));

                assertThat(pageResponse.getCidadesList().size()).isEqualTo(0);

                JSONObject my_obj = new JSONObject();

                my_obj.put("uf", "SC");
                my_obj.put("nome", "Joinville");
                my_obj.put("populacao", 590400);

                mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                my_obj = new JSONObject();

                my_obj.put("uf", "SC");
                my_obj.put("nome", "Florianópolis");
                my_obj.put("populacao", 508826);

                mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                result = mockMvc.perform(get("/api/v1/cidade/sc/page?number=1&size=1")).andExpect(status().isOk())
                                .andReturn();

                pageResponse = (objectMapper.readValue(result.getResponse().getContentAsString(), PageResponse.class));

                assertThat(pageResponse.getCidadesList().size()).isEqualTo(1);
                assertThat(pageResponse.getTotalPages()).isEqualTo(2);
                assertThat(pageResponse.getPageNumber()).isEqualTo(1);
        }

        @Test
        void itShouldGetCidadesPage_orderByPopulacao() throws Exception {
                MvcResult result = mockMvc.perform(get("/api/v1/cidade/sc/page")).andExpect(status().isOk())
                                .andReturn();

                PageResponse pageResponse = (objectMapper.readValue(result.getResponse().getContentAsString(),
                                PageResponse.class));

                assertThat(pageResponse.getCidadesList().size()).isEqualTo(0);

                JSONObject my_obj = new JSONObject();

                my_obj.put("uf", "SC");
                my_obj.put("nome", "Joinville");
                my_obj.put("populacao", 590400);

                mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                my_obj = new JSONObject();

                my_obj.put("uf", "SC");
                my_obj.put("nome", "Florianopolis");
                my_obj.put("populacao", 508826);

                mockMvc.perform(post("/api/v1/cidade/save").contentType(MediaType.APPLICATION_JSON)
                                .content(my_obj.toString())).andExpect(status().isOk()).andReturn();

                result = mockMvc.perform(get("/api/v1/cidade/sc/page?number=0&size=2&sort=populacao&asc=false"))
                                .andExpect(status().isOk()).andReturn();

                pageResponse = (objectMapper.readValue(result.getResponse().getContentAsString(), PageResponse.class));

                assertThat(pageResponse.getCidadesList().size()).isEqualTo(2);
                assertThat(pageResponse.getTotalPages()).isEqualTo(1);
                assertThat(pageResponse.getPageNumber()).isEqualTo(0);
                assertThat(pageResponse.getCidadesList().get(0).getPopulacao()).isEqualTo(590400);
                assertThat(pageResponse.getCidadesList().get(1).getPopulacao()).isEqualTo(508826);

                result = mockMvc.perform(get("/api/v1/cidade/sc/page?number=0&size=2&sort=nome&asc=false"))
                                .andExpect(status().isOk()).andReturn();

                pageResponse = (objectMapper.readValue(result.getResponse().getContentAsString(), PageResponse.class));

                assertThat(pageResponse.getCidadesList().size()).isEqualTo(2);
                assertThat(pageResponse.getTotalPages()).isEqualTo(1);
                assertThat(pageResponse.getPageNumber()).isEqualTo(0);
                assertThat(pageResponse.getCidadesList().get(0).getNome()).isEqualTo("joinville");
                assertThat(pageResponse.getCidadesList().get(1).getNome()).isEqualTo("florianopolis");
        }

        @Test
        public void itShouldSaveCidadesFromFile_10CidadeDoParana() throws Exception {
                MockMultipartFile file = new MockMultipartFile("file", "cidades.csv", "text/csv",
                                new FileInputStream(new File("cidades.csv")));

                MvcResult result = mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file))
                                .andExpect(status().isOk()).andReturn();

                List<Cidade> cidadeList = Arrays.asList(
                                objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));
                assertThat(cidadeList.size()).isEqualTo(10);

                result = mockMvc.perform(get("/api/v1/cidade/pr/page?number=0&size=5&sort=nome&asc=true"))
                                .andExpect(status().isOk()).andReturn();

                PageResponse pageResponse = (objectMapper.readValue(result.getResponse().getContentAsString(), PageResponse.class));

                assertThat(pageResponse.getCidadesList().size()).isEqualTo(5);
                assertThat(pageResponse.getTotalPages()).isEqualTo(2);
                assertThat(pageResponse.getPageNumber()).isEqualTo(0);
                assertThat(pageResponse.getCidadesList().get(0).getNome()).isEqualTo("araucaria");

        }
}