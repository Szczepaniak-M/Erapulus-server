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
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("Post")
public class PostEntity implements Entity {
    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("title")
    private String title;

    @NotNull
    @Column("date")
    private LocalDate date;

    @NotNull
    @Column("content")
    private String content;

    @NotNull
    @Column("university")
    private Integer universityId;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
