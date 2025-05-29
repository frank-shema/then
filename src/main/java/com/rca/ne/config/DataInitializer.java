package com.rca.ne.config;

import com.rca.ne.dto.auth.RegisterRequest;
import com.rca.ne.model.Employee;
import com.rca.ne.repository.EmployeeRepository;
import com.rca.ne.service.AuthService;
import com.rca.ne.service.DeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DeductionService deductionService;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default deductions
        deductionService.initializeDefaultDeductions();

        // Create admin user if not exists
        if (!employeeRepository.existsByEmail("admin@gov.rw")) {
            RegisterRequest adminUser = new RegisterRequest();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@gov.rw");
            adminUser.setPassword("admin123");
            adminUser.setMobile("+250700000000");
            adminUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
            
            // Set all roles
            Set<Employee.Role> roles = new HashSet<>();
            roles.add(Employee.Role.ROLE_ADMIN);
            roles.add(Employee.Role.ROLE_MANAGER);
            roles.add(Employee.Role.ROLE_EMPLOYEE);
            adminUser.setRoles(roles);
            
            // Set employment details
            adminUser.setDepartment("Administration");
            adminUser.setPosition("System Administrator");
            adminUser.setBaseSalary(new BigDecimal("100000"));
            adminUser.setJoiningDate(LocalDate.now());
            
            authService.registerUser(adminUser);
            
            System.out.println("Admin user created with email: admin@gov.rw and password: admin123");
        }

        // Create manager user if not exists
        if (!employeeRepository.existsByEmail("manager@gov.rw")) {
            RegisterRequest managerUser = new RegisterRequest();
            managerUser.setFirstName("Manager");
            managerUser.setLastName("User");
            managerUser.setEmail("manager@gov.rw");
            managerUser.setPassword("manager123");
            managerUser.setMobile("+250700000001");
            managerUser.setDateOfBirth(LocalDate.of(1992, 2, 2));
            
            // Set manager role
            Set<Employee.Role> roles = new HashSet<>();
            roles.add(Employee.Role.ROLE_MANAGER);
            roles.add(Employee.Role.ROLE_EMPLOYEE);
            managerUser.setRoles(roles);
            
            // Set employment details
            managerUser.setDepartment("Human Resources");
            managerUser.setPosition("HR Manager");
            managerUser.setBaseSalary(new BigDecimal("80000"));
            managerUser.setJoiningDate(LocalDate.now());
            
            authService.registerUser(managerUser);
            
            System.out.println("Manager user created with email: manager@gov.rw and password: manager123");
        }

        // Create employee user if not exists
        if (!employeeRepository.existsByEmail("employee@gov.rw")) {
            RegisterRequest employeeUser = new RegisterRequest();
            employeeUser.setFirstName("Employee");
            employeeUser.setLastName("User");
            employeeUser.setEmail("employee@gov.rw");
            employeeUser.setPassword("employee123");
            employeeUser.setMobile("+250700000002");
            employeeUser.setDateOfBirth(LocalDate.of(1995, 5, 5));
            
            // Set employee role
            Set<Employee.Role> roles = new HashSet<>();
            roles.add(Employee.Role.ROLE_EMPLOYEE);
            employeeUser.setRoles(roles);
            
            // Set employment details
            employeeUser.setDepartment("Finance");
            employeeUser.setPosition("Accountant");
            employeeUser.setBaseSalary(new BigDecimal("70000"));
            employeeUser.setJoiningDate(LocalDate.now());
            
            authService.registerUser(employeeUser);
            
            System.out.println("Employee user created with email: employee@gov.rw and password: employee123");
        }
    }
}