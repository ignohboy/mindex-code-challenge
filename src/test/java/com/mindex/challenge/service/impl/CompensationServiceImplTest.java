package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImplTest.class);

	private String compensationUrl;
	private String compensationIdUrl;
	
	@Autowired private EmployeeService employeeService;
	
	@LocalServerPort
    private int port;
	
	@Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }
    
    @Test
    public void testCreateRead() {
    	//Make a test employee to test compensation against.
    	Employee testEmployee = persistTestEmployee();
    	
    	//Compensation insertion checks
    	Compensation createdCompensation = performInsertionChecks(testEmployee.getEmployeeId());
    	
    	//Compensation read checks
    	performReadChecks(createdCompensation);
    	
    	LOG.debug("Completed CompensationServiceImpl Tests.");
    }
    
    /**
     * Create a test employee to link to Compensation for
     * 	Compensation checks.
     */
    private Employee persistTestEmployee() {
    	Employee testEmployee = new Employee();
        testEmployee.setFirstName("Testy");
        testEmployee.setLastName("Tester");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        
        LOG.debug("Creating test employee [{}]", testEmployee);
        return employeeService.create(testEmployee);
    }
    
    /**
     * <p>Perform insertion checks.</p>
     * 
     * @param employeeId - The ID of the test employee.
     * @return The created test Compensation.
     */
    private Compensation performInsertionChecks(String employeeId) {
        //Test compensation insert without employeeId
    	Compensation testCompensation = new Compensation();
    	testCompensation.setSalary(100000.00);
    	testCompensation.setEffectiveDate(new Date());
    	//Should fail to insert.
    	LOG.debug("Creating test compensation with null employeeId [{}]", testCompensation);
    	assertTrue(failedCreationWithoutEmployeeId(testCompensation));
    	
    	//Test compensation insert with employeeId
    	LOG.debug("Creating test compensation [{}]", testCompensation);
    	testCompensation.setEmployeeId(employeeId);
    	Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
    	assertCompensationEquivalence(testCompensation, createdCompensation);
    	
    	//Attempt to insert duplicate record
    	Compensation duplicateCompensation = new Compensation();
    	duplicateCompensation.setEmployeeId(testCompensation.getEmployeeId());
    	duplicateCompensation.setSalary(testCompensation.getSalary());
    	duplicateCompensation.setEffectiveDate(testCompensation.getEffectiveDate());
    	//Should fail to insert
    	LOG.debug("Creating duplicate compensation.");
    	assertTrue(failedDuplicateCreation(duplicateCompensation, testCompensation));
    	
    	return createdCompensation;
    }
    
    /**
     * <p>Perform read checks.</p>
     * 
     * @param createdCompensation - The created compensation record to compare the read against.
     */
    private void performReadChecks(Compensation createdCompensation) {
    	//Test read by reading back the inserted record.
    	LOG.debug("Attempting to read test compensation.");
    	Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdCompensation.getEmployeeId()).getBody();
    	assertCompensationEquivalence(createdCompensation, readCompensation);
    }
    
    private boolean failedCreationWithoutEmployeeId(Compensation testCompensation) {
    	assertNull(testCompensation.getEmployeeId());
    	Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
    	//Empty object returned on failure
    	return checkIfCreatedCompensationIsEmpty(createdCompensation);
    }
    
    private boolean failedDuplicateCreation(Compensation duplicateCompensation, Compensation testCompensation) {
    	assertCompensationEquivalence(duplicateCompensation, testCompensation);
    	Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, duplicateCompensation, Compensation.class).getBody();
    	//Empty object returned on failure
    	return checkIfCreatedCompensationIsEmpty(createdCompensation);
    }
    
    private boolean checkIfCreatedCompensationIsEmpty(Compensation compensation) {
    	boolean isEmpty = false;
    	if (compensation.getEffectiveDate() == null
    			&& compensation.getEmployeeId() == null
    			&& compensation.getSalary() == null ) {
    		
    		isEmpty = true;
    	}
    	return isEmpty;
    }
    
    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
    	assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
    	assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    	assertEquals(expected.getSalary(), actual.getSalary());
    }
}
