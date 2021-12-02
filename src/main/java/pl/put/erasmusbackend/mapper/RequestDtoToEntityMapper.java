package pl.put.erasmusbackend.mapper;

public interface RequestDtoToEntityMapper<T, R> {

    R from(T entity);
}
