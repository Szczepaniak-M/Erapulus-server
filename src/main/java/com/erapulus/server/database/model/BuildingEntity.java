package com.erapulus.server.database.model;

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
@Table("building")
public class BuildingEntity implements Entity {

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

    @NotNull
    @Column("latitude")
    private Double latitude;

    @NotNull
    @Column("longitude")
    private Double longitude;

    @NotNull
    @Column("university")
    private Integer universityId;

    @LastModifiedBy
    @Column("last_modified_by")
    private Integer lastModifiedBy;
}
