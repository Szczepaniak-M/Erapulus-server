package com.erapulus.server.student.database;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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

    @Override
    public StudentEntity id(Integer id) {
        super.id(id);
        return this;
    }

    @Override
    public StudentEntity type(UserType type) {
        super.type(type);
        return this;
    }

    @Override
    public StudentEntity universityId(Integer universityId) {
        super.universityId(universityId);
        return this;
    }

    // Automatic type filling by Builder
    private static final class StudentEntityBuilderImpl extends StudentEntity.StudentEntityBuilder<StudentEntity, StudentEntity.StudentEntityBuilderImpl> {
        @Override
        public StudentEntity build() {
            return new StudentEntity(this).type(UserType.STUDENT);
        }
    }

}
