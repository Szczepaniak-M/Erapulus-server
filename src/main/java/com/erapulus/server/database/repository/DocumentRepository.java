package com.erapulus.server.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.erapulus.server.database.model.DocumentEntity;
import reactor.core.publisher.Flux;

@Repository
public interface DocumentRepository extends R2dbcRepository<DocumentEntity, Integer> {

    @Query("""
            SELECT * FROM document
            WHERE (:university IS NULL OR university = :university)
            AND (:program IS NULL OR program = :program)
            AND (:module IS NULL OR module = :module)
            """)
    Flux<DocumentEntity> findAllByFilters(@Param("university") Integer universityId,
                                          @Param("program") Integer programId,
                                          @Param("module") Integer moduleId);
}
