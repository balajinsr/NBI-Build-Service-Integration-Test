package com.ca.nbiapps.core.compnents;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.ca.nbiapps.build.client.RestServiceClient;
import com.ca.nbiapps.build.model.Base;
import com.ca.nbiapps.build.model.BaseResponse;
import com.ca.nbiapps.build.model.BuildData;
import com.ca.nbiapps.build.model.Head;
import com.ca.nbiapps.build.model.PullRequest;
import com.ca.nbiapps.build.model.PullRequestEvent;
import com.ca.nbiapps.build.model.Repo;
import com.ca.nbiapps.build.model.ResponseModel;
import com.ca.nbiapps.build.model.TestCaseContext;
import com.ca.nbiapps.build.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
/**
 * 
 * @author Balaji N
 *
 */
@Component
public class BuildClientComponent {
	
	@Autowired
	PropertyComponents propertyComponents;
	
	@Autowired
	RestServiceClient restServiceClient;
	
	@Autowired
	CommonComponent commonComponent;
	
	private PullRequestEvent getPullRequest(TestCaseContext testCaseContext, String taskId) throws Exception {
		PullRequestEvent pullReqEvent = new PullRequestEvent();
		pullReqEvent.setAction("opened");

		PullRequest pullReq = new PullRequest();
		pullReq.setTitle(taskId);

		Head head = new Head();
		head.setRef("master");
		head.setSha(testCaseContext.getHeadGitCommitId());

		User user = new User();
		user.setLogin(propertyComponents.getGitUserName());

		Repo repo = new Repo();
		repo.setFork(true);
		repo.setName(propertyComponents.getSiloName());
		repo.setSsh_url(propertyComponents.getGitForkSshUrl());

		head.setUser(user);
		head.setRepo(repo);

		Base base = new Base();
		base.setSha(testCaseContext.getBaseGitCommitId());
		base.setRepo(repo);

		pullReq.setBase(base);
		pullReq.setHead(head);

		pullReqEvent.setPull_request(pullReq);
		return pullReqEvent;
	}

	

