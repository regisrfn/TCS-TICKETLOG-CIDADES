package com.ticketlog.server.repository;

import java.util.List;
import java.util.UUID;

import com.ticketlog.server.dao.CidadeDao;
import com.ticketlog.server.dao.JpaDao;
import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.Cidade.UF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public Cidade saveOrUpdateCidade(Cidade cidade) {
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
        Sort sort = Sort.by("nome").descending();
        return jpaDataAccess.findAll(sort);
    }

    @Override
    public Cidade getCidade(UUID id) {
        return jpaDataAccess.findById(id).orElse(null);
    }

    @Override
    public List<Cidade> getByUF(UF uf) {
        return jpaDataAccess.findByIdEstadoOrderByNome(uf);
    }

    @Override
    public Page<Cidade> getCidadesPage(UF uf, String orderBy, boolean asc, int pageNumber, int size) {
        Sort sort;
        if (asc)
            sort = Sort.by(orderBy).ascending();
        else
            sort = Sort.by(orderBy).descending();
        PageRequest pageRequest = PageRequest.of(pageNumber, size, sort);
        return jpaDataAccess.findByIdEstado(uf, pageRequest);
    }

    @Override
    public List<Cidade> getByNome(String nome) {
        Sort sort = Sort.by("idEstado").ascending();
        return jpaDataAccess.findByNome(nome, sort);
    }
}