package com.erapulus.server.common.mapper;

public interface EntityToResponseDtoMapper<T, R> {

    R from(T entity);
}
