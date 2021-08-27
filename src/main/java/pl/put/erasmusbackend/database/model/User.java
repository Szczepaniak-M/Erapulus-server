package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("User")
public class User {
    @Id
    @Column("id")
    private int id;

    @Column("name")
    private String firstName;

    @Column("address")
    private String lastName;

    @Column("email")
    private String email;

    @Column("facebookUrl")
    private String facebookUrl;

    @Column("whatsUpUrl")
    private String whatsUpUrl;

    @Column("university")
    private int universityId;

    @Column("lastModifiedBy")
    private int lastModifiedBy;
}
