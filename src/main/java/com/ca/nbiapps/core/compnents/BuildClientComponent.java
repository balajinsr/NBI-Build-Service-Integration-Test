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
import com.ca.nbiapps.build.model.BuildTestStats;
import com.ca.nbiapps.build.model.Head;
import com.ca.nbiapps.build.model.PullRequest;
import com.ca.nbiapps.build.model.PullRequestEvent;
import com.ca.nbiapps.build.model.Repo;
import com.ca.nbiapps.build.model.ResponseModel;
import com.ca.nbiapps.build.model.StepResults;
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
		String url = propertyComponents.getBuildServiceBaseUrl() + "/processPullRequest";
		StepResults stepResults = getStepResult("Preview");
		String stepName = BuildTestStats.BUILD_PULL_REQUEST.name();
		try {
			PullRequestEvent pullReqEvent = getPullRequest(testCaseContext, taskId);
			HttpHeaders requestHeaders = restServiceClient.createHttpHeader("*/*", "UTF-8", "application/json");
			requestHeaders.add("X-GitHub-Event", "pull_request");

			if (taskId != null) {
				Type returnTypeOfObject = new TypeToken<PullRequestEvent>() {
				}.getType();
				String payLoad = toJsonFromObject(pullReqEvent, returnTypeOfObject);
				Type returnTypeOfBaseResponse = new TypeToken<BaseResponse>() {
				}.getType();
				BaseResponse baseRes = (BaseResponse) restServiceClient.postRestAPICall(logger, url, requestHeaders, payLoad, ResponseModel.class, returnTypeOfBaseResponse);
				logger.info("PullRequest Response : " + baseRes.toString());
				testCaseContext.setTestCaseSuccess(baseRes.isResponseStatus());
				if (!baseRes.isResponseStatus()) {
					setStepFailedValues(stepName, "Failed to create pull request", stepResults);
				} else {
					setStepSuccessValues(stepName, stepResults);
				}
			} else {
				// TODO: send an email
				testCaseContext.setTestCaseSuccess(false);
				logger.info("DT number not created in salesforce.. Try again.!!!");
				setStepFailedValues(stepName, "DT number not created in salesforce.. Try again.!!!", stepResults);
			}
		} catch (Exception e) {
			setStepFailedValues(stepName, "Failed to create pull request " + e.getMessage(), stepResults);
			throw e;
		} finally {
			testCaseContext.getStepResults().add(stepResults);
		}
	}

	public void doBuildAssert(TestCaseContext testCaseContext, String taskId, BuildData actualBuildData, JSONObject buildTask) throws Exception {
		StepResults stepResults = getStepResult("Preview");
		String stepName = BuildTestStats.BUILD_STATUS_CHECK.name();
		Logger logger = testCaseContext.getLogger();
		StepResults stepResultsAssert = null;
		boolean expectedArtifactsAvailable = false;
		try {

			JSONObject buildAssertValues = buildTask.getJSONObject("buildAssertValues");
			JSONObject expectedToVerify = buildAssertValues.getJSONObject("expectedToVerify");
			expectedArtifactsAvailable = expectedToVerify.getBoolean("isArtifactsAvailable");

			if (actualBuildData.isArtifactsAvailable() && expectedArtifactsAvailable) {
				JSONArray expectedFilesInPackage = expectedToVerify.getJSONArray("expectedFilesInPackage");
				String buildSuccess = buildAssertValues.getString("buildStatus");
				String artifactUploadStatus = expectedToVerify.getString("artifactUploadStatus");

				if (!buildSuccess.equals(actualBuildData.getBuildStatus())) {
					testCaseContext.setTestCaseSuccess(false);
					setStepFailedValues(stepName, "Expected build status: " + buildSuccess + ", Actual Build Status: " + actualBuildData.getBuildStatus(), stepResults);
					return;
				} else {
					setStepSuccessValues(stepName, stepResults);
				}

				if (!artifactUploadStatus.equals(actualBuildData.getArtifactUploadStatus())) {
					testCaseContext.setTestCaseSuccess(false);
					setStepFailedValues(stepName,
							"Expected upload artifacts status: " + artifactUploadStatus + ", Actual upload artifacts Status: " + actualBuildData.getArtifactUploadStatus(),
							stepResults);
					return;
				} else {
					setStepSuccessValues(stepName, stepResults);
				}
				
				stepResultsAssert = getStepResult("Preview");
				String stepNameAssert = BuildTestStats.BUILD_PACKAGE_ASSERT.name();
				verifyBuildPackage(testCaseContext, taskId, actualBuildData, expectedFilesInPackage, stepNameAssert, stepResultsAssert);

			} else if (actualBuildData.isArtifactsAvailable() == expectedArtifactsAvailable) {
				testCaseContext.setTestCaseSuccess(true);
				setStepSuccessValues(stepName, stepResults);
			} else {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(stepName, "Expected artifacts =" + expectedArtifactsAvailable + ", actually artifacts - " + actualBuildData.isArtifactsAvailable(),
						stepResults);
			}
		} catch (Exception e) {
			setStepFailedValues(BuildTestStats.BUILD_STATUS_CHECK.name(), "Failed to do assert the build" + e.toString(), stepResults);
			throw e;
		} finally {
			testCaseContext.getStepResults().add(stepResults);
			if(stepResultsAssert != null) {		
				testCaseContext.getStepResults().add(stepResultsAssert);
			}
			
			if(!actualBuildData.isArtifactsAvailable() && !expectedArtifactsAvailable) {
				setSkippedStepResults(testCaseContext, "Preview", BuildTestStats.BUILD_PACKAGE_ASSERT.name(), "No Artifacts to do build package assert.");
			}
		}
	}

	public BuildData waitForBuildCompleteAndGetBuildResults(TestCaseContext testCaseContext, String taskId, Long previousBuildNumber, int buildStepIndex) throws Exception {
		int attempts = 1;
		BuildData buildData = null;
		Logger logger = testCaseContext.getLogger();
		StepResults stepResults = getStepResult("Preview");
		String stepName = BuildTestStats.BUILD_RESULT_FETCH.name();
		try {
			while (attempts <= 10) {
				try {
					Thread.sleep(15000L);
					previousBuildNumber = previousBuildNumber == null ? 992 : previousBuildNumber;
					String url = propertyComponents.getBuildServiceBaseUrl() + "/test/getBuildResults?siloName=" + propertyComponents.getSiloName() + "&buildNumber="
							+ (previousBuildNumber + 1) + "&taskId=" + taskId;
					Type returnTypeOfObject = new TypeToken<ResponseModel>() {
					}.getType();
					ResponseModel responseModel = (ResponseModel) restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
					Type returnTypeOfSub = new TypeToken<BuildData>() {
					}.getType();
					buildData = (BuildData) restServiceClient.getSubJSONParseCall(logger, returnTypeOfSub, responseModel);
					if (buildData != null && buildData.getBuildStatus() != null && !"InProgress".equals(buildData.getBuildStatus())) {
						testCaseContext.setTestCaseSuccess(true);
						setStepSuccessValues(stepName, stepResults);
						return buildData;
					}
				} catch (Exception e) {
					testCaseContext.setTestCaseSuccess(false);
					setStepFailedValues(stepName, "BuildComplete and getBuildResults fetch failed " + e.getMessage(), stepResults);
					logger.error("Error: " + e, e);
				}
				attempts++;
			}
			return buildData;
		} catch (Exception e) {
			setStepFailedValues(BuildTestStats.BUILD_RESULT_FETCH.name(), "Failed to fetch the build results." + e.toString(), stepResults);
			throw e;
		} finally {
			testCaseContext.getStepResults().add(stepResults);
		}
	}

	public Long getPreviousBuildNumber(Logger logger) throws Exception {
		try {
			String url = propertyComponents.getBuildServiceBaseUrl() + "/test/getBuildNumber?siloName=" + propertyComponents.getSiloName();
			Type returnTypeOfObject = new TypeToken<ResponseModel>() {
			}.getType();
			ResponseModel responseModel = (ResponseModel) restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			Type returnTypeOfSub = new TypeToken<BuildData>() {
			}.getType();

			BuildData buildData = (BuildData) restServiceClient.getSubJSONParseCall(logger, returnTypeOfSub, responseModel);
			return buildData.getBuildNumber();
		} catch (Exception e) {
			throw e;
		}
	}

	public void verifyBuildPackage(TestCaseContext testCaseContext, String taskId, BuildData actualBuildData, JSONArray expectedFilesInPackage, String stepName, StepResults stepResults) throws Exception {
		Logger logger = testCaseContext.getLogger();
		Long buildNumber = actualBuildData.getBuildNumber();
		StepResults downLoadStepResult = null;
		try {
			String saveLocalDir = getPathByOSSpecific(propertyComponents.getArtifactoryDownloadLocalDir()).toString() + File.separator + testCaseContext.getTestCaseName();
			saveLocalDir = saveLocalDir + "/" + taskId + "_" + buildNumber;

			String artifactsUri = "/" + propertyComponents.getSiloName() + "/" + taskId + "/" + buildNumber;
			logger.info("Build artifacts URL: " + artifactsUri);
			boolean isSuccessDownload = downloadPackage(logger, "Preview", saveLocalDir, artifactsUri);
			downLoadStepResult = getStepResult("Preview");
			String downLoadStepName = BuildTestStats.BUILD_PACKAGE_DOWNLOAD.name();
			if (isSuccessDownload) {
				setStepSuccessValues(downLoadStepName, downLoadStepResult);
				assertPackageFiles(testCaseContext, saveLocalDir, expectedFilesInPackage, stepName, stepResults);
			} else {
				testCaseContext.setTestCaseSuccess(false);
				setStepFailedValues(downLoadStepName, "build package download failed.", downLoadStepResult);
			}
			
		} catch (Exception e) {
			setStepFailedValues(BuildTestStats.BUILD_PACKAGE_ASSERT.name(), "Failed to verify build package.." + e.toString(), stepResults);
			throw e;
		} finally {
			if(downLoadStepResult != null) {
				testCaseContext.getStepResults().add(downLoadStepResult);
			}
		}
	}

	public void resetBuildDBEntries(TestCaseContext testCaseContext) throws Exception {
		Logger logger = testCaseContext.getLogger();
		try {
			
			String url = propertyComponents.getBuildServiceBaseUrl() + "/test/resetDB?siloName=" + propertyComponents.getSiloName() + "buildNumbers=&releaseIds=";
			Type returnTypeOfObject = new TypeToken<ResponseModel>() {
			}.getType();
			ResponseModel responseModel = (ResponseModel) restServiceClient.getRestAPICall(logger, url, ResponseModel.class, returnTypeOfObject);
			Type returnTypeOfSub = new TypeToken<BuildData>() {
			}.getType();

			BaseResponse baseResponse = (BaseResponse) restServiceClient.getSubJSONParseCall(logger, returnTypeOfSub, responseModel);
			testCaseContext.setTestCaseSuccess(baseResponse.isResponseStatus());
		} catch (Exception e) {
			throw e;
		}
	}

}
