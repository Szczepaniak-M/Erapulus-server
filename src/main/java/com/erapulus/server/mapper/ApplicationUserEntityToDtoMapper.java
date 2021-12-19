package com.erapulus.server.mapper;

import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.dto.ApplicationUserDto;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUserEntityToDtoMapper implements EntityToResponseDtoMapper<ApplicationUserEntity, ApplicationUserDto> {

    public ApplicationUserDto from(ApplicationUserEntity applicationUserEntity) {
        return ApplicationUserDto.builder()
                                 .id(applicationUserEntity.id())
                                 .type(applicationUserEntity.type())
                                 .firstName(applicationUserEntity.firstName())
                                 .lastName(applicationUserEntity.lastName())
                                 .email(applicationUserEntity.email())
                                 .universityId(applicationUserEntity.universityId())
                                 .build();
    }
}
