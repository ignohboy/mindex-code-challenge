package com.mindex.challenge;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.dto.ReportingStructure;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallengeApplicationTests {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChallengeApplicationTests.class);
	
	@Test
	public void contextLoads() {
	}

	@Test
	public void testReportingStructure() {
		//Test with 0 directReports.
		LOG.debug("Testing ReportingStructure with 0 direct reports.");
		Employee testEmployee_A = new Employee();
		testEmployee_A.setEmployeeId("A");
        testEmployee_A.setFirstName("A");
        testEmployee_A.setLastName("Test");
        ReportingStructure testReportingStructure = new ReportingStructure(testEmployee_A);
        Assert.assertEquals(0, testReportingStructure.getNumberOfReports());
		
        //Test with circular references
        LOG.debug("Testing ReportingStructure with circular references.");
        Employee testEmployee_B = new Employee();
		testEmployee_B.setEmployeeId("B");
        testEmployee_B.setFirstName("B");
        testEmployee_B.setLastName("Test");
        
        Employee testEmployee_C = new Employee();
		testEmployee_C.setEmployeeId("C");
        testEmployee_C.setFirstName("C");
        testEmployee_C.setLastName("Test");
        List<Employee> directReportsC = new ArrayList<>();
        directReportsC.add(testEmployee_A);
        testEmployee_C.setDirectReports(directReportsC);
        
        List<Employee> directReportsA = new ArrayList<>();
        directReportsA.add(testEmployee_B);
        directReportsA.add(testEmployee_C);
        testEmployee_A.setDirectReports(directReportsA);
        
        testReportingStructure = new ReportingStructure(testEmployee_A);
        Assert.assertEquals(2, testReportingStructure.getNumberOfReports());
        
        //Test with nested duplicates
        LOG.debug("Testing ReportingStructure with nested duplicate references.");
        Employee testEmployee_D = new Employee();
		testEmployee_D.setEmployeeId("D");
        testEmployee_D.setFirstName("D");
        testEmployee_D.setLastName("Test");
        List<Employee> directReportsD = new ArrayList<>();
        directReportsD.add(testEmployee_B);
        testEmployee_D.setDirectReports(directReportsD);
        
        testEmployee_C.getDirectReports().clear();
        testEmployee_C.getDirectReports().add(testEmployee_D);
        
        testReportingStructure = new ReportingStructure(testEmployee_A);
        Assert.assertEquals(3, testReportingStructure.getNumberOfReports());
        
        LOG.debug("ReportingStructure tests complete.");
	}
}
