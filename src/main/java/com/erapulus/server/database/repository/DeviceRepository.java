package com.erapulus.server.database.repository;

import com.erapulus.server.database.model.DeviceEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DeviceRepository extends R2dbcRepository<DeviceEntity, Integer> {
    @Query("SELECT * FROM device WHERE application_user = :student")
    Flux<DeviceEntity> findAllByStudentId(@Param("student") int studentId);

    @Query("SELECT * FROM device WHERE id = :device AND application_user = :student")
    Mono<DeviceEntity> findByIdAndStudentId(@Param("device") int deviceId,
                                            @Param("student") int studentId);
}
