package pl.put.erasmusbackend.database.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("ApplicationUser")
public class StudentEntity extends ApplicationUserEntity {
    @Column("pictureUrl")
    private String pictureUrl;

    @Column("facebookUrl")
    private String facebookUrl;

    @Column("whatsUpUrl")
    private String whatsUpUrl;

    @Column("instagramUsername")
    private String instagramUsername;

    // Automatic type filling by Builder
    private static final class StudentEntityBuilderImpl extends StudentEntity.StudentEntityBuilder<StudentEntity, StudentEntity.StudentEntityBuilderImpl> {
        @Override
        public StudentEntity build() {
            return (StudentEntity) new StudentEntity(this).type(UserType.STUDENT);
        }
    }
}
