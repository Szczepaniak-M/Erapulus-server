package com.erapulus.server.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.erapulus.server.database.model.BuildingEntity;
import reactor.core.publisher.Flux;

@Repository
public interface BuildingRepository extends R2dbcRepository<BuildingEntity, Integer> {

    @Query("SELECT * FROM building WHERE university = :universityId")
    Flux<BuildingEntity> findAllByUniversityId(@Param("universityId") int universityId);
}
