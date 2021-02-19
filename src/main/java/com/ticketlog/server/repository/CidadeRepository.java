package com.ticketlog.server.repository;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.dao.CidadeDao;
import com.ticketlog.server.dao.JpaDao;
import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CidadeRepository implements CidadeDao {

    private JpaDao jpaDataAccess;

    @Autowired
    public CidadeRepository(JpaDao jpaDataAccess, JdbcTemplate jdbcTemplate) {
        this.jpaDataAccess = jpaDataAccess;
    }

    @Override
    public Cidade insertCidade(Cidade cidade) {
        return jpaDataAccess.save(cidade);
    }

    @Override
    public boolean deleteCidadeById(UUID id) {
        try {
            jpaDataAccess.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Cidade> getAll() {
        return jpaDataAccess.findAll();
    }

    @Override
    public Cidade getCidade(UUID id) {
        return jpaDataAccess.findById(id).orElse(null);
    }

    @Override
    public Cidade updateCidade(UUID id, Cidade cidade) {
        cidade.setId(id);
        return jpaDataAccess.save(cidade);
    }

    @Override
    public List<Cidade> getByUF(UF uf) {
        return jpaDataAccess.findByIdEstado(uf);
    }
}