package com.ca.nbiapps.unit.testcases;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.ca.nbiapps.build.client.RestServiceClient;
import com.ca.nbiapps.build.model.BaseResponse;
import com.ca.nbiapps.build.model.ResponseModel;
import com.ca.nbiapps.core.compnents.GitComponent;
import com.google.gson.reflect.TypeToken;

public class TestExample extends GitComponent {
	
	@Autowired
	RestServiceClient restServiceClient;
	
	@Test
	public void testPropertiesRead(final ITestContext testContext) {
		try {
			//String key = PropertyUtils.getValueFromProperties("BUILD_SERVICE_URL", "testng.properties");
			 //String sshPath = System.getenv("systemdrive")+File.separator+System.getenv("homepath");
			// System.out.println("::"+sshPath);
			//	Assert.assertEquals("Hell1o", key);
			System.out.println("Current runing test name: "+testContext.getCurrentXmlTest().getName());
			Assert.assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
		}
	}
	
	@Test(enabled=false)
	public void test(final ITestContext testContext) {
		
		try {
			Logger logger = getLogger("Test", "INFO");
			Type returnTypeOfObject = new TypeToken<BaseResponse>() {}.getType();
			BaseResponse baseResponse = (BaseResponse)restServiceClient.getRestAPICall(logger, "http://localhost:8081/nbiAppBuild/service/changeLogLevel?siloName=NBI-Applications-SECUREDEMO&logLevel=INFO", ResponseModel.class, returnTypeOfObject);
			
			
	        System.out.println(baseResponse.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test(enabled=false)
	public void testReset() throws Exception {
		Logger logger = getLogger("Test", "INFO");
		try (Git git = getGit(logger)) {
			reset(git, "fde54cca9bab31d5712308800d2ceca31d89fda9");
			gitPush(git, true);
		} catch (Exception e) {
			Assert.assertTrue(false);
			logger.error("Error in testAdddNewFileAndCommit:- "+e,e);
		}
	}
	
}
