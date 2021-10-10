package pl.put.erasmusbackend.database.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("ApplicationUser")
public class Employee extends ApplicationUser {
    @Column("password")
    private String password;
}
