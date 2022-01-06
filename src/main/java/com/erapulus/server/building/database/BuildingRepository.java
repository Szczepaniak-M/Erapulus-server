package com.erapulus.server.building.database;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BuildingRepository extends R2dbcRepository<BuildingEntity, Integer> {

    @Query("SELECT * FROM building WHERE university = :university")
    Flux<BuildingEntity> findAllByUniversityId(@Param("university") int universityId);

    @Query("SELECT * FROM building WHERE id = :building AND university = :university")
    Mono<BuildingEntity> findByIdAndUniversityId(@Param("building") int buildingId,
                                                 @Param("university") int universityId);

    @Query("DELETE FROM building WHERE university = :university")
    Mono<Void> deleteAllByUniversityId(@Param("university") int universityId);
}
