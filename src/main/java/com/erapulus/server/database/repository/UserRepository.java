package com.erapulus.server.database.repository;

import com.erapulus.server.database.model.ApplicationUserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<ApplicationUserEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email")
    Mono<ApplicationUserEntity> findByEmail(@Param("email") String email);
}
