package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Table("AddFriendRequest")
public class AddFriendRequest {

    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("sender")
    private Integer senderId;

    @NotNull
    @Column("recipient")
    private Integer recipientId;

    @NotNull
    @Column("sendTime")
    private Instant sendTime;

    @NotNull
    @Column("status")
    private Integer status;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
