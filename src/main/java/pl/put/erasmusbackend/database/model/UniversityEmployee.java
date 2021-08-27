package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("UniversityEmployee")
public class UniversityEmployee {
    @Id
    @Column("id")
    private int id;

    @Column("name")
    private String firstName;

    @Column("address")
    private String lastName;

    @Column("university")
    private int universityId;

    @Column("login")
    private String zipcode;

    @Column("password")
    private String password;

    @Column("lastModifiedBy")
    private int lastModifiedBy;
}
