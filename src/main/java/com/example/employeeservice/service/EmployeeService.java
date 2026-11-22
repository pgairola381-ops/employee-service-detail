package com.example.employeeservice.service;

import com.example.employeeservice.dto.EmployeeRequest;
import com.example.employeeservice.entity.Employee;
import com.example.employeeservice.exception.ResourceNotFoundException;
import com.example.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(EmployeeRequest request) {
        Employee employee = new Employee(
                request.getName(), request.getEmail(), request.getDepartment());
        return repository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee update(Long id, EmployeeRequest request) {
        Employee employee = findById(id);
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        return repository.save(employee);
    }

    public void delete(Long id) {
        Employee employee = findById(id);
        repository.delete(employee);
    }
}
