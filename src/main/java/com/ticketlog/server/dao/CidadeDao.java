package com.ticketlog.server.dao;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;

public interface CidadeDao {
    Cidade saveOrUpdateCidade(Cidade Cidade);

    boolean deleteCidadeById(UUID id);

    List<Cidade> getAll();

    List<Cidade> saveList(List<Cidade> cList);

    List<Cidade> getByUF(UF uf);

    Cidade getCidade(UUID id);   
}