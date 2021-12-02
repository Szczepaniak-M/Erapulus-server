package pl.put.erasmusbackend.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table("ApplicationUser")
public class ApplicationUserEntity implements Entity {
    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("type")
    private UserType type;

    @NotNull
    @Column("firstName")
    private String firstName;

    @NotNull
    @Column("lastName")
    private String lastName;

    @Column("university")
    private Integer universityId;

    @NotNull
    @Column("email")
    private String email;

    @NotNull
    @Column("phoneNumber")
    private String phoneNumber;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
