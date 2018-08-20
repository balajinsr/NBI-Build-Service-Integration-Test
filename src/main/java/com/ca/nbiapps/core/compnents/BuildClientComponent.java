package com.ca.nbiapps.core.compnents;

import java.io.File;
import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import com.google.gson.reflect.TypeToken;
/**
 * 
 * @author Balaji N
 *
 */
@Component
public class BuildClientComponent extends ArtifactoryComponent {
	
	@Autowired
	PropertyComponents propertyComponents;
	
	@Autowired
	RestServiceClient restServiceClient;
	
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

	public void pullRequest(TestCaseContext testCaseContext, String taskId, int buildStepIndex) throws Exception {
		Logger logger = testCaseContext.getLogger();
		String url = propertyComponents.getBuildServiceBaseUrl()+"/processPullRequest";
		if(buildStepIndex > 0) fillBuildStepResults(testCaseContext.getBuildTestStats().BUILD_PULL_REQUEST, "Preview");
		try {
			PullRequestEvent pullReqEvent = getPullRequest(testCaseContext, taskId);
			HttpHeaders requestHeaders = restServiceClient.createHttpHeader("*/*", "UTF-8", "application/json");
			requestHeaders.add("X-GitHub-Event", "pull_request");

			if (taskId != null) {
				Type returnTypeOfObject = new TypeToken<PullRequestEvent>() {
				}.getType();
				String payLoad = toJsonFromObject(pullReqEvent, returnTypeOfObject);
				System.out.println("PayLoad:" + payLoad);
				Type returnTypeOfBaseResponse = new TypeToken<BaseResponse>() {
				}.getType();
				BaseResponse baseRes = (BaseResponse) restServiceClient.postRestAPICall(logger, url, requestHeaders, payLoad, ResponseModel.class, returnTypeOfBaseResponse);
				logger.info("PullRequest Response : "+baseRes.toString());
				testCaseContext.setTestCaseSuccess(baseRes.isResponseStatus());
				if(!baseRes.isResponseStatus()) {
					setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_PULL_REQUEST, 0, "Failed to create pull request"); 
				} else {
					setStepSuccessStatus(testCaseContext.getBuildTestStats().BUILD_PULL_REQUEST, 0); 
				}
			} else {
				// TODO: send an email
				testCaseContext.setTestCaseSuccess(false);
				logger.info("DT number not created in salesforce.. Try again.!!!");
				setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_ADJUST_TASKID_STATUS, 0, "DT number not created in salesforce.. Try again.!!!"); 
			}

		} catch (Exception e) {
			setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_PULL_REQUEST, 0, "Failed to create pull request "+e.getMessage()); 
			throw e;
		}
	}

	/**
	 * @param testCaseContext
	 * @param taskId
	 * @param buildTask
	 */
	public void doBuildAssert(TestCaseContext testCaseContext, String taskId, BuildData actualBuildData,  JSONObject buildTask, int buildStepIndex) throws Exception {
		Logger logger = testCaseContext.getLogger();
		JSONObject buildAssertValues = buildTask.getJSONObject("buildAssertValues");
		JSONObject expectedToVerify = buildAssertValues.getJSONObject("expectedToVerify");
		boolean expectedArtifactsAvailable = expectedToVerify.getBoolean("isArtifactsAvailable");
		
		if(buildStepIndex > 0) {
			fillBuildStepResults(testCaseContext.getBuildTestStats().BUILD_STATUS_CHECK, "Preview");
		}
		
		if(actualBuildData.isArtifactsAvailable() && expectedArtifactsAvailable) {
			JSONArray expectedFilesInPackage = expectedToVerify.getJSONArray("expectedFilesInPackage");
			String buildSuccess = buildAssertValues.getString("buildStatus");
			String artifactUploadStatus = expectedToVerify.getString("artifactUploadStatus");
			
			if(!buildSuccess.equals(actualBuildData.getBuildStatus())) {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(testCaseContext.getBuildTestStats(), buildStepIndex, "Expected build status: "+buildSuccess+", Actual Build Status: "+actualBuildData.getBuildStatus());
				return;
			} else {
				setStepSuccessStatus(testCaseContext.getBuildTestStats().BUILD_STATUS_CHECK, buildStepIndex);
			}
			
			if(!artifactUploadStatus.equals(actualBuildData.getArtifactUploadStatus())) {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_STATUS_CHECK, buildStepIndex, "Expected upload artifacts status: "+artifactUploadStatus+", Actual upload artifacts Status: "+actualBuildData.getArtifactUploadStatus());
				return;
			}
			
			verifyBuildPackage(testCaseContext, taskId, actualBuildData.getBuildNumber(), expectedFilesInPackage);
			
		} else if(actualBuildData.isArtifactsAvailable() == expectedArtifactsAvailable) {
			testCaseContext.setTestCaseSuccess(true);
			setStepSuccessStatus(testCaseContext.getBuildTestStats().BUILD_STATUS_CHECK, buildStepIndex);
		} else {
			testCaseContext.setTestCaseSuccess(false);
			setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_STATUS_CHECK, buildStepIndex, "Expected artifacts ="+expectedArtifactsAvailable+", actually artifacts - "+actualBuildData.isArtifactsAvailable());
		}
	}

	
	public BuildData waitForBuildCompleteAndGetBuildResults(TestCaseContext testCaseContext, String taskId, Long previousBuildNumber, int buildStepIndex) throws Exception {
		int attempts = 1;
		BuildData buildData = null;
		Logger logger = testCaseContext.getLogger();
		if(buildStepIndex > 0) {
			fillBuildStepResults(testCaseContext.getBuildTestStats().BUILD_RESULT_FETCH, "Preview");
		}
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
					setStepSuccessStatus(testCaseContext.getBuildTestStats().BUILD_RESULT_FETCH,0);
					return buildData;
				}
			} catch(Exception e) {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_RESULT_FETCH,0, "BuildComplete and getBuildResults fetch failed "+e.getMessage());
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
	
	public void verifyBuildPackage(TestCaseContext testCaseContext, String taskId, Long buildNumber, JSONArray expectedFilesInPackage) throws Exception {
		Logger logger = testCaseContext.getLogger();
		String saveLocalDir = getPathByOSSpecific(propertyComponents.getArtifactoryDownloadLocalDir()).toString()+File.separator+testCaseContext.getTestCaseName();
		saveLocalDir = saveLocalDir+"/"+taskId+"_"+buildNumber;
		
		String artifactsUri = "/" + propertyComponents.getSiloName() + "/" + taskId + "/" + buildNumber;
		logger.info("Build artifacts URL: "+artifactsUri);
		boolean isSuccessDownload = downloadPackage(logger, "Preview", saveLocalDir, artifactsUri);
		if (isSuccessDownload) {
			setStepSuccessStatus(testCaseContext.getBuildTestStats().BUILD_PACKAGE_DOWNLOAD, 0);
			assertPackageFiles(testCaseContext, saveLocalDir, expectedFilesInPackage, 0, testCaseContext.getBuildTestStats().BUILD_PACKAGE_ASSERT);
		} else {
			testCaseContext.setTestCaseSuccess(false);
			setStepFailedValues(testCaseContext.getBuildTestStats().BUILD_PACKAGE_DOWNLOAD, 0, "build package download failed.");
		}
	}
	
	public void resetBuildDBEntries(TestCaseContext testCaseContext) throws Exception {
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
