package com.erapulus.server.program.database;

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
@Table("program")
public class ProgramEntity implements Entity {
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

    @Column("description")
    private String description;

    @NotNull
    @Column("faculty")
    private Integer facultyId;

    @LastModifiedBy
    @Column("last_modified_by")
    private Integer lastModifiedBy;
}
