package com.erapulus.server.device.database;

import com.erapulus.server.common.database.Entity;
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
@Table("device")
public class DeviceEntity implements Entity {
    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("application_user")
    private Integer applicationUserId;

    @NotNull
    @Column("device_id")
    private String deviceId;

    @NotNull
    @Column("name")
    private String name;

    @LastModifiedBy
    @Column("last_modified_by")
    private Integer lastModifiedBy;
}
