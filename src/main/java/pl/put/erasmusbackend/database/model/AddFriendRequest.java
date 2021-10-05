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
    private Integer id;

    @Column("sender")
    private Integer senderId;

    @Column("recipient")
    private Integer recipientId;

    @Column("sendTime")
    private Instant sendTime;

    @Column("status")
    private Integer status;

    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
