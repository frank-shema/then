package com.rca.ne.dto.payslip;

import com.rca.ne.dto.employee.EmployeeDto;
import com.rca.ne.model.Payslip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayslipDto {
    private Long id;
    private EmployeeDto employee;
    private BigDecimal houseAmount;
    private BigDecimal transportAmount;
    private BigDecimal employeeTaxedAmount;
    private BigDecimal pensionAmount;
    private BigDecimal medicalInsuranceAmount;
    private BigDecimal otherTaxedAmount;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private Integer month;
    private Integer year;
    private Payslip.PayslipStatus status;

    // Constructor to convert from Entity to DTO
    public PayslipDto(Payslip payslip) {
        this.id = payslip.getId();
        this.employee = new EmployeeDto(payslip.getEmployee());
        this.houseAmount = payslip.getHouseAmount();
        this.transportAmount = payslip.getTransportAmount();
        this.employeeTaxedAmount = payslip.getEmployeeTaxedAmount();
        this.pensionAmount = payslip.getPensionAmount();
        this.medicalInsuranceAmount = payslip.getMedicalInsuranceAmount();
        this.otherTaxedAmount = payslip.getOtherTaxedAmount();
        this.grossSalary = payslip.getGrossSalary();
        this.netSalary = payslip.getNetSalary();
        this.month = payslip.getMonth();
        this.year = payslip.getYear();
        this.status = payslip.getStatus();
    }
}