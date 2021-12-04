package pl.put.erasmusbackend.mapper;

public interface EntityToResponseDtoMapper<T, R> {

    R from(T responseDto);
}
