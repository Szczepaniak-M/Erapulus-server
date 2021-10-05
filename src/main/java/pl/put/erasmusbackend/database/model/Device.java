package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;


@Data
@Table("Device")
public class Device {

    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("user")
    private Integer userId;

    @NotNull
    @Column("deviceId")
    private String deviceId;

    @LastModifiedBy
    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
