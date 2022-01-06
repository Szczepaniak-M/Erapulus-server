package com.erapulus.server.employee.database;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("application_user")
public class EmployeeEntity extends ApplicationUserEntity {
    @NotNull
    @Column("password")
    private String password;

    public EmployeeEntity id(int id) {
        super.id(id);
        return this;
    }

    @Override
    public EmployeeEntity type(UserType userType) {
        super.type(userType);
        return this;
    }

    @Override
    public EmployeeEntity universityId(Integer universityId) {
        super.universityId(universityId);
        return this;
    }
}
