package pl.put.erasmusbackend.database.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("ApplicationUser")
public class Employee extends ApplicationUser {
    @Column("password")
    private String password;

    // Automatic type filling by Builder
    private static final class EmployeeBuilderImpl extends Employee.EmployeeBuilder<Employee, Employee.EmployeeBuilderImpl> {
        @Override
        public Employee build() {
            return (Employee) new Employee(this).type(UserType.EMPLOYEE);
        }
    }
}
