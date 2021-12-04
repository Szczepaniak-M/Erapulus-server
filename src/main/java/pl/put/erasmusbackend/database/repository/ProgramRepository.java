package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.ProgramEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProgramRepository extends R2dbcRepository<ProgramEntity, Integer> {
    @Query("""
            SELECT * FROM program
            WHERE faculty = :faculty
            ORDER BY name OFFSET :offset ROWS
            FETCH NEXT :size ROWS ONLY
            """)
    Flux<ProgramEntity> findByFaculty(@Param("faculty") int facultyId,
                                      @Param("offset") long offset,
                                      @Param("size") int pageSize);

    @Query("""
            SELECT COUNT(*) FROM program
            WHERE faculty = :faculty
            """)
    Mono<Integer> countByFaculty(@Param("faculty") int facultyId);

    @Query("""
            SELECT * FROM program p
            JOIN faculty f ON p.faculty = f.id
            WHERE p.id = :program AND f.id = :faculty AND f.university = :university
            """)
    Mono<ProgramEntity> findByIdAndUniversityIdAndFacultyId(@Param("program") int programId,
                                                            @Param("university") int universityId,
                                                            @Param("faculty") int facultyId);
}
