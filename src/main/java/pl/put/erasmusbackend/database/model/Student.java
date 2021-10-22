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
public class Student extends ApplicationUser {
    @Column("facebookUrl")
    private String facebookUrl;

    @Column("whatsUpUrl")
    private String whatsUpUrl;

    @Column("instagramUsername")
    private String instagramUsername;

    // Automatic type filling by Builder
    private static final class StudentBuilderImpl extends Student.StudentBuilder<Student, Student.StudentBuilderImpl> {
        @Override
        public Student build() {
            return (Student) new Student(this).type(UserType.STUDENT);
        }
    }
}
