package com.ca.nbiapps.unit.testcases;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.ca.nbiapps.core.compnents.CommonComponent;
import com.ca.nbiapps.core.compnents.PropertyComponents;
import com.ca.nbiapps.core.compnents.TestCaseContext;
import com.ca.nbiapps.integration.test.BaseTest;

/**
 * @author Balaji N
 *
 */
public class TestFlow extends BaseTest {
	
	@Autowired
	PropertyComponents propertyComponent;
	
	@Autowired
	CommonComponent commonComponent;
	
	@Autowired
	TestCaseContext testCaseContext;
	
	public static final String UNIT_TEST_LOG_FILENAME="UnitTestLog";
	
	
	@Test
	public void testInstance(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = commonComponent.getLogger(UNIT_TEST_LOG_FILENAME, "INFO");
			logger.info("testCaseContext::"+testCaseContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
