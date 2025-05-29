package com.rca.ne.service;

import com.rca.ne.dto.auth.JwtResponse;
import com.rca.ne.dto.auth.LoginRequest;
import com.rca.ne.dto.auth.RegisterRequest;
import com.rca.ne.model.Employee;
import com.rca.ne.model.Employment;
import com.rca.ne.repository.EmployeeRepository;
import com.rca.ne.repository.EmploymentRepository;
import com.rca.ne.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmploymentRepository employmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Employee employee = employeeRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new JwtResponse(
                jwt,
                employee.getId(),
                employee.getCode(),
                employee.getEmail(),
                employee.getFirstName(),
                employee.getLastName(),
                roles
        );
    }

    @Transactional
    public Employee registerUser(RegisterRequest registerRequest) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new employee
        Employee employee = new Employee();
        employee.setCode(generateEmployeeCode());
        employee.setFirstName(registerRequest.getFirstName());
        employee.setLastName(registerRequest.getLastName());
        employee.setEmail(registerRequest.getEmail());
        employee.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        employee.setMobile(registerRequest.getMobile());
        employee.setDateOfBirth(registerRequest.getDateOfBirth());
        employee.setStatus(Employee.EmployeeStatus.ACTIVE);

        // Set roles
        Set<Employee.Role> roles = registerRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add(Employee.Role.ROLE_EMPLOYEE);
        }
        employee.setRoles(roles);

        // Save employee
        Employee savedEmployee = employeeRepository.save(employee);

        // Create employment record
        Employment employment = new Employment();
        employment.setCode(generateEmploymentCode());
        employment.setEmployee(savedEmployee);
        employment.setDepartment(registerRequest.getDepartment());
        employment.setPosition(registerRequest.getPosition());
        employment.setBaseSalary(registerRequest.getBaseSalary());
        employment.setStatus(Employment.EmploymentStatus.ACTIVE);
        employment.setJoiningDate(registerRequest.getJoiningDate());

        // Save employment
        employmentRepository.save(employment);

        return savedEmployee;
    }

    private String generateEmployeeCode() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateEmploymentCode() {
        return "EMPL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}