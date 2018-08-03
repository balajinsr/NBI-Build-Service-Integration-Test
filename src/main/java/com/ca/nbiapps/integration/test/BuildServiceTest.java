package com.ca.nbiapps.integration.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.ca.nbiapps.service.TestCaseService;


public class BuildServiceTest extends BaseTest {
	
	@Autowired
	TestCaseService testCaseService;
	
	@Test
	public void process(final ITestContext testContext) {
		try {
			System.out.println("Process Start");
			
			testCaseService.process(testContext.getCurrentXmlTest().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
