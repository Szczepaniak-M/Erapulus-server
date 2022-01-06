package com.erapulus.server.faculty.database;

import com.erapulus.server.common.database.Entity;
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
@Table("faculty")
public class FacultyEntity implements Entity {
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

    @NotNull
    @Column("email")
    private String email;

    @NotNull
    @Column("university")
    private Integer universityId;

    @LastModifiedBy
    @Column("last_modified_by")
    private Integer lastModifiedBy;
}
