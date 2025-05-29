package com.rca.ne.controller;

import com.rca.ne.dto.payslip.PayslipApproveRequest;
import com.rca.ne.dto.payslip.PayslipDto;
import com.rca.ne.dto.payslip.PayslipProcessRequest;
import com.rca.ne.model.Payslip;
import com.rca.ne.service.MessageService;
import com.rca.ne.service.PayslipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
@Tag(name = "Payslips", description = "Payslip Management API")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all payslips", description = "Returns a list of all payslips")
    public ResponseEntity<List<PayslipDto>> getAllPayslips() {
        return ResponseEntity.ok(payslipService.getAllPayslips());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get payslip by ID", description = "Returns a single payslip by ID")
    public ResponseEntity<PayslipDto> getPayslipById(@PathVariable Long id) {
        return ResponseEntity.ok(payslipService.getPayslipById(id));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get payslips by employee", description = "Returns all payslips for a specific employee")
    public ResponseEntity<List<PayslipDto>> getPayslipsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get payslips by employee and status", description = "Returns all payslips for a specific employee with a specific status")
    public ResponseEntity<List<PayslipDto>> getPayslipsByEmployeeAndStatus(
            @PathVariable Long employeeId,
            @PathVariable Payslip.PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByEmployeeAndStatus(employeeId, status));
    }

    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get payslip by employee, month, and year", description = "Returns a single payslip for a specific employee, month, and year")
    public ResponseEntity<PayslipDto> getPayslipByEmployeeAndMonthAndYear(
            @PathVariable Long employeeId,
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipByEmployeeAndMonthAndYear(employeeId, month, year));
    }

    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get payslips by month and year", description = "Returns all payslips for a specific month and year")
    public ResponseEntity<List<PayslipDto>> getPayslipsByMonthAndYear(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYear(month, year));
    }

    @GetMapping("/month/{month}/year/{year}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get payslips by month, year, and status", description = "Returns all payslips for a specific month, year, and status")
    public ResponseEntity<List<PayslipDto>> getPayslipsByMonthAndYearAndStatus(
            @PathVariable Integer month,
            @PathVariable Integer year,
            @PathVariable Payslip.PayslipStatus status) {
        return ResponseEntity.ok(payslipService.getPayslipsByMonthAndYearAndStatus(month, year, status));
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Process payslips", description = "Processes payslips for all active employees for a specific month and year")
    public ResponseEntity<List<PayslipDto>> processPayslips(@Valid @RequestBody PayslipProcessRequest request) {
        return ResponseEntity.ok(payslipService.processPayslips(request));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve payslips", description = "Approves payslips for a specific month and year, optionally for a specific employee")
    public ResponseEntity<List<PayslipDto>> approvePayslips(@Valid @RequestBody PayslipApproveRequest request) {
        List<PayslipDto> approvedPayslips = payslipService.approvePayslips(request);
        
        // Send notification messages
        messageService.sendMessages();
        
        return ResponseEntity.ok(approvedPayslips);
    }
}