/**
 * 
 */
package com.ca.nbiapps.service.impl;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ca.nbiapps.build.client.RestServiceClient;
import com.ca.nbiapps.build.model.BaseResponse;
import com.ca.nbiapps.build.model.ResponseModel;
import com.ca.nbiapps.build.model.TestCaseContext;
import com.ca.nbiapps.core.compnents.PropertyComponents;
import com.google.gson.reflect.TypeToken;

/**
 * @author Balaji N
 *
 */
@Component
public class SalesForceComponent {
	
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
				testCaseContext.setTestCaseFailureReason("Failed to update taskId status in SalesForce");
				return;
			}
			testCaseContext.setTestCaseSuccess(true);
		} catch (Exception e) {
			throw e;
		}
		
	}

	/**
	 * @param testCaseContext
	 * @param tasks
	 */
	public void adjustTaskIdStatusToDoConsolidationPackage(TestCaseContext testCaseContext, JSONArray tasks) throws Exception {
		Logger logger = testCaseContext.getLogger();
		try {			
			String url = propertyComponents.getBuildServiceBaseUrl()+"/test/updateSFStatusForConsolidated?siloName="+propertyComponents.getSiloName()+"&taskIds="+tasks.toString()+"&cycleName=Preview";
			Type returnTypeOfObject = new TypeToken<BaseResponse>() {
			}.getType();
			BaseResponse baseResponse = (BaseResponse)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			if(!baseResponse.isResponseStatus()) {
				testCaseContext.setTestCaseSuccess(false);
				testCaseContext.setTestCaseFailureReason("Failed to update taskId status in SalesForce");
				return;
			}
		} catch (Exception e) {
			throw e;
		}		
	}
}
