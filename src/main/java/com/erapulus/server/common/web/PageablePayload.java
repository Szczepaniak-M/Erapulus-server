package com.erapulus.server.common.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class PageablePayload<T> {

    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("currentPage")
    private Integer currentPage;

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("pageSize")
    private Integer pageSize;

    public PageablePayload(List<T> content, PageRequest pageRequest, Integer totalCount) {
        this.content = content;
        this.currentPage = pageRequest.getPageNumber();
        this.pageSize = pageRequest.getPageSize();
        this.totalCount = totalCount;
    }

    @JsonProperty("offset")
    private Integer offset() {
        return currentPage * pageSize;
    }
}
