package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.FacultyEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FacultyRepository extends R2dbcRepository<FacultyEntity, Integer> {

    @Query("""
            SELECT * FROM faculty
            WHERE university = :university
            ORDER BY name OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<FacultyEntity> findByUniversityId(@Param("university") int universityId,
                                           @Param("offset") long offset,
                                           @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM faculty
            WHERE university = :university
            """)
    Mono<Integer> countByUniversityId(@Param("university") int universityId);

    @Query("SELECT * FROM faculty WHERE id = :facultyId AND university = :universityId")
    Mono<FacultyEntity> findByIdAndUniversityId(@Param("facultyId") int facultyId,
                                                @Param("universityId") int universityId);
}
