package com.rca.ne.controller;

import com.rca.ne.dto.deduction.DeductionDto;
import com.rca.ne.service.DeductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deductions")
@Tag(name = "Deductions", description = "Deduction Management API")
public class DeductionController {

    @Autowired
    private DeductionService deductionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all deductions", description = "Returns a list of all deductions")
    public ResponseEntity<List<DeductionDto>> getAllDeductions() {
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get active deductions", description = "Returns a list of all active deductions")
    public ResponseEntity<List<DeductionDto>> getActiveDeductions() {
        return ResponseEntity.ok(deductionService.getActiveDeductions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get deduction by ID", description = "Returns a single deduction by ID")
    public ResponseEntity<DeductionDto> getDeductionById(@PathVariable Long id) {
        return ResponseEntity.ok(deductionService.getDeductionById(id));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get deduction by code", description = "Returns a single deduction by code")
    public ResponseEntity<DeductionDto> getDeductionByCode(@PathVariable String code) {
        return ResponseEntity.ok(deductionService.getDeductionByCode(code));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get deduction by name", description = "Returns a single deduction by name")
    public ResponseEntity<DeductionDto> getDeductionByName(@PathVariable String name) {
        return ResponseEntity.ok(deductionService.getDeductionByName(name));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create deduction", description = "Creates a new deduction")
    public ResponseEntity<DeductionDto> createDeduction(@Valid @RequestBody DeductionDto deductionDto) {
        return ResponseEntity.ok(deductionService.createDeduction(deductionDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update deduction", description = "Updates an existing deduction")
    public ResponseEntity<DeductionDto> updateDeduction(@PathVariable Long id, @Valid @RequestBody DeductionDto deductionDto) {
        return ResponseEntity.ok(deductionService.updateDeduction(id, deductionDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete deduction", description = "Deletes a deduction")
    public ResponseEntity<Void> deleteDeduction(@PathVariable Long id) {
        deductionService.deleteDeduction(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Initialize default deductions", description = "Creates default deductions if none exist")
    public ResponseEntity<Void> initializeDefaultDeductions() {
        deductionService.initializeDefaultDeductions();
        return ResponseEntity.ok().build();
    }
}