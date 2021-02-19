package com.ticketlog.server.service;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.dao.CidadeDao;
import com.ticketlog.server.exception.ApiRequestException;
import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CidadeService {

    private CidadeDao cidadeDao;

    @Autowired
    public CidadeService(CidadeDao cidadeDao) {
        this.cidadeDao = cidadeDao;
    }

    public Cidade saveCidade(Cidade cidade) {
        try {
            return cidadeDao.insertCidade(cidade);
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

    public Cidade updateCidade(String id, Cidade cidade) {
        try {
            UUID cidadeId = UUID.fromString(id.toUpperCase());
            return cidadeDao.updateCidade(cidadeId, cidade);
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Formato de id invalido", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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


    
}