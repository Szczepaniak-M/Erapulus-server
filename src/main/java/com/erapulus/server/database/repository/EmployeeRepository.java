package com.erapulus.server.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.erapulus.server.database.model.EmployeeEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EmployeeRepository extends R2dbcRepository<EmployeeEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email AND type != 'STUDENT'")
    Mono<EmployeeEntity> findByEmailAndType(@Param("email") String email);

    @Query("SELECT * FROM application_user WHERE id = :employee AND type != 'STUDENT'")
    Mono<EmployeeEntity> findByIdAndType(@Param("employee") int employeeId);

    @Query("SELECT * FROM application_user WHERE university = :university AND (type = 'UNIVERSITY_ADMINISTRATOR' OR type = 'EMPLOYEE')")
    Flux<EmployeeEntity> findAllByUniversityIdAndType(@Param("university") int universityId);

    @Query("DELETE FROM application_user WHERE university = :university AND (type = 'UNIVERSITY_ADMINISTRATOR' OR type = 'EMPLOYEE')")
    Mono<Void> deleteAllByUniversityId(int universityId);
}
