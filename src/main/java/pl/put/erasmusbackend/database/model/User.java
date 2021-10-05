package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@Table("User")
public class User {
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
    @Column("email")
    private String email;


    @Column("facebookUrl")
    private String facebookUrl;

    @Column("whatsUpUrl")
    private String whatsUpUrl;

    @NotNull
    @Column("university")
    private Integer universityId;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
