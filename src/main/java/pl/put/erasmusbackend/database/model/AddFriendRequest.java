package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table("AddFriendRequest")
public class AddFriendRequest {

    @Id
    @Column("id")
    private int id;

    @Column("sender")
    private int senderId;

    @Column("recipient")
    private int recipientId;

    @Column("sendTime")
    private Instant sendTime;

    @Column("status")
    private int status;

    @Column("lastModifiedBy")
    private int lastModifiedBy;
}
