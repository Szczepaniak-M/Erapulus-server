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
@Table("Document")
public class DocumentEntity implements Entity {
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

    @Column("university")
    private Integer universityId;

    @Column("university")
    private Integer courseId;

    @Column("subject")
    private Integer subjectId;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
