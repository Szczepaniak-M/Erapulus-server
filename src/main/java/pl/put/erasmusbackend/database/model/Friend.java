package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
public class Friend {

    @Id
    @Column("id")
    private Integer id;

    @Column("user")
    private Integer userId;

    @Column("friend")
    private Integer friendId;

    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
