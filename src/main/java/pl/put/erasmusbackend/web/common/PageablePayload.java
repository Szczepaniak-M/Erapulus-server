package pl.put.erasmusbackend.web.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class PageablePayload<T> {

    public PageablePayload(List<T> content, PageRequest pageRequest, Integer totalCount) {
        this.content = content;
        this.currentPage = pageRequest.getPageNumber();
        this.pageSize = pageRequest.getPageSize();
        this.totalCount = totalCount;
    }

    @JsonProperty("content")
    private List<T> content;

    @JsonProperty("currentPage")
    private Integer currentPage;

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("pageSize")
    private Integer pageSize;

    @JsonProperty("offset")
    private Integer offset() {
        return currentPage * pageSize;
    }
}
