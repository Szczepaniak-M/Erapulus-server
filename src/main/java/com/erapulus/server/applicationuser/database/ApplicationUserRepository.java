package com.erapulus.server.applicationuser.database;

import com.erapulus.server.common.database.UserType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ApplicationUserRepository extends R2dbcRepository<ApplicationUserEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email")
    Mono<ApplicationUserEntity> findByEmail(@Param("email") String email);

    @Query("""
            SELECT * FROM application_user
            WHERE (:university IS NULL OR university = :university)
            AND (:type IS NULL OR type = :type)
            AND (:email IS NULL OR email LIKE CONCAT(:email, '%'))
            AND (:name IS NULL OR LOWER(CONCAT(first_name, last_name)) LIKE LOWER(CONCAT('%', :name, '%'))
                               OR LOWER(CONCAT(last_name, first_name)) LIKE LOWER(CONCAT('%', :name, '%')))
            ORDER BY id OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<ApplicationUserEntity> findAllByFilters(@Param("university") Integer universityId,
                                                 @Param("type") UserType userType,
                                                 @Param("name") String name,
                                                 @Param("email") String email,
                                                 @Param("offset") long offset,
                                                 @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM application_user
            WHERE (:university IS NULL OR university = :university)
            AND (:type IS NULL OR type = :type)
            AND (:email IS NULL OR email LIKE CONCAT(:email, '%'))
            AND (:name IS NULL OR LOWER(CONCAT(first_name, last_name)) LIKE LOWER(CONCAT('%', :name, '%'))
                               OR LOWER(CONCAT(last_name, first_name)) LIKE LOWER(CONCAT('%', :name, '%')))
            """)
    Mono<Integer> countByFilters(@Param("university") Integer universityId,
                                 @Param("type") UserType userType,
                                 @Param("name") String name,
                                 @Param("email") String email);

}
