package com.erapulus.server.service;

import com.erapulus.server.database.model.Entity;
import com.erapulus.server.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Service
@Validated
@AllArgsConstructor
public abstract class CrudGenericService<T extends Entity, R, S> {

    protected final R2dbcRepository<T, Integer> repository;
    protected final RequestDtoToEntityMapper<R, T> requestDtoToEntityMapper;
    protected final EntityToResponseDtoMapper<T, S> entityToResponseDtoMapper;
    protected final String entityName;

    protected Mono<S> createEntity(@Valid R requestDto, UnaryOperator<T> transform) {
        return Mono.just(requestDto)
                   .map(requestDtoToEntityMapper::from)
                   .map(transform)
                   .flatMap(repository::save)
                   .map(entityToResponseDtoMapper::from);
    }

    protected Mono<S> getEntityById(Supplier<Mono<T>> supplier) {
        return supplier.get()
                       .map(entityToResponseDtoMapper::from)
                       .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)));
    }

    protected Mono<S> updateEntity(@Valid R requestDto, UnaryOperator<T> transform) {
        return Mono.just(requestDto)
                   .map(requestDtoToEntityMapper::from)
                   .map(transform)
                   .flatMap(updatedT -> repository.findById(updatedT.id())
                                                  .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                                  .flatMap(b -> repository.save(updatedT)))
                   .map(entityToResponseDtoMapper::from);
    }

    protected Mono<S> updateEntity(@Valid R requestDto, UnaryOperator<T> transform, BinaryOperator<T> mergeEntity) {
        return Mono.just(requestDto)
                   .map(requestDtoToEntityMapper::from)
                   .map(transform)
                   .flatMap(updatedT -> repository.findById(updatedT.id())
                                                  .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                                                  .flatMap(oldEntity -> repository.save(mergeEntity.apply(oldEntity, updatedT))))
                   .map(entityToResponseDtoMapper::from);
    }

    public Mono<Boolean> deleteEntity(int entityId) {
        return repository.findById(entityId)
                         .switchIfEmpty(Mono.error(new NoSuchElementException(entityName)))
                         .flatMap(b -> repository.deleteById(entityId))
                         .thenReturn(true);
    }
}
