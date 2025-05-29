package com.rca.ne.dto.employment;

import com.rca.ne.dto.employee.EmployeeDto;
import com.rca.ne.model.Employment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {
    private Long id;
    private String code;
    private EmployeeDto employee;
    private String department;
    private String position;
    private BigDecimal baseSalary;
    private Employment.EmploymentStatus status;
    private LocalDate joiningDate;

    // Constructor to convert from Entity to DTO
    public EmploymentDto(Employment employment) {
        this.id = employment.getId();
        this.code = employment.getCode();
        this.employee = new EmployeeDto(employment.getEmployee());
        this.department = employment.getDepartment();
        this.position = employment.getPosition();
        this.baseSalary = employment.getBaseSalary();
        this.status = employment.getStatus();
        this.joiningDate = employment.getJoiningDate();
    }
}