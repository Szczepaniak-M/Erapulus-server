package com.erapulus.server.database.repository;

import com.erapulus.server.database.model.StudentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StudentRepository extends R2dbcRepository<StudentEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email AND type = 'STUDENT'")
    Mono<StudentEntity> findByEmail(@Param("email") String email);

    @Query("SELECT * FROM application_user WHERE id = :student AND type = 'STUDENT'")
    Mono<StudentEntity> findByIdAndType(@Param("student") int studentId);

    @Query("""
            SELECT a.id, first_name, last_name, picture_url
            FROM application_user AS a
            JOIN friendship f ON a.id = f.friend
            WHERE (:name IS NULL OR LOWER(CONCAT(first_name, last_name)) LIKE LOWER(CONCAT('%', :name, '%'))
                                 OR LOWER(CONCAT(last_name, first_name)) LIKE LOWER(CONCAT('%', :name, '%')))
            AND f.application_user = :student
            AND f.status = 'ACCEPTED'
            ORDER BY id OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<StudentEntity> findFriendsByIdAndFilters(@Param("student") int studentId,
                                                  @Param("name") String name,
                                                  @Param("offset") long offset,
                                                  @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*)
            FROM application_user a
            JOIN friendship f ON a.id = f.friend
            WHERE (:name IS NULL OR LOWER(CONCAT(first_name, last_name)) LIKE LOWER(CONCAT('%', :name, '%'))
                                 OR LOWER(CONCAT(last_name, first_name)) LIKE LOWER(CONCAT('%', :name, '%')))
            AND f.application_user = :student
            AND f.status = 'ACCEPTED'
            """)
    Mono<Integer> countFriendsByIdAndFilters(@Param("student") int studentId,
                                             @Param("name") String name);
}
