package com.erapulus.server.applicationuser.mapper;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.applicationuser.dto.ApplicationUserDto;
import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
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
