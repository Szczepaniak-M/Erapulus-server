package pl.put.erasmusbackend.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ModuleRequestDto {
    @NotNull
    @Column("name")
    private String name;

    @NotNull
    @Column("abbrev")
    private String abbrev;

    @Column("description")
    private String description;
}
