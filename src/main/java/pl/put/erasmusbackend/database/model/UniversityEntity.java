package pl.put.erasmusbackend.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("University")
public class UniversityEntity {
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
