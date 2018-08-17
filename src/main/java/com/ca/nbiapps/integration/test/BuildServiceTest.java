package com.ca.nbiapps.integration.test;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
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

	public Map<String, BuildTestStats> results;
	@AfterSuite
	public void beforeSuite() {
		results = new LinkedHashMap<>();
	}

	@Test
	public void process(final ITestContext testContext) {
		Logger logger = null;
		TestCaseContext testCaseContext = new TestCaseContext();
		String testCaseName = testContext.getCurrentXmlTest().getName();
		try {
			logger = gitComponent.getLogger("Integration-Test-Log", "INFO");
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
			results.put(testCaseName, testCaseContext.getBuildTestStats());
			try {
				testCaseService.reset(testCaseContext);
			} catch (Exception e) {
				handleException(logger, e);
				org.testng.Assert.assertTrue(false);
			}
		}
	}

	@AfterSuite
	public void printTestResults() {
		Iterator<String> it = results.keySet().iterator();
		while (it.hasNext()) {
			String testCaseName = it.next();
			BuildTestStats buildTestStats = results.get(testCaseName);
			for (BuildTestStats buildTestStat : buildTestStats.values()) {
				for (StepResults step : buildTestStat.getStepResults()) {
					logger.info("====================="+testCaseName+"========================== Start");
					logger.info(step.toString());
					logger.info("====================="+testCaseName+"========================== End");
				}

			}
		}
	}
}
