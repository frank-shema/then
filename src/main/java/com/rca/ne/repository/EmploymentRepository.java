package com.rca.ne.repository;

import com.rca.ne.model.Employee;
import com.rca.ne.model.Employment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {

    Optional<Employment> findByCode(String code);

    List<Employment> findByEmployee(Employee employee);

    List<Employment> findByEmployeeAndStatus(Employee employee, Employment.EmploymentStatus status);

    Optional<Employment> findByEmployeeAndStatusOrderByJoiningDateDesc(Employee employee, Employment.EmploymentStatus status);

    boolean existsByCode(String code);
}