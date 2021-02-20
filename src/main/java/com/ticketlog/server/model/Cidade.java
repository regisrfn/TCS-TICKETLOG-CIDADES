package com.ticketlog.server.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Entity
@Table(name = "cidades", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "idEstado", "nome" }, name = "uk_cidade_nome") })
public class Cidade {

    @Id
    private UUID id;

    @NotBlank(message = "Campo nao deve ser vazio")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Campo não deve ser vazio")
    @Min(value = 0, message = "Populacao deve ser maior ou igual a zero")
    private Long populacao;

    @NotNull(message = "Campo não deve ser vazio")
    @DecimalMin(value = "0.0")
    private Double custoCidadeUs;

    @JsonProperty("uf")
    @NotNull(message = "Campo não deve ser vazio")
    private UF idEstado;

    public Cidade() {
        setId(UUID.randomUUID());
        setCustoCidadeUs(0.0);
    }

    public void setIdEstado(String id) {
        try {
            this.idEstado = UF.valueOf(id.toUpperCase());
        } catch (Exception e) {
            this.idEstado = null;
        }
    }

    public String getIdEstado() {
        return this.idEstado.toString();
    }

    public enum UF {
        SC, PR, RS
    }

}
