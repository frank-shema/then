package com.rca.ne.service;

import com.rca.ne.dto.employee.EmployeeCreateUpdateDto;
import com.rca.ne.dto.employee.EmployeeDto;
import com.rca.ne.model.Employee;
import com.rca.ne.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeDto::new)
                .collect(Collectors.toList());
    }

    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return new EmployeeDto(employee);
    }

    public EmployeeDto getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found with email: " + email));
        return new EmployeeDto(employee);
    }

    public EmployeeDto getEmployeeByCode(String code) {
        Employee employee = employeeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Employee not found with code: " + code));
        return new EmployeeDto(employee);
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateUpdateDto employeeDto) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        Employee employee = new Employee();
        employee.setCode(generateEmployeeCode());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        // If password is not provided, generate a random one
        String password = employeeDto.getPassword() != null ? employeeDto.getPassword() : UUID.randomUUID().toString().substring(0, 8);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setMobile(employeeDto.getMobile());
        employee.setDateOfBirth(employeeDto.getDateOfBirth());
        employee.setStatus(employeeDto.getStatus() != null ? employeeDto.getStatus() : Employee.EmployeeStatus.ACTIVE);
        employee.setRoles(employeeDto.getRoles());

        Employee savedEmployee = employeeRepository.save(employee);
        return new EmployeeDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeCreateUpdateDto employeeDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!employee.getEmail().equals(employeeDto.getEmail()) && 
                employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        // Only update password if it's provided
        if (employeeDto.getPassword() != null && !employeeDto.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        }
        employee.setMobile(employeeDto.getMobile());
        employee.setDateOfBirth(employeeDto.getDateOfBirth());
        employee.setStatus(employeeDto.getStatus());
        employee.setRoles(employeeDto.getRoles());

        Employee updatedEmployee = employeeRepository.save(employee);
        return new EmployeeDto(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private String generateEmployeeCode() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