	public void pullRequest(TestCaseContext testCaseContext, String taskId) throws Exception {
		Logger logger = testCaseContext.getLogger();
		String url = propertyComponents.getBuildServiceBaseUrl()+"/processPullRequest";
		try {
			PullRequestEvent pullReqEvent = getPullRequest(testCaseContext, taskId);
			HttpHeaders requestHeaders = restServiceClient.createHttpHeader("*/*", "UTF-8", "application/json");
			requestHeaders.add("X-GitHub-Event", "pull_request");

			if (taskId != null) {
				Type returnTypeOfObject = new TypeToken<PullRequestEvent>() {
				}.getType();
				String payLoad = commonComponent.toJsonFromObject(pullReqEvent, returnTypeOfObject);
				System.out.println("PayLoad:" + payLoad);
				Type returnTypeOfBaseResponse = new TypeToken<BaseResponse>() {
				}.getType();
				BaseResponse baseRes = (BaseResponse) restServiceClient.postRestAPICall(logger, url, requestHeaders, payLoad, ResponseModel.class, returnTypeOfBaseResponse);
				logger.info("PullRequest Response : "+baseRes.toString());
				testCaseContext.setTestCaseSuccess(baseRes.isResponseStatus());
			} else {
				// TODO: send an email
				logger.info("DT number not created in salesforce.. Try again.!!!");
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param testCaseContext
	 * @param taskId
	 * @param buildTask
	 */
	public void doBuildAssert(TestCaseContext testCaseContext, String taskId, BuildData actualBuildData,  JSONObject buildTask) {
		Logger logger = testCaseContext.getLogger();
		JSONObject buildAssertValues = buildTask.getJSONObject("buildAssertValues");
		JSONObject expectedToVerify = buildAssertValues.getJSONObject("expectedToVerify");
		boolean expectedArtifactsAvailable = expectedToVerify.getBoolean("isArtifactsAvailable");
		if(actualBuildData.isArtifactsAvailable() && expectedArtifactsAvailable) {
			String buildSuccess = buildAssertValues.getString("buildStatus");
			String artifactUploadStatus = expectedToVerify.getString("artifactUploadStatus");
			
			if(!buildSuccess.equals(actualBuildData.getBuildStatus())) {
				testCaseContext.setTestCaseSuccess(false);
				testCaseContext.setTestCaseFailureReason("Expected build status: "+buildSuccess+", Actual Build Status: "+actualBuildData.getBuildStatus());
				return;
			}
			
			if(!artifactUploadStatus.equals(actualBuildData.getArtifactUploadStatus())) {
				testCaseContext.setTestCaseSuccess(false);
				testCaseContext.setTestCaseFailureReason("Expected upload artifacts status: "+artifactUploadStatus+", Actual upload artifacts Status: "+actualBuildData.getArtifactUploadStatus());
				return;
			}
			logger.info("BuildResults: :: "+actualBuildData.toString());
			testCaseContext.setTestCaseSuccess(true);
		} else {
			testCaseContext.setTestCaseSuccess(false);
			testCaseContext.setTestCaseFailureReason("Expected artifacts ="+expectedArtifactsAvailable+"actually artifacts - "+actualBuildData.isArtifactsAvailable());
		}
	}

	

	/**
	 * @param testCaseContext
	 */
	public BuildData waitForBuildCompleteAndGetBuildResults(TestCaseContext testCaseContext, String taskId, Long previousBuildNumber) throws Exception {
		int attempts = 1;
		BuildData buildData = null;
		Logger logger = testCaseContext.getLogger();
		while(attempts <= 3) {
			try {
				Thread.sleep(15000L);
				previousBuildNumber = previousBuildNumber == null?992:previousBuildNumber;
				String url = propertyComponents.getBuildServiceBaseUrl()+"/test/getBuildResults?siloName="+propertyComponents.getSiloName()+"&buildNumber="+(previousBuildNumber+1)+"&taskId="+taskId;
				Type returnTypeOfObject = new TypeToken<ResponseModel>() {
				}.getType();
				ResponseModel responseModel = (ResponseModel)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
				Type returnTypeOfSub = new TypeToken<BuildData>() {
				}.getType();
				buildData = (BuildData)restServiceClient.getSubJSONParseCall(logger, returnTypeOfSub, responseModel);
				if(buildData!= null && buildData.getBuildStatus() != null && !"InProgress".equals(buildData.getBuildStatus())) {
					testCaseContext.setTestCaseSuccess(true);
					return buildData;
				}
			} catch(Exception e) {
				testCaseContext.setTestCaseSuccess(false);
				logger.error("Error: "+e,e);
			}
			attempts++;
		}
		return buildData;
	}

	public Long getPreviousBuildNumber(Logger logger) throws Exception {
		try {
			String url = propertyComponents.getBuildServiceBaseUrl()+"/test/getBuildNumber?siloName="+propertyComponents.getSiloName();
			Type returnTypeOfObject = new TypeToken<ResponseModel>() {
			}.getType();
			ResponseModel responseModel = (ResponseModel)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			Type returnTypeOfSub = new TypeToken<BuildData>() {
			}.getType();
			
			BuildData buildData = (BuildData)restServiceClient.getSubJSONParseCall(logger, returnTypeOfSub, responseModel);
			return buildData.getBuildNumber();
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	

	public void resetDB(TestCaseContext testCaseContext) throws Exception {
		Logger logger = testCaseContext.getLogger();
		try {
			String url = propertyComponents.getBuildServiceBaseUrl()+"/test/resetDB?siloName="+propertyComponents.getSiloName()+"buildNumber="+testCaseContext.getBuildNumber();
			Type returnTypeOfObject = new TypeToken<ResponseModel>() {
			}.getType();
			ResponseModel responseModel = (ResponseModel)restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			Type returnTypeOfSub = new TypeToken<BuildData>() {
			}.getType();
			
			BaseResponse baseResponse = (BaseResponse)restServiceClient.getSubJSONParseCall(logger, returnTypeOfSub, responseModel);
			testCaseContext.setTestCaseSuccess(baseResponse.isResponseStatus());
		} catch (Exception e) {
			throw e;
		}
	}
	
}
