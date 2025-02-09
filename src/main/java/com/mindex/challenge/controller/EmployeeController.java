package com.mindex.challenge.controller;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.dto.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable("id") String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
    
    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable("id") String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }
    
    @GetMapping("/employee/reportingStructure/{id}")
    public ReportingStructure getReportingStructure(@PathVariable("id") String id) {
    	LOG.debug("Received reporting structure read request for employee id [{}]");
    	Employee employee = employeeService.read(id);
    	return new ReportingStructure(employee);
    }
}
