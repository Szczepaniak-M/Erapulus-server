package com.erapulus.server.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.erapulus.server.database.model.StudentEntity;
import reactor.core.publisher.Mono;

@Repository
public interface StudentRepository extends R2dbcRepository<StudentEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email")
    Mono<StudentEntity> findByEmail(@Param("email") String email);

    @Query("SELECT * FROM application_user WHERE id = :studentId AND type = 'STUDENT'")
    Mono<StudentEntity> findById(@Param("studentId") int studentId);
}
