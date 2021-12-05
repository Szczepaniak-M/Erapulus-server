package com.erapulus.server.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.erapulus.server.database.model.EmployeeEntity;
import reactor.core.publisher.Mono;

@Repository
public interface EmployeeRepository extends R2dbcRepository<EmployeeEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email")
    Mono<EmployeeEntity> findByEmail(@Param("email") String email);
}
