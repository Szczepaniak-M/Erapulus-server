package com.erapulus.server.university.database;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UniversityRepository extends R2dbcRepository<UniversityEntity, Integer> {

    @Query("SELECT id, name, logo_url FROM university")
    Flux<UniversityEntity> findAllUniversities();
}
