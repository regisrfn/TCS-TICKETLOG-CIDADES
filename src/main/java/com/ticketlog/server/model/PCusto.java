package com.ticketlog.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PCusto {

    private Double custoPorPessoa;
    private Double desconto;
    private Long valorCorte;

    public PCusto() {
        setCustoPorPessoa(0.0);
        setValorCorte(0L);
    }
}