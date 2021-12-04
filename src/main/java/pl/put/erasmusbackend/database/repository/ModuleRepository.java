package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.ModuleEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ModuleRepository extends R2dbcRepository<ModuleEntity, Integer> {
    @Query("""
            SELECT * FROM module
            WHERE program = :program
            ORDER BY name OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<ModuleEntity> findByProgramId(@Param("program") int programId,
                                       @Param("offset") long offset,
                                       @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM module
            WHERE program = :program
            """)
    Mono<Integer> countByProgramId(@Param("program") int programId);

    @Query("SELECT * FROM module WHERE id = :id AND program = :program")
    Mono<ModuleEntity> findByIdAndProgramId(@Param("id") int moduleId,
                                            @Param("program") int programId);
}
