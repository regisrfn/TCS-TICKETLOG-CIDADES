package com.ticketlog.server.service;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.dao.CidadeDao;
import com.ticketlog.server.exception.ApiRequestException;
import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;
import com.ticketlog.server.model.PCusto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class CidadeService {

    private CidadeDao cidadeDao;
    private RestTemplate restTemplate;
    private Dotenv dotenv;
    private String apiCusto;
    private String custoId = "95d1421a-4071-4a9b-b3d3-603865c89097";

    @Autowired
    public CidadeService(CidadeDao cidadeDao, RestTemplate restTemplate, Dotenv dotenv) {
        this.dotenv = dotenv;
        this.cidadeDao = cidadeDao;
        this.restTemplate = restTemplate;
        this.apiCusto = this.dotenv.get("API_CUSTO_URL");
    }

    public Cidade saveCidade(Cidade cidade) {
        try {
            Double custo = calcCusto(cidade);
            cidade.setCustoCidadeUs(custo);
            return cidadeDao.saveOrUpdateCidade(cidade);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRequestException("Cidade não pode ser salva", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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

    public Cidade getCidadeById(String cidadeUf) {
        try {
            UUID cidadeId = UUID.fromString(cidadeUf);
            Cidade cidade = cidadeDao.getCidade(cidadeId);
            if (cidade == null)
                throw new ApiRequestException("Cidade não encontrada", HttpStatus.NOT_FOUND);
            return cidade;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new ApiRequestException("Formato de id invalido", HttpStatus.BAD_REQUEST);
        }

    }

    public boolean deleteCidadeById(String cidadeUf) {
        try {
            UUID cidadeId = UUID.fromString(cidadeUf);
            boolean ok = cidadeDao.deleteCidadeById(cidadeId);
            if (!ok)
                throw new ApiRequestException("Cidade não encontrada", HttpStatus.NOT_FOUND);
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

}