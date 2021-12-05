package com.erapulus.server.database.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("application_user")
public class StudentEntity extends ApplicationUserEntity {
    @Column("picture_url")
    private String pictureUrl;

    @Column("facebook_url")
    private String facebookUrl;

    @Column("whats_up_url")
    private String whatsUpUrl;

    @Column("instagram_username")
    private String instagramUsername;

    // Automatic type filling by Builder
    private static final class StudentEntityBuilderImpl extends StudentEntity.StudentEntityBuilder<StudentEntity, StudentEntity.StudentEntityBuilderImpl> {
        @Override
        public StudentEntity build() {
            return (StudentEntity) new StudentEntity(this).type(UserType.STUDENT);
        }
    }
}
