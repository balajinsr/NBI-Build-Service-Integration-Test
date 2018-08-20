package com.ca.nbiapps.integration.test;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.ca.nbiapps.build.model.StepResults;
import com.ca.nbiapps.build.model.StepResults.BuildTestStats;
import com.ca.nbiapps.build.model.TestCaseContext;
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
	GitComponent gitComponent;

	@Test
	public void process(final ITestContext testContext) {
		Logger logger = null;
		TestCaseContext testCaseContext = new TestCaseContext();
		String testCaseName = testContext.getCurrentXmlTest().getName();
		
		try {
			logger = gitComponent.getLogger("Integration-Test-Log", "INFO");
			logger.info("====================="+testCaseName+"========================== Start");
			JSONObject jsonTemplateObject = getJSONObject(logger, "testcasesTemplates/" + testCaseName + ".json");
			testCaseContext.setTestCaseData(jsonTemplateObject);
			testCaseContext.setTestCaseName(testCaseName);
			testCaseContext.setLogger(logger);
			testCaseService.process(testCaseContext);
			org.testng.Assert.assertTrue(testCaseContext.isTestCaseSuccess());
		} catch (Exception e) {
			handleException(logger, e);
			org.testng.Assert.assertTrue(false);
		} finally {
			logger.info("TestCaseName: [" + testCaseContext.getTestCaseName() + "], TestCaseStatus: [" + testCaseContext.isTestCaseSuccess() + "]");		
			try {
				printTestResults(testCaseContext);
				testCaseService.reset(testCaseContext);
			} catch (Exception e) {
				handleException(logger, e);
				org.testng.Assert.assertTrue(false);
			}
			
			logger.info("====================="+testCaseName+"========================== End");
		}
	}

	
	public void printTestResults(TestCaseContext testCaseContext) {
		Logger logger = testCaseContext.getLogger();
		BuildTestStats buildTestStats = testCaseContext.getBuildTestStats();
		for (BuildTestStats buildTestStat : buildTestStats.values()) {
			for (StepResults step : buildTestStat.getStepResults()) {		
				if(isPromotedApproval(testCaseContext, step.getCycleName())) {
					logger.info(step.toString());
				}
			}

		}
	}

	private boolean isPromotedApproval(TestCaseContext testCaseContext, String cycleName) {
		if("Preview".equalsIgnoreCase(cycleName)) return true;
		JSONObject jsonTemplateObject = testCaseContext.getTestCaseData();
		boolean promotedApproval = false;
		if("ValFac".equalsIgnoreCase(cycleName)) {
			JSONObject jsonObject = (JSONObject) jsonTemplateObject.getJSONObject("promoteToValFac");
			promotedApproval = (boolean)jsonObject.get("promotedApproval");
		}
		
		if(promotedApproval && "Production".equalsIgnoreCase(cycleName)) {
			JSONObject jsonObject = (JSONObject) jsonTemplateObject.getJSONObject("promoteToProduction");
			promotedApproval = (boolean)jsonObject.get("promotedApproval");
		}
		return promotedApproval;
	}
}
