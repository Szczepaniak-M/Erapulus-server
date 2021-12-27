package com.erapulus.server.database.model;

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
@Table("friendship")
public class FriendshipEntity implements Entity {
    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("application_user")
    private Integer applicationUserId;

    @NotNull
    @Column("friend")
    private Integer friendId;

    @NotNull
    @Column("status")
    private FriendshipStatus status;

    @NotNull
    @LastModifiedBy
    @Column("last_modified_by")
    private Integer lastModifiedBy;

    public FriendshipEntity reverse() {
        return FriendshipEntity.builder()
                               .status(this.status)
                               .applicationUserId(friendId)
                               .friendId(applicationUserId)
                               .build();
    }
}
