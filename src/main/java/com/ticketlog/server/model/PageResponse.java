package com.ticketlog.server.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "page" })
public class PageResponse {

    private List<Cidade> cidadesList;
    private Integer totalPages, pageNumber;
    private Page<Cidade> page;

    public PageResponse() {
    }

    public PageResponse(Page<Cidade> page) {
        this.page = page;
        this.cidadesList = page.toList();
        this.pageNumber = page.getNumber();
        this.totalPages = page.getTotalPages();
    }


}