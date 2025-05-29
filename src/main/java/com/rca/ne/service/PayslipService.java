package com.rca.ne.service;

import com.rca.ne.dto.payslip.PayslipApproveRequest;
import com.rca.ne.dto.payslip.PayslipDto;
import com.rca.ne.dto.payslip.PayslipProcessRequest;
import com.rca.ne.model.Deduction;
import com.rca.ne.model.Employee;
import com.rca.ne.model.Employment;
import com.rca.ne.model.Payslip;
import com.rca.ne.repository.DeductionRepository;
import com.rca.ne.repository.EmployeeRepository;
import com.rca.ne.repository.EmploymentRepository;
import com.rca.ne.repository.PayslipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PayslipService {

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmploymentRepository employmentRepository;

    @Autowired
    private DeductionRepository deductionRepository;

    @Autowired
    private MessageService messageService;

    public List<PayslipDto> getAllPayslips() {
        return payslipRepository.findAll().stream()
                .map(PayslipDto::new)
                .collect(Collectors.toList());
    }

    public PayslipDto getPayslipById(Long id) {
        Payslip payslip = payslipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payslip not found with id: " + id));
        return new PayslipDto(payslip);
    }

    public List<PayslipDto> getPayslipsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        return payslipRepository.findByEmployee(employee).stream()
                .map(PayslipDto::new)
                .collect(Collectors.toList());
    }

    public List<PayslipDto> getPayslipsByEmployeeAndStatus(Long employeeId, Payslip.PayslipStatus status) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        return payslipRepository.findByEmployeeAndStatus(employee, status).stream()
                .map(PayslipDto::new)
                .collect(Collectors.toList());
    }

    public PayslipDto getPayslipByEmployeeAndMonthAndYear(Long employeeId, Integer month, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        Payslip payslip = payslipRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                .orElseThrow(() -> new RuntimeException("Payslip not found for employee with id: " + employeeId + " for month: " + month + " and year: " + year));
        return new PayslipDto(payslip);
    }

    public List<PayslipDto> getPayslipsByMonthAndYear(Integer month, Integer year) {
        return payslipRepository.findByMonthAndYear(month, year).stream()
                .map(PayslipDto::new)
                .collect(Collectors.toList());
    }

    public List<PayslipDto> getPayslipsByMonthAndYearAndStatus(Integer month, Integer year, Payslip.PayslipStatus status) {
        return payslipRepository.findByMonthAndYearAndStatus(month, year, status).stream()
                .map(PayslipDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PayslipDto> processPayslips(PayslipProcessRequest request) {
        Integer month = request.getMonth();
        Integer year = request.getYear();

        // Get all active employees
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(employee -> employee.getStatus() == Employee.EmployeeStatus.ACTIVE)
                .collect(Collectors.toList());

        // Get all active deductions
        List<Deduction> activeDeductions = deductionRepository.findByActive(true);

        // Process payslips for each active employee
        return activeEmployees.stream()
                .map(employee -> processEmployeePayslip(employee, month, year, activeDeductions))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(PayslipDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PayslipDto> approvePayslips(PayslipApproveRequest request) {
        Integer month = request.getMonth();
        Integer year = request.getYear();
        Long employeeId = request.getEmployeeId();

        List<Payslip> payslipsToApprove;
        if (employeeId != null) {
            // Approve payslip for a specific employee
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
            Payslip payslip = payslipRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                    .orElseThrow(() -> new RuntimeException("Payslip not found for employee with id: " + employeeId + " for month: " + month + " and year: " + year));
            if (payslip.getStatus() == Payslip.PayslipStatus.PAID) {
                throw new RuntimeException("Payslip is already approved");
            }
            payslipsToApprove = List.of(payslip);
        } else {
            // Approve all pending payslips for the given month and year
            payslipsToApprove = payslipRepository.findByMonthAndYearAndStatus(month, year, Payslip.PayslipStatus.PENDING);
        }

        // Approve payslips and create messages
        return payslipsToApprove.stream()
                .map(payslip -> {
                    payslip.setStatus(Payslip.PayslipStatus.PAID);
                    Payslip approvedPayslip = payslipRepository.save(payslip);

                    // Create message for the employee
                    messageService.createPayslipApprovalMessage(approvedPayslip);

                    return approvedPayslip;
                })
                .map(PayslipDto::new)
                .collect(Collectors.toList());
    }

    private Optional<Payslip> processEmployeePayslip(Employee employee, Integer month, Integer year, List<Deduction> deductions) {
        // Check if payslip already exists for this employee, month, and year
        if (payslipRepository.existsByEmployeeAndMonthAndYear(employee, month, year)) {
            return Optional.empty();
        }

        // Get the active employment for the employee
        Optional<Employment> activeEmploymentOpt = employmentRepository.findByEmployeeAndStatusOrderByJoiningDateDesc(
                employee, Employment.EmploymentStatus.ACTIVE);

        if (activeEmploymentOpt.isEmpty()) {
            return Optional.empty();
        }

        Employment activeEmployment = activeEmploymentOpt.get();
        BigDecimal baseSalary = activeEmployment.getBaseSalary();

        // Calculate deductions
        BigDecimal employeeTaxAmount = BigDecimal.ZERO;
        BigDecimal pensionAmount = BigDecimal.ZERO;
        BigDecimal medicalInsuranceAmount = BigDecimal.ZERO;
        BigDecimal otherTaxAmount = BigDecimal.ZERO;
        BigDecimal housingAmount = BigDecimal.ZERO;
        BigDecimal transportAmount = BigDecimal.ZERO;

        for (Deduction deduction : deductions) {
            BigDecimal percentage = deduction.getPercentage().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal amount = baseSalary.multiply(percentage).setScale(2, RoundingMode.HALF_UP);

            switch (deduction.getDeductionName()) {
                case "Employee Tax":
                    employeeTaxAmount = amount;
                    break;
                case "Pension":
                    pensionAmount = amount;
                    break;
                case "Medical Insurance":
                    medicalInsuranceAmount = amount;
                    break;
                case "Others":
                    otherTaxAmount = amount;
                    break;
                case "Housing":
                    housingAmount = amount;
                    break;
                case "Transport":
                    transportAmount = amount;
                    break;
            }
        }

        // Calculate gross and net salary
        BigDecimal grossSalary = baseSalary.add(housingAmount).add(transportAmount);
        BigDecimal totalDeductions = employeeTaxAmount.add(pensionAmount).add(medicalInsuranceAmount).add(otherTaxAmount);
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);

        // Create and save payslip
        Payslip payslip = new Payslip();
        payslip.setEmployee(employee);
        payslip.setHouseAmount(housingAmount);
        payslip.setTransportAmount(transportAmount);
        payslip.setEmployeeTaxedAmount(employeeTaxAmount);
        payslip.setPensionAmount(pensionAmount);
        payslip.setMedicalInsuranceAmount(medicalInsuranceAmount);
        payslip.setOtherTaxedAmount(otherTaxAmount);
        payslip.setGrossSalary(grossSalary);
        payslip.setNetSalary(netSalary);
        payslip.setMonth(month);
        payslip.setYear(year);
        payslip.setStatus(Payslip.PayslipStatus.PENDING);

        return Optional.of(payslipRepository.save(payslip));
    }
}
