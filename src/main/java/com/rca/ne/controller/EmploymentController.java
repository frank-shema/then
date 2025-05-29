package com.rca.ne.controller;

import com.rca.ne.dto.employment.EmploymentDto;
import com.rca.ne.service.EmploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employments")
@Tag(name = "Employments", description = "Employment Management API")
public class EmploymentController {

    @Autowired
    private EmploymentService employmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employments", description = "Returns a list of all employments")
    public ResponseEntity<List<EmploymentDto>> getAllEmployments() {
        return ResponseEntity.ok(employmentService.getAllEmployments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employment by ID", description = "Returns a single employment by ID")
    public ResponseEntity<EmploymentDto> getEmploymentById(@PathVariable Long id) {
        return ResponseEntity.ok(employmentService.getEmploymentById(id));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employment by code", description = "Returns a single employment by code")
    public ResponseEntity<EmploymentDto> getEmploymentByCode(@PathVariable String code) {
        return ResponseEntity.ok(employmentService.getEmploymentByCode(code));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get employments by employee", description = "Returns all employments for a specific employee")
    public ResponseEntity<List<EmploymentDto>> getEmploymentsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployee(employeeId));
    }

    @GetMapping("/employee/{employeeId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get active employments by employee", description = "Returns all active employments for a specific employee")
    public ResponseEntity<List<EmploymentDto>> getActiveEmploymentsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employmentService.getActiveEmploymentsByEmployee(employeeId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create employment", description = "Creates a new employment")
    public ResponseEntity<EmploymentDto> createEmployment(@Valid @RequestBody EmploymentDto employmentDto) {
        return ResponseEntity.ok(employmentService.createEmployment(employmentDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update employment", description = "Updates an existing employment")
    public ResponseEntity<EmploymentDto> updateEmployment(@PathVariable Long id, @Valid @RequestBody EmploymentDto employmentDto) {
        return ResponseEntity.ok(employmentService.updateEmployment(id, employmentDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete employment", description = "Deletes an employment")
    public ResponseEntity<Void> deleteEmployment(@PathVariable Long id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.noContent().build();
    }
}