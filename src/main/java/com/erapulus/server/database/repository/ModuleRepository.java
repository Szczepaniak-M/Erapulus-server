package com.erapulus.server.database.repository;

import com.erapulus.server.database.model.ModuleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ModuleRepository extends R2dbcRepository<ModuleEntity, Integer> {
    @Query("""
            SELECT * FROM module
            WHERE program = :program
            AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT(:name, '%')))
            ORDER BY name OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<ModuleEntity> findByProgramIdAndName(@Param("program") int programId,
                                              @Param("name") String name,
                                              @Param("offset") long offset,
                                              @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM module
            WHERE program = :program
            AND (:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT(:name, '%')))
            """)
    Mono<Integer> countByProgramIdAndName(@Param("program") int programId,
                                          @Param("name") String name);

    @Query("SELECT * FROM module WHERE id = :id AND program = :program")
    Mono<ModuleEntity> findByIdAndProgramId(@Param("id") int moduleId,
                                            @Param("program") int programId);

    @Query("""
            SELECT CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT)
            FROM module m
            JOIN program p ON m.program = p.id
            JOIN faculty f ON p.faculty = f.id
            WHERE m.id = :module
            AND p.id = :program
            AND f.id = :faculty
            AND f.university = :university
            """)
    Mono<Boolean> existsByIdAndUniversityIdAndFacultyIdAndProgramId(@Param("module") int moduleId,
                                                                    @Param("university") int universityId,
                                                                    @Param("faculty") int facultyId,
                                                                    @Param("program") int programId);

    @Query("SELECT id FROM module WHERE program = :program")
    Flux<Integer> findAllByProgramId(@Param("program") int programId);
}
