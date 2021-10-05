package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@Table("Document")
public class Document {
    @Id
    @Column("id")
    private Integer id;

    @NotNull
    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @NotNull
    @Column("path")
    private String path;

    @NotNull
    @Column("university")
    private Integer universityId;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
