package com.erapulus.server.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.erapulus.server.database.model.FacultyEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FacultyRepository extends R2dbcRepository<FacultyEntity, Integer> {

    @Query("""
            SELECT * FROM faculty
            WHERE university = :university
            AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT(:name, '%')))
            ORDER BY name OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<FacultyEntity> findByUniversityIdAndName(@Param("university") int universityId,
                                                  @Param("name") String name,
                                                  @Param("offset") long offset,
                                                  @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM faculty
            WHERE university = :university
            AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT(:name, '%')))
            """)
    Mono<Integer> countByUniversityIdAndName(@Param("university") int universityId,
                                             @Param("name") String name);

    @Query("SELECT * FROM faculty WHERE id = :faculty AND university = :university")
    Mono<FacultyEntity> findByIdAndUniversityId(@Param("faculty") int facultyId,
                                                @Param("university") int universityId);

    @Query("SELECT id FROM faculty WHERE university = :university")
    Flux<Integer> findAllByUniversityId(@Param("university") int universityId);
}
