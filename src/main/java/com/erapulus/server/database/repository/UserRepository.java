package com.erapulus.server.database.repository;

import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.database.model.UserType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<ApplicationUserEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email")
    Mono<ApplicationUserEntity> findByEmail(@Param("email") String email);

    @Query("""
            SELECT * FROM application_user
            WHERE (:university IS NULL OR university = :university)
            AND (:type IS NULL OR type = :type)
            AND (:name IS NULL OR LOWER(first_name) LIKE LOWER(CONCAT(:name, '%')) OR LOWER(last_name) LIKE LOWER(CONCAT(:name, '%')))
            ORDER BY id OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<ApplicationUserEntity> findByFilters(@Param("university") Integer universityId,
                                              @Param("type") UserType userType,
                                              @Param("name") String name,
                                              @Param("offset") long offset,
                                              @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM application_user
            WHERE (:university IS NULL OR university = :university)
            AND (:type IS NULL OR type = :type)
            AND (:name IS NULL OR LOWER(first_name) LIKE LOWER(CONCAT(:name, '%')) OR LOWER(last_name) LIKE LOWER(CONCAT(:name, '%')))
            """)
    Mono<Integer> countByFilters(@Param("university") Integer universityId,
                                 @Param("type") UserType userType,
                                 @Param("name") String name);

}
