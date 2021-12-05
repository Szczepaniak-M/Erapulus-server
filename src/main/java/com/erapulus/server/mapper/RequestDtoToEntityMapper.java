package com.erapulus.server.mapper;

public interface RequestDtoToEntityMapper<T, R> {

    R from(T entity);
}
