package pl.put.erasmusbackend.database.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.put.erasmusbackend.database.model.ApplicationUserEntity;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<ApplicationUserEntity, Integer> {

    @Query("SELECT * FROM application_user WHERE email = :email")
    Mono<ApplicationUserEntity> findByEmail(@Param("email") String email);
}
