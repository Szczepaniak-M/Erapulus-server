package com.erapulus.server.post.database;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;


@Repository
public interface PostRepository extends R2dbcRepository<PostEntity, Integer> {

    @Query("""
            SELECT * FROM post
            WHERE university = :university
            AND (:title IS NULL OR LOWER(title) LIKE LOWER(CONCAT('%', :title, '%')))
            AND date BETWEEN :fromDate AND :toDate
            ORDER BY id DESC OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<PostEntity> findPostByFilters(@Param("university") int universityId,
                                       @Param("title") String title,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate,
                                       @Param("offset") long offset,
                                       @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM post
            WHERE university = :university
            AND LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))
            AND date BETWEEN :fromDate AND :toDate
            """)
    Mono<Integer> countPostByFilters(@Param("university") int universityId,
                                     @Param("title") String title,
                                     @Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate);

    @Query("SELECT * FROM post WHERE id = :post AND university = :university")
    Mono<PostEntity> findByIdAndUniversityId(@Param("post") int postId,
                                             @Param("university") int universityId);

    @Query("DELETE FROM post WHERE university = :university")
    Mono<Void> deleteAllByUniversityId(@Param("university") int universityId);
}
