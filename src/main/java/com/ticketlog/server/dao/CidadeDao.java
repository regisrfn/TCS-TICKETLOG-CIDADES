package com.ticketlog.server.dao;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;

import org.springframework.data.domain.Page;

public interface CidadeDao {
    Cidade saveOrUpdateCidade(Cidade Cidade);

    boolean deleteCidadeById(UUID id);

    List<Cidade> getAll();

    List<Cidade> getByUF(UF uf);

    List<Cidade> getByNome(String nome);

    List<Cidade> getCidadesList(UF uf, String nome);

    Cidade getCidade(UUID id);

    Page<Cidade> getCidadesPage(UF uf, String orderBy, boolean asc, int pageNumber, int size);

}