package com.rca.ne.service;

import com.rca.ne.dto.deduction.DeductionDto;
import com.rca.ne.model.Deduction;
import com.rca.ne.repository.DeductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeductionService {

    @Autowired
    private DeductionRepository deductionRepository;

    public List<DeductionDto> getAllDeductions() {
        return deductionRepository.findAll().stream()
                .map(DeductionDto::new)
                .collect(Collectors.toList());
    }

    public List<DeductionDto> getActiveDeductions() {
        return deductionRepository.findByActive(true).stream()
                .map(DeductionDto::new)
                .collect(Collectors.toList());
    }

    public DeductionDto getDeductionById(Long id) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction not found with id: " + id));
        return new DeductionDto(deduction);
    }

    public DeductionDto getDeductionByCode(String code) {
        Deduction deduction = deductionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Deduction not found with code: " + code));
        return new DeductionDto(deduction);
    }

    public DeductionDto getDeductionByName(String name) {
        Deduction deduction = deductionRepository.findByDeductionName(name)
                .orElseThrow(() -> new RuntimeException("Deduction not found with name: " + name));
        return new DeductionDto(deduction);
    }

    @Transactional
    public DeductionDto createDeduction(DeductionDto deductionDto) {
        // Check if deduction name already exists
        if (deductionRepository.existsByDeductionName(deductionDto.getDeductionName())) {
            throw new RuntimeException("Deduction name is already in use");
        }

        Deduction deduction = new Deduction();
        deduction.setCode(generateDeductionCode());
        deduction.setDeductionName(deductionDto.getDeductionName());
        deduction.setPercentage(deductionDto.getPercentage());
        deduction.setActive(deductionDto.isActive());

        Deduction savedDeduction = deductionRepository.save(deduction);
        return new DeductionDto(savedDeduction);
    }

    @Transactional
    public DeductionDto updateDeduction(Long id, DeductionDto deductionDto) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction not found with id: " + id));

        // Check if deduction name is being changed and if it already exists
        if (!deduction.getDeductionName().equals(deductionDto.getDeductionName()) && 
                deductionRepository.existsByDeductionName(deductionDto.getDeductionName())) {
            throw new RuntimeException("Deduction name is already in use");
        }

        deduction.setDeductionName(deductionDto.getDeductionName());
        deduction.setPercentage(deductionDto.getPercentage());
        deduction.setActive(deductionDto.isActive());

        Deduction updatedDeduction = deductionRepository.save(deduction);
        return new DeductionDto(updatedDeduction);
    }

    @Transactional
    public void deleteDeduction(Long id) {
        if (!deductionRepository.existsById(id)) {
            throw new RuntimeException("Deduction not found with id: " + id);
        }
        deductionRepository.deleteById(id);
    }

    @Transactional
    public void initializeDefaultDeductions() {
        // Check if deductions already exist
        if (deductionRepository.count() > 0) {
            return;
        }

        // Create default deductions
        createDefaultDeduction("Employee Tax", 30.0);
        createDefaultDeduction("Pension", 6.0);
        createDefaultDeduction("Medical Insurance", 5.0);
        createDefaultDeduction("Housing", 14.0);
        createDefaultDeduction("Transport", 14.0);
        createDefaultDeduction("Others", 5.0);
    }

    private void createDefaultDeduction(String name, double percentage) {
        Deduction deduction = new Deduction();
        deduction.setCode(generateDeductionCode());
        deduction.setDeductionName(name);
        deduction.setPercentage(new java.math.BigDecimal(percentage));
        deduction.setActive(true);
        deductionRepository.save(deduction);
    }

    private String generateDeductionCode() {
        return "DED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}