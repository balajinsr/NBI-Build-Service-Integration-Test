package com.ca.nbiapps.integration.test;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.ca.nbiapps.build.model.TestCaseContext;
import com.ca.nbiapps.core.compnents.CommonComponent;
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
	
	@Test
	public void process(final ITestContext testContext) {
		Logger logger = null;
		try {
			logger = commonComponent.getLogger("Integration-Test-Log", "INFO");
			String testCaseName = testContext.getCurrentXmlTest().getName();
			JSONObject jsonTemplateObject = getJSONObject(logger, "testcasesTemplates/"+testCaseName+".json");
			
			TestCaseContext testCaseContext = new TestCaseContext();
			testCaseContext.setTestCaseData(jsonTemplateObject);
			testCaseContext.setTestCaseName(testCaseName);
			testCaseContext.setLogger(logger);
			testCaseService.process(testCaseContext);
		} catch (Exception e) {
			handleException(logger, e);
		} finally {
			
		}
	}
}
