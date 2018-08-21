/**
 * 
 */
package com.ca.nbiapps.core.compnents;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ca.nbiapps.build.client.RestServiceClient;
import com.ca.nbiapps.build.model.BaseResponse;
import com.ca.nbiapps.build.model.BuildTestStats;
import com.ca.nbiapps.build.model.ResponseModel;
import com.ca.nbiapps.build.model.StepResults;
import com.ca.nbiapps.build.model.TestCaseContext;
import com.google.gson.reflect.TypeToken;

/**
 * @author Balaji N
 *
 */
@Component
public class SalesForceComponent extends CommonComponent {
	
	@Autowired
	PropertyComponents propertyComponents;
	
	@Autowired
	RestServiceClient restServiceClient;
	
	public void adjustTaskIdStatusForAcceptTheBuild(TestCaseContext testCaseContext, String taskId) throws Exception {
		StepResults stepResults = getStepResult("Preview");
		Logger logger = testCaseContext.getLogger();
		try {
			String url = propertyComponents.getBuildServiceBaseUrl()+"/test/updateSFStatusForBuild?siloName="+propertyComponents.getSiloName()+"&taskId="+taskId+"&cycleName=Preview";
			Type returnTypeOfObject = new TypeToken<BaseResponse>() {
			}.getType();
			BaseResponse baseResponse = (BaseResponse)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			
			if(!baseResponse.isResponseStatus()) {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(BuildTestStats.BUILD_ADJUST_TASKID_STATUS.name(), "Failed to update taskId status in SalesForce", stepResults); 
				return;
			}
			logger.info("TestCase: ["+testCaseContext.getTestCaseName()+"], Changed task ["+taskId+"] status to \"Assigned\" and task Accpeted - YES");
			testCaseContext.setTestCaseSuccess(true);
			setStepSuccessValues(BuildTestStats.BUILD_ADJUST_TASKID_STATUS.name(), stepResults); 
		} catch (Exception e) {
			setStepFailedValues(BuildTestStats.BUILD_ADJUST_TASKID_STATUS.name(), "Failed to update taskId status to \"Assigned\" in SalesForce "+e.toString(), stepResults); 
			throw e;
		} finally {
			testCaseContext.getStepResults().add(stepResults);
		}
	}
	
	
	public void adjustTaskIdStatusToDoConsolidationPackage(TestCaseContext testCaseContext, String tasks, String cycleName) throws Exception {
		Logger logger = testCaseContext.getLogger();
		StepResults stepResults = getStepResult(cycleName);
		try {	
			String url = propertyComponents.getBuildServiceBaseUrl()+"/test/updateSFStatusForConsolidated?siloName="+propertyComponents.getSiloName()+"&taskIds="+tasks.toString()+"&cycleName=Preview";
			Type returnTypeOfObject = new TypeToken<BaseResponse>() {
			}.getType();
			BaseResponse baseResponse = (BaseResponse)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			testCaseContext.setTestCaseSuccess(baseResponse.isResponseStatus());
			
			if(!baseResponse.isResponseStatus()) {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(BuildTestStats.CON_PACKAGE_TASKIDS_STATUS.name(), "Failed to update taskId status to \"Ready to Deploy\" in SalesForce", stepResults); 
				return;
			}
			testCaseContext.setTestCaseSuccess(true);
			setStepSuccessValues(BuildTestStats.CON_PACKAGE_TASKIDS_STATUS.name(), stepResults); 
			logger.info("TestCase: ["+testCaseContext.getTestCaseName()+"], Changed tasks ["+tasks+"] status to \"Ready to Deploy\"");
		} catch (Exception e) {
			setStepFailedValues(BuildTestStats.CON_PACKAGE_TASKIDS_STATUS.name(), "Failed to update taskId status to \"Ready to Deploy\" in SalesForce - "+e.getMessage(), stepResults); 
			throw e;
		} finally {
			testCaseContext.getStepResults().add(stepResults);
		}
	}
}
