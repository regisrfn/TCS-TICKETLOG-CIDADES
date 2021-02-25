package com.ticketlog.server.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.ticketlog.server.model.Cidade;
import com.ticketlog.server.model.PageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("savelist")
    public ResponseEntity<Object> saveCidadeList(@RequestParam("file") MultipartFile file) {
        List<Cidade> cidadesSaved = cidadeService.saveCidadesFromFile(file);
        return new ResponseEntity<>(cidadesSaved, HttpStatus.OK);
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

    @GetMapping("{uf}/page")
    public PageResponse getAllFiles(
            @PathVariable String uf,
            @RequestParam(name = "sort", defaultValue = "nome") String orderBy,
            @RequestParam(name = "asc", defaultValue = "true") boolean asc,
            @RequestParam(name = "number", defaultValue = "0") int number,
            @RequestParam(name = "size", defaultValue = "10") int size
    ){
        return cidadeService.getPage(uf,orderBy,asc,number, size);
    }

    @DeleteMapping("delete/{id}")
    public Map<String, String> deleteCidadeById(@PathVariable String id) {
        cidadeService.deleteCidadeById(id);
        return Map.of("message", "Operação realizada com sucesso");
    }

    @PutMapping("deletelist")
    public List<Cidade> deleteCidades(@RequestBody List<Cidade> cList) {
        return cidadeService.deleteListOfCidades(cList);
    }
}