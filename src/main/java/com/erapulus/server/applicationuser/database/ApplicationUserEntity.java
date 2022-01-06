package com.erapulus.server.applicationuser.database;

import com.erapulus.server.common.database.Entity;
import com.erapulus.server.common.database.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table("application_user")
public class ApplicationUserEntity implements Entity {
    @Id
    @NotNull
    @Column("id")
    private Integer id;

    @NotNull
    @Column("type")
    private UserType type;

    @NotNull
    @Column("first_name")
    private String firstName;

    @NotNull
    @Column("last_name")
    private String lastName;

    @Column("university")
    private Integer universityId;

    @NotNull
    @Column("email")
    private String email;

    @NotNull
    @Column("phone_number")
    private String phoneNumber;

    @LastModifiedBy
    @Column("last_modified_by")
    private Integer lastModifiedBy;
}
