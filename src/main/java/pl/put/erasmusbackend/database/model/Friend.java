package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
public class Friend {

    @Id
    @Column("id")
    private int id;

    @Column("user")
    private int userId;

    @Column("friend")
    private int friendId;

    @Column("lastModifiedBy")
    private int lastModifiedBy;
}
