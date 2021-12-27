package com.erapulus.server.database.repository;

import com.erapulus.server.database.model.ProgramEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProgramRepository extends R2dbcRepository<ProgramEntity, Integer> {
    @Query("""
            SELECT * FROM program
            WHERE faculty = :faculty
            AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT(:name, '%')))
            ORDER BY name OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<ProgramEntity> findByFacultyIdAndName(@Param("faculty") int facultyId,
                                               @Param("name") String name,
                                               @Param("offset") long offset,
                                               @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM program
            WHERE faculty = :faculty
            AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT(:name, '%')))
            """)
    Mono<Integer> countByFacultyIdAndName(@Param("faculty") int facultyId,
                                          @Param("name") String name);

    @Query("""
            SELECT * FROM program p
            JOIN faculty f ON p.faculty = f.id
            WHERE p.id = :program
            AND f.id = :faculty
            AND f.university = :university
            """)
    Mono<ProgramEntity> findByIdAndUniversityIdAndFacultyId(@Param("program") int programId,
                                                            @Param("university") int universityId,
                                                            @Param("faculty") int facultyId);

    @Query("""
            SELECT CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT)
            FROM program p
            JOIN faculty f ON p.faculty = f.id
            WHERE p.id = :program
            AND f.id = :faculty
            AND f.university = :university
            """)
    Mono<Boolean> existsByIdAndUniversityIdAndFacultyId(@Param("program") int programId,
                                                        @Param("university") int universityId,
                                                        @Param("faculty") int facultyId);
}
