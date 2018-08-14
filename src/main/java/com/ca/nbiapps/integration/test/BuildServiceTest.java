package com.ca.nbiapps.integration.test;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.ca.nbiapps.build.model.TestCaseContext;
import com.ca.nbiapps.core.compnents.CommonComponent;
import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.service.TestCaseService;
/**
 * 
 * @author Balaji N
 *
 */
public class BuildServiceTest extends BaseTest {
	
	@Autowired
	TestCaseService testCaseService;
	
	@Autowired
	CommonComponent commonComponent;
	
	@Autowired
	GitComponent gitComponent;
	
	@Test
	public void process(final ITestContext testContext) {
		Logger logger = null;
		TestCaseContext testCaseContext = new TestCaseContext();
		try {
			logger = commonComponent.getLogger("Integration-Test-Log", "INFO");
			String testCaseName = testContext.getCurrentXmlTest().getName();
			JSONObject jsonTemplateObject = getJSONObject(logger, "testcasesTemplates/"+testCaseName+".json");
			testCaseContext.setTestCaseData(jsonTemplateObject);
			testCaseContext.setTestCaseName(testCaseName);
			testCaseContext.setLogger(logger);
			testCaseService.process(testCaseContext);
			org.testng.Assert.assertTrue(testCaseContext.isTestCaseSuccess());
		} catch (Exception e) {
			org.testng.Assert.assertTrue(false);
			handleException(logger, e);
		} finally {
			try {
				testCaseService.reset(testCaseContext);
			} catch (Exception e) {
				org.testng.Assert.assertTrue(false);
				handleException(logger, e);
			}
		}
	}
}
