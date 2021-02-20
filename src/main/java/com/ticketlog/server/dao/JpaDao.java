package com.ticketlog.server.dao;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDao extends JpaRepository<Cidade, UUID> {
    List<Cidade> findByIdEstadoOrderByNome(UF uf);
}