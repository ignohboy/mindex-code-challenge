package com.mindex.challenge.data;

import java.util.Date;

public class Compensation {
	
	//No need for id here since employeeId is effectively the PK.
	//	Because of this, this probably shouldn't be its own table
	//	and should instead be a schema update directly on employee.
	private String employeeId;
	private Double salary;
	private Date effectiveDate;
	
	
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public Double getSalary() {
		return salary;
	}
	public void setSalary(Double salary) {
		this.salary = salary;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
