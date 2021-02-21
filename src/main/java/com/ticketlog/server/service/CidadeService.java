package com.ticketlog.server.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ticketlog.server.dao.CidadeDao;
import com.ticketlog.server.exception.ApiRequestException;
import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;
import com.ticketlog.server.model.PCusto;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class CidadeService {

    private CidadeDao cidadeDao;
    private RestTemplate restTemplate;
    private Dotenv dotenv;
    private String apiCusto;
    private String custoId = "95d1421a-4071-4a9b-b3d3-603865c89097";
    private FileStorageService storageService;

    @Autowired
    public CidadeService(CidadeDao cidadeDao, RestTemplate restTemplate, Dotenv dotenv,
            FileStorageService storageService) {
        this.dotenv = dotenv;
        this.cidadeDao = cidadeDao;
        this.restTemplate = restTemplate;
        this.apiCusto = this.dotenv.get("API_CUSTO_URL");
        this.storageService = storageService;
    }

    public Cidade saveCidade(Cidade cidade) {
        try {
            Double custo = calcCusto(cidade);
            cidade.setCustoCidadeUs(custo);
        } catch (Exception e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return cidadeDao.saveOrUpdateCidade(cidade);
    }

    public List<Cidade> getAllCidades() {
        try {
            return cidadeDao.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRequestException("Listagem das cidades não pode ser efetuada",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Cidade getCidadeById(String id) {
        try {
            UUID cidadeId = UUID.fromString(id);
            Cidade cidade = cidadeDao.getCidade(cidadeId);
            if (cidade == null)
                throw new ApiRequestException("Cidade não encontrada", HttpStatus.NOT_FOUND);
            return cidade;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new ApiRequestException("Formato de id invalido", HttpStatus.BAD_REQUEST);
        }

    }

    public boolean deleteCidadeById(String id) {
        try {
            UUID cidadeId = UUID.fromString(id);
            Cidade cidade = cidadeDao.getCidade(cidadeId);
            if (cidade == null)
                throw new ApiRequestException("Cidade não encontrada", HttpStatus.NOT_FOUND);
            else if (cidade.getIdEstado().equals("RS"))
                throw new ApiRequestException("Cidades do Rio Grande do Sul não podem ser removidas",
                        HttpStatus.FORBIDDEN);
            boolean ok = cidadeDao.deleteCidadeById(cidadeId);
            return ok;
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Formato de id invalido", HttpStatus.BAD_REQUEST);
        }
    }

    public List<Cidade> getCidadesPorEstado(String estadoUf) {
        try {
            UF uf = UF.valueOf(estadoUf.toUpperCase());
            return cidadeDao.getByUF(uf);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRequestException("Listagem das cidades não pode ser efetuada",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Cidade> saveCidadesFromFile(MultipartFile file) {
        String filename, savedFilePath, extFile;
        extFile = getExtension(file.getOriginalFilename());
        filename = UUID.randomUUID() + "." + extFile;
        List<Cidade> cidadesList = new ArrayList<>();

        savedFilePath = storageService.save(file, filename);
        CsvSchema cidadeLineSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Cidade> cidadeLines;
        try {
            cidadeLines = csvMapper.readerFor(Cidade.class).with(cidadeLineSchema).readValues(new File(savedFilePath));
            while (cidadeLines.hasNext()) {
                try {
                    cidadesList.add(saveCidade(cidadeLines.next()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiRequestException("Erro na leitura do arquivo");
        }

        return cidadesList;
    }

    public List<Cidade> deleteListOfCidades(List<Cidade> cidadesList) {
        List<Cidade> deletedCidades = new ArrayList<>();
        for (Cidade c : cidadesList) {
            try {
                deleteCidadeById(c.getId().toString());
                deletedCidades.add(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedCidades;
    }

    /////////////////////////////////////// PRIVATE METHODS
    private Double calcCusto(Cidade cidade) {
        try {
            PCusto response = restTemplate.getForObject(this.apiCusto + "/get/" + this.custoId, PCusto.class);
            long corte = response.getValorCorte();
            double desconto = response.getDesconto();
            double custoPorPessoa = response.getCustoPorPessoa();

            if (cidade.getPopulacao() <= corte)
                return custoPorPessoa * cidade.getPopulacao();

            return corte * custoPorPessoa + (cidade.getPopulacao() - corte) * custoPorPessoa * (1 - desconto / 100);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Não foi possivel obter os parametros de custo");
        }

    }

    private String getExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

}