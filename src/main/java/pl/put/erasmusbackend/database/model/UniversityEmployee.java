package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@Table("UniversityEmployee")
public class UniversityEmployee {
    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("name")
    private String firstName;

    @NotNull
    @Column("address")
    private String lastName;

    @NotNull
    @Column("university")
    private Integer universityId;

    @NotNull
    @Column("login")
    private String zipcode;

    @NotNull
    @Column("password")
    private String password;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
