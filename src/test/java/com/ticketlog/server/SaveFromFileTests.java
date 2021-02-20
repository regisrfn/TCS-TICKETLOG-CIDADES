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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class SaveFromFileTests {

	@Autowired
	private MockMvc mockMvc;
	private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	@Test
	public void itShouldUploadAFile() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "cidades.csv", "text/csv",
				new FileInputStream(new File("cidades.csv")));

		MvcResult result = mockMvc.perform(multipart("/api/v1/cidade/savelist").file(file)).andExpect(status().isOk())
				.andReturn();

		List<Cidade> cidadeList = Arrays
				.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Cidade[].class));
		assertThat(cidadeList.size()).isEqualTo(2);

	}
}