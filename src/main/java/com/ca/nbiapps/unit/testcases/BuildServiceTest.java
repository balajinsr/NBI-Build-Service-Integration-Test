package com.ca.nbiapps.unit.testcases;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.integration.test.BaseTest;

/**
 * 
 * @author Balaji N
 *
 */
public class BuildServiceTest extends BaseTest {
	
	@Autowired
	GitComponent gitComponent;
	
	@Test
	public void testGitClone(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			gitComponent.cloneRepo(logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testAddUpstream(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			gitComponent.addUpstream(logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRemoveUpstream(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			gitComponent.removeUpstream(logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
