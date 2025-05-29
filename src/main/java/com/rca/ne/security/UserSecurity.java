package com.rca.ne.security;

import com.rca.ne.model.Employee;
import com.rca.ne.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private EmployeeRepository employeeRepository;

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUserEmail = authentication.getName();
        Employee employee = employeeRepository.findById(userId).orElse(null);

        return employee != null && employee.getEmail().equals(currentUserEmail);
    }
}