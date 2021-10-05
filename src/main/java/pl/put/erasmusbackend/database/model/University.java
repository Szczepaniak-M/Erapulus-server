package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@Table("University")
public class University {
    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("name")
    private String name;

    @NotNull
    @Column("address")
    private String address;

    @Column("address2")
    private String address2;

    @NotNull
    @Column("zipcode")
    private String zipcode;

    @NotNull
    @Column("city")
    private String city;

    @NotNull
    @Column("country")
    private String country;

    @Column("description")
    private String description;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
