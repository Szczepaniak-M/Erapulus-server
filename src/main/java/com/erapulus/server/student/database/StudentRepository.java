package com.erapulus.server.student.database;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentRepository extends R2dbcRepository<StudentEntity, Integer> {

    @Query("""
            SELECT id, first_name, last_name, picture_url
            FROM application_user
            WHERE (:name IS NULL OR LOWER(CONCAT(first_name, last_name)) LIKE LOWER(CONCAT('%', :name, '%'))
                                 OR LOWER(CONCAT(last_name, first_name)) LIKE LOWER(CONCAT('%', :name, '%')))
            AND type = 'STUDENT'
            AND university = :university
            """)
    Flux<StudentEntity> findAllByName(@Param("name") String name,
                                      @Param("university") int universityId);

    @Query("SELECT * FROM application_user WHERE email = :email AND type = 'STUDENT'")
    Mono<StudentEntity> findByEmail(@Param("email") String email);

    @Query("SELECT * FROM application_user WHERE id = :student AND type = 'STUDENT'")
    Mono<StudentEntity> findByIdAndType(@Param("student") int studentId);
}
