package com.rca.ne.dto.employee;

import com.rca.ne.model.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateUpdateDto {
    private Long id;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private String password;
    
    @NotBlank(message = "Mobile number is required")
    private String mobile;
    
    private LocalDate dateOfBirth;
    
    private Employee.EmployeeStatus status;
    
    private Set<Employee.Role> roles;

    // Constructor to convert from Entity to DTO
    public EmployeeCreateUpdateDto(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        // Password is not set for security reasons
        this.mobile = employee.getMobile();
        this.dateOfBirth = employee.getDateOfBirth();
        this.status = employee.getStatus();
        this.roles = employee.getRoles();
    }
}