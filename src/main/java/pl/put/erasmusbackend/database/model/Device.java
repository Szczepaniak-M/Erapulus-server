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
    private int id;

    @Column("user")
    private int userId;

    @Column("deviceId")
    private String deviceId;

    @Column("lastModifiedBy")
    private int lastModifiedBy;
}
