package pl.put.erasmusbackend.database.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Table("Device")
public class Device {

    @Id
    @Column("id")
    private Integer id;

    @Column("user")
    private Integer userId;

    @Column("deviceId")
    private String deviceId;

    @Column("lastModifiedBy")
    private Integer lastModifiedBy;
}
