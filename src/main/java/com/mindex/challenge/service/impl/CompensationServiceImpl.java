package com.mindex.challenge.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;


@Service
public class CompensationServiceImpl implements CompensationService {

	@Autowired private CompensationRepository compensationRepository;
	@Autowired private EmployeeService employeeService;
	
	
	@Override
	public Compensation create(Compensation compensation) {
		//Opting to enforce this here because I don't see the JPA annotations as importable and
		//	don't want to bring in additional dependencies into this project.
		Assert.notNull(compensation, "Compensation cannot be null.");
		Assert.hasText(compensation.getEmployeeId(), "Compensation.employeeId cannot be null or empty.");
		//Confirm user exists to link compensation to.
		Assert.isTrue(employeeService.checkIfUserExists(compensation.getEmployeeId()),
				String.format("Employee with id %s does not exist.", compensation.getEmployeeId()));
		//Confirm compensation doesn't already exist.
		Assert.isTrue(!checkIfCompensationExists(compensation.getEmployeeId()),
				String.format("Compensation already exists for employeeId %s.", compensation.getEmployeeId()));
		
		return compensationRepository.insert(compensation);
	}

	@Override
	public Compensation read(String employeeId) {
		return compensationRepository.findByEmployeeId(employeeId);
	}
	
	@Override
	public boolean checkIfCompensationExists(String employeeId) {
		return compensationRepository.countByEmployeeId(employeeId) > 0
				? true
				: false;
	}

}
