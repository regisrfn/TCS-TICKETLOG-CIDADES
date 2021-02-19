package com.ticketlog.server.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.service.CidadeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/cidade")
@CrossOrigin
public class CidadeController {

    private CidadeService cidadeService;

    @Autowired
    public CidadeController(CidadeService cidadeService) {
        this.cidadeService = cidadeService;
    }

    @PostMapping("save")
    public ResponseEntity<Object> saveCidade(@Valid @RequestBody Cidade cidade) {
        Cidade cidadeSaved = cidadeService.saveCidade(cidade);
        return new ResponseEntity<>(cidadeSaved, HttpStatus.OK);
    }

    @GetMapping("get")
    public List<Cidade> getAllCidades() {
        return cidadeService.getAllCidades();
    }

    @GetMapping("get/{id}")
    public Cidade getCidadeById(@PathVariable String id) {
        return cidadeService.getCidadeById(id);
    }

    @GetMapping("{uf}")
    public List<Cidade> getCidadeByUf(@PathVariable String uf) {
        return cidadeService.getCidadesPorEstado(uf);
    }

    @DeleteMapping("delete/{id}")
    public Map<String, String> deleteCidadeById(@PathVariable String id) {
        cidadeService.deleteCidadeById(id);
        return Map.of("message", "successfully operation");
    }

    @PutMapping("update/{id}")
    public Cidade updateCidade(@PathVariable String id, @Valid @RequestBody Cidade cidade) {
        return cidadeService.updateCidade(id, cidade);
    }
}