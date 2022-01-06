package com.erapulus.server.common.mapper;

public interface RequestDtoToEntityMapper<T, R> {

    R from(T requestDto);
}
