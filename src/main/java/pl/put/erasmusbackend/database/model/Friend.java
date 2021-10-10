package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.NotNull;

@Data
public class Friend {

    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("applicationUser")
    private Integer applicationUserId;

    @NotNull
    @Column("friend")
    private Integer friendId;

    @NotNull
    @Column("status")
    private String status;

    @NotNull
    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
