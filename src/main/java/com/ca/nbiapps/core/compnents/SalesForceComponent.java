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
import com.ca.nbiapps.build.model.ResponseModel;
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
		Logger logger = testCaseContext.getLogger();
		try {
			String url = propertyComponents.getBuildServiceBaseUrl()+"/test/updateSFStatusForBuild?siloName="+propertyComponents.getSiloName()+"&taskId="+taskId+"&cycleName=Preview";
			Type returnTypeOfObject = new TypeToken<BaseResponse>() {
			}.getType();
			BaseResponse baseResponse = (BaseResponse)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			if(!baseResponse.isResponseStatus()) {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_ADJUST_TASKID_STATUS, 0, "Failed to update taskId status in SalesForce"); 
				return;
			}
			logger.info("TestCase: ["+testCaseContext.getTestCaseName()+"], Changed task ["+taskId+"] status to \"Assigned\" and task Accpeted - YES");
			testCaseContext.setTestCaseSuccess(true);
			setStepSuccessStatus(testCaseContext.getBuildTestStats().BUILD_ADJUST_TASKID_STATUS, 0); 
		} catch (Exception e) {
			setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_ADJUST_TASKID_STATUS, 0, "Failed to update taskId status to \"Assigned\" in SalesForce "+e.toString()); 
			throw e;
		}	
	}

	public void adjustTaskIdStatusToDoConsolidationPackage(TestCaseContext testCaseContext, String tasks, int cycleIndex) throws Exception {
		Logger logger = testCaseContext.getLogger();
		try {	
			String url = propertyComponents.getBuildServiceBaseUrl()+"/test/updateSFStatusForConsolidated?siloName="+propertyComponents.getSiloName()+"&taskIds="+tasks.toString()+"&cycleName=Preview";
			Type returnTypeOfObject = new TypeToken<BaseResponse>() {
			}.getType();
			BaseResponse baseResponse = (BaseResponse)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			testCaseContext.setTestCaseSuccess(baseResponse.isResponseStatus());
			if(!baseResponse.isResponseStatus()) {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(testCaseContext.getBuildTestStats().CON_PACKAGE_TASKIDS_STATUS, cycleIndex, "Failed to update taskId status to \"Ready to Deploy\" in SalesForce"); 
				return;
			}
			testCaseContext.setTestCaseSuccess(true);
			setStepSuccessStatus(testCaseContext.getBuildTestStats().BUILD_ADJUST_TASKID_STATUS, 0); 
			logger.info("TestCase: ["+testCaseContext.getTestCaseName()+"], Changed tasks ["+tasks+"] status to \"Ready to Deploy\"");
		} catch (Exception e) {
			setStepFailedValues(testCaseContext.getBuildTestStats().CON_PACKAGE_TASKIDS_STATUS, cycleIndex, "Failed to update taskId status to \"Ready to Deploy\" in SalesForce - "+e.getMessage()); 
			throw e;
		}		
	}
}
