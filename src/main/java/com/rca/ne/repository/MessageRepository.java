package com.rca.ne.repository;

import com.rca.ne.model.Employee;
import com.rca.ne.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByEmployee(Employee employee);
    
    List<Message> findByEmployeeAndMonthAndYear(Employee employee, Integer month, Integer year);
    
    List<Message> findByMonthAndYear(Integer month, Integer year);
    
    List<Message> findBySent(boolean sent);
    
    List<Message> findByEmployeeAndSent(Employee employee, boolean sent);
}