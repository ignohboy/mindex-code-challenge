package com.mindex.challenge.data.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mindex.challenge.data.Employee;

/**
 * <p>Class to track the reporting structure for an employee.</p>
 */
//I didn't want to put this directly in com.mindex.challenge.data because this
//	does not represent an entity.
public class ReportingStructure {
	private Employee employee;
	private int numberOfReports = 0;
	
	
	//Since this is computed on the fly and not updated between creation and delivery,
	//	and everything needed to calculate this is on the Employee, just calculate it
	//	in the constructor here.
	public ReportingStructure(Employee employee) {
		this.employee = employee;
		this.numberOfReports = findNumberOfReports(employee);
	}
	
	
	private static int findNumberOfReports(Employee employee) {
		int directReportsCount = 0;
		
		//auto-0 conditions
		if (employee == null
				|| employee.getDirectReports() == null
				|| employee.getDirectReports().isEmpty()) {
			return directReportsCount;
		}
		
		//Map for quick lookup of records we've already gathered reports of.
		Map<String, Boolean> alreadyChecked = new HashMap<>();
		
		//List of employee that report to employee.
		List<Employee> directReportsToCheck = new ArrayList<>();
		
		//Add employee to checked map so they don't get checked again.
		alreadyChecked.put(employee.getEmployeeId(), Boolean.TRUE);
	
		//Add first level of directReports as starting point.
		directReportsToCheck.addAll(employee.getDirectReports());
		
		boolean needToFlatten = checkIfNeedToFlatten(directReportsToCheck, alreadyChecked);
		
		//Flatten directReports list while removing duplicates.
		while (needToFlatten) {
			Employee directReport = directReportsToCheck.get(0);
			
			//Add employees reporting to the directReport employee to our list
			//of employees to check reports of.
			if (directReport.getDirectReports() != null) {
				directReportsToCheck.addAll( directReport.getDirectReports().stream()
						.filter(e -> alreadyChecked.get(e.getEmployeeId()) == null)
						.collect(Collectors.toList())
				);
			}
			
			//Add the directReport employee to the lookup table.
			alreadyChecked.put(directReport.getEmployeeId(), Boolean.TRUE);
			
			//Remove the directReport employee from the list of employees to
			//check for nested directReports lists.
			directReportsToCheck.remove(directReport);
			
			//Check if there are more iterations needed.
			needToFlatten = checkIfNeedToFlatten(directReportsToCheck, alreadyChecked);
			directReportsCount++;
		}
		
		//Add any leftover to our count.
		directReportsCount += directReportsToCheck.stream()
				.filter(e -> alreadyChecked.get(e.getEmployeeId()) == null)
				.collect(Collectors.toList())
				.size();
		
		return directReportsCount;
	}
	
	private static boolean checkIfNeedToFlatten(List<Employee> directReportsToCheck, Map<String, Boolean> alreadyChecked) {
		boolean needToFlatten = false;
		
		for (Employee employee : directReportsToCheck) {
			if (alreadyChecked.get(employee.getEmployeeId()) == null) {
				needToFlatten = true;
				break;
			}
		}
		
		return needToFlatten;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	public int getNumberOfReports() {
		return numberOfReports;
	}
}
