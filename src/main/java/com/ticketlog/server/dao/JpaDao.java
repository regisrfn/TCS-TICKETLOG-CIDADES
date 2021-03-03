package com.ticketlog.server.dao;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDao extends JpaRepository<Cidade, UUID> {
    List<Cidade> findByIdEstadoOrderByNome(UF uf);

    List<Cidade> findByIdEstadoAndNomeContains(UF uf, String nome, Sort sort);

    List<Cidade> findByNomeContains(String nome, Sort sort);

    Page<Cidade> findByIdEstado(UF uf, Pageable pageable);

}