package com.erapulus.server.mapper;

public interface EntityToResponseDtoMapper<T, R> {

    R from(T entity);
}
