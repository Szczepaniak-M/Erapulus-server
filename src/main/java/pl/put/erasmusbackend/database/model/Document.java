package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("Document")
public class Document {
    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("path")
    private String path;

    @Column("university")
    private Integer universityId;

    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
