package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.Student;

@Repository
public interface StudentRepository extends R2dbcRepository<Student, Integer> {
}
