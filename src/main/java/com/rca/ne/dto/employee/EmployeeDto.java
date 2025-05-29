package com.rca.ne.dto.employee;

import com.rca.ne.model.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private LocalDate dateOfBirth;
    private Employee.EmployeeStatus status;
    private Set<Employee.Role> roles;

    // Constructor to convert from Entity to DTO
    public EmployeeDto(Employee employee) {
        this.id = employee.getId();
        this.code = employee.getCode();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.mobile = employee.getMobile();
        this.dateOfBirth = employee.getDateOfBirth();
        this.status = employee.getStatus();
        this.roles = employee.getRoles();
    }
}