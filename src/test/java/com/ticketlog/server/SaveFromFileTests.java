package com.ticketlog.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ticketlog.server.model.Cidade;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class SaveFromFileTests {

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
	public void itShouldSaveCidadesFromFile() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "cidades.csv", "text/csv",
				new FileInputStream(new File("cidades.csv")));

		MvcResult result = mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file)).andExpect(status().isOk())
				.andReturn();

		List<Cidade> cidadeList = Arrays
				.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));
		assertThat(cidadeList.size()).isEqualTo(4);

	}

	@Test
	public void itShouldNotSaveCidadesFromFile_TodasCidadesJaExistem() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "cidades.csv", "text/csv",
				new FileInputStream(new File("cidades.csv")));

		MvcResult result = mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file)).andExpect(status().isOk())
				.andReturn();

		List<Cidade> cidadeList = Arrays
				.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));
		assertThat(cidadeList.size()).isEqualTo(4);

		result = mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file))
				.andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("Nenhuma cidade foi salva")))
				.andExpect(status().isInternalServerError()).andReturn();
		;

	}

	@Test
	public void itShouldSaveCidadesFromFile_fileWithError() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "cidades.csv", "text/csv",
				new FileInputStream(new File("cidades_with_erro.csv")));

		MvcResult result = mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file)).andExpect(status().isOk())
				.andReturn();

		List<Cidade> cidadeList = Arrays
				.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));
		assertThat(cidadeList.size()).isEqualTo(1);
	}
}