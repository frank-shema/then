package com.rca.ne.service;

import com.rca.ne.dto.employment.EmploymentDto;
import com.rca.ne.model.Employee;
import com.rca.ne.model.Employment;
import com.rca.ne.repository.EmployeeRepository;
import com.rca.ne.repository.EmploymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmploymentService {

    @Autowired
    private EmploymentRepository employmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmploymentDto> getAllEmployments() {
        return employmentRepository.findAll().stream()
                .map(EmploymentDto::new)
                .collect(Collectors.toList());
    }

    public EmploymentDto getEmploymentById(Long id) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employment not found with id: " + id));
        return new EmploymentDto(employment);
    }

    public EmploymentDto getEmploymentByCode(String code) {
        Employment employment = employmentRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Employment not found with code: " + code));
        return new EmploymentDto(employment);
    }

    public List<EmploymentDto> getEmploymentsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        return employmentRepository.findByEmployee(employee).stream()
                .map(EmploymentDto::new)
                .collect(Collectors.toList());
    }

    public List<EmploymentDto> getActiveEmploymentsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        return employmentRepository.findByEmployeeAndStatus(employee, Employment.EmploymentStatus.ACTIVE).stream()
                .map(EmploymentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmploymentDto createEmployment(EmploymentDto employmentDto) {
        Employee employee = employeeRepository.findById(employmentDto.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employmentDto.getEmployee().getId()));

        Employment employment = new Employment();
        employment.setCode(generateEmploymentCode());
        employment.setEmployee(employee);
        employment.setDepartment(employmentDto.getDepartment());
        employment.setPosition(employmentDto.getPosition());
        employment.setBaseSalary(employmentDto.getBaseSalary());
        employment.setStatus(employmentDto.getStatus() != null ? employmentDto.getStatus() : Employment.EmploymentStatus.ACTIVE);
        employment.setJoiningDate(employmentDto.getJoiningDate());

        Employment savedEmployment = employmentRepository.save(employment);
        return new EmploymentDto(savedEmployment);
    }

    @Transactional
    public EmploymentDto updateEmployment(Long id, EmploymentDto employmentDto) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employment not found with id: " + id));

        Employee employee = employeeRepository.findById(employmentDto.getEmployee().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employmentDto.getEmployee().getId()));

        employment.setEmployee(employee);
        employment.setDepartment(employmentDto.getDepartment());
        employment.setPosition(employmentDto.getPosition());
        employment.setBaseSalary(employmentDto.getBaseSalary());
        employment.setStatus(employmentDto.getStatus());
        employment.setJoiningDate(employmentDto.getJoiningDate());

        Employment updatedEmployment = employmentRepository.save(employment);
        return new EmploymentDto(updatedEmployment);
    }

    @Transactional
    public void deleteEmployment(Long id) {
        if (!employmentRepository.existsById(id)) {
            throw new RuntimeException("Employment not found with id: " + id);
        }
        employmentRepository.deleteById(id);
    }

    private String generateEmploymentCode() {
        return "EMPL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}