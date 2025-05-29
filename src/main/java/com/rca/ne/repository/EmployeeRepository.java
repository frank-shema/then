package com.rca.ne.repository;

import com.rca.ne.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    Optional<Employee> findByCode(String code);
    
    boolean existsByEmail(String email);
    
    boolean existsByCode(String code);
}