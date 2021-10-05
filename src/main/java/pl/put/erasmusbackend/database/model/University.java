package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("University")
public class University {
    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("address")
    private String address;

    @Column("address2")
    private String address2;

    @Column("zipcode")
    private String zipcode;

    @Column("city")
    private String city;

    @Column("country")
    private String country;

    @Column("description")
    private String description;

    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
