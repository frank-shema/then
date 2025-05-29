package com.rca.ne.repository;

import com.rca.ne.model.Deduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionRepository extends JpaRepository<Deduction, Long> {
    
    Optional<Deduction> findByCode(String code);
    
    Optional<Deduction> findByDeductionName(String deductionName);
    
    List<Deduction> findByActive(boolean active);
    
    boolean existsByCode(String code);
    
    boolean existsByDeductionName(String deductionName);
}