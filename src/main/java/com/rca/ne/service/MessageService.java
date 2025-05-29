package com.rca.ne.service;

import com.rca.ne.model.Employee;
import com.rca.ne.model.Message;
import com.rca.ne.model.Payslip;
import com.rca.ne.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JavaMailSender mailSender;

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public List<Message> getMessagesByEmployee(Employee employee) {
        return messageRepository.findByEmployee(employee);
    }

    public List<Message> getMessagesByMonthAndYear(Integer month, Integer year) {
        return messageRepository.findByMonthAndYear(month, year);
    }

    public List<Message> getUnsentMessages() {
        return messageRepository.findBySent(false);
    }

    @Transactional
    public Message createPayslipApprovalMessage(Payslip payslip) {
        Employee employee = payslip.getEmployee();
        String messageContent = generatePayslipApprovalMessage(payslip);

        Message message = new Message();
        message.setEmployee(employee);
        message.setMessage(messageContent);
        message.setMonth(payslip.getMonth());
        message.setYear(payslip.getYear());
        message.setSent(false);

        return messageRepository.save(message);
    }

    @Transactional
    public void sendMessages() {
        List<Message> unsentMessages = getUnsentMessages();
        
        for (Message message : unsentMessages) {
            sendEmail(message.getEmployee().getEmail(), "Payslip Approval Notification", message.getMessage());
            message.setSent(true);
            messageRepository.save(message);
        }
    }

    private String generatePayslipApprovalMessage(Payslip payslip) {
        Employee employee = payslip.getEmployee();
        String monthName = getMonthName(payslip.getMonth());
        
        return String.format(
            "Dear %s, your salary for %s/%d from Rwanda Government amounting to %s has been credited to your account %s successfully.",
            employee.getFirstName(),
            monthName,
            payslip.getYear(),
            payslip.getNetSalary().toString(),
            employee.getCode()
        );
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month - 1];
    }
}