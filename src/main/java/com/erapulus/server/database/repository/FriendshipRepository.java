package com.erapulus.server.database.repository;

import com.erapulus.server.database.model.FriendshipEntity;
import com.erapulus.server.database.model.StudentEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FriendshipRepository extends R2dbcRepository<FriendshipEntity, Integer> {


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

    @Query("""
            SELECT a.id, first_name, last_name, picture_url
            FROM application_user a
            JOIN friendship f ON a.id = f.friend
            AND f.application_user = :student
            AND f.status = 'REQUESTED'
            """)
    Flux<StudentEntity> findFriendRequestsById(@Param("student") int studentId);

    @Query("SELECT * FROM friendship WHERE application_user = :student AND friend = :friend")
    Mono<FriendshipEntity> findByUserIdAndFriendId(@Param("student") int studentId,
                                                   @Param("friend") int friendId);

    @Modifying
    @Query("""
            DELETE FROM friendship
            WHERE (application_user = :student AND friend = :friend)
            OR (application_user = :friend AND friend = :student)
            """)
    Mono<Integer> deleteByUserIdAndFriendId(@Param("student") int studentId,
                                            @Param("friend") int friendId);

    @Query("DELETE FROM friendship WHERE application_user = :student OR friend = :student")
    Mono<Void> deleteAllByStudentId(@Param("student") int studentId);
}
