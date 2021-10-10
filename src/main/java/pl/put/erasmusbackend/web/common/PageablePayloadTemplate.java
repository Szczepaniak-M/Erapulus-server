package pl.put.erasmusbackend.web.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageablePayloadTemplate<T> {

    @JsonProperty("content")
    private T content;

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("offset")
    private Integer offset;

}
