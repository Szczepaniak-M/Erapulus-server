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
@Table("Building")
public class BuildingEntity {

    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("name")
    private String name;

    @NotNull
    @Column("abbrev")
    private String abbrev;

    @Column("latitude")
    private Long latitude;

    @NotNull
    @Column("longitude")
    private Integer longitude;

    @NotNull
    @Column("university")
    private Integer universityId;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
