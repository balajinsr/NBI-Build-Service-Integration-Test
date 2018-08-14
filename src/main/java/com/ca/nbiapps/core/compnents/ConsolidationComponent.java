package com.ca.nbiapps.core.compnents;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.model.Folder;
import org.jfrog.artifactory.client.model.Item;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.ca.nbiapps.build.client.RestServiceClient;
import com.ca.nbiapps.build.model.ResponseModel;
import com.ca.nbiapps.build.model.TaskLevelBaseReq;
import com.ca.nbiapps.build.model.TaskLevelBaseRes;
import com.ca.nbiapps.build.model.TestCaseContext;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author Balaji N
 *
 */
@Component
public class ConsolidationComponent {

	@Autowired
	public PropertyComponents propertyComponents;

	@Autowired
	RestServiceClient restServiceClient;

	@Autowired
	CommonComponent commonComponent;

	@Autowired
	ArtifactoryComponent artifactoryComponent;

	public String doConsolidationPackage(TestCaseContext testCaseContext, String cycleName, String tasks) throws Exception {
		Logger logger = testCaseContext.getLogger();
		String url = propertyComponents.getBuildServiceBaseUrl() + "/pullDTLevelChanges";
		HttpHeaders requestHeaders = restServiceClient.createHttpHeader("*/*", "UTF-8", "application/json");

		TaskLevelBaseReq taskLevelReq = new TaskLevelBaseReq();
		taskLevelReq.setSiloName(propertyComponents.getSiloName());
		taskLevelReq.setCycleName(cycleName);
		taskLevelReq.setTaskIds(tasks);

		Type returnTypeOfObject = new TypeToken<TaskLevelBaseReq>() {
		}.getType();
		String payLoad = commonComponent.toJsonFromObject(taskLevelReq, returnTypeOfObject);

		System.out.println("PayLoad:" + payLoad);
		Type returnTypeOfResponseModel = new TypeToken<ResponseModel>() {
		}.getType();

		ResponseModel responseModel = (ResponseModel) restServiceClient.postRestAPICall(logger, url, requestHeaders, payLoad, ResponseModel.class, returnTypeOfResponseModel);
		Type returnTypeOfSub = new TypeToken<TaskLevelBaseRes>() {
		}.getType();
		TaskLevelBaseRes taskLevelBaseRes = (TaskLevelBaseRes) restServiceClient.getSubJSONParseCall(logger, returnTypeOfSub, responseModel);
		if (taskLevelBaseRes != null && taskLevelBaseRes.getReleaseId() != null) {
			testCaseContext.setTestCaseSuccess(true);
			return taskLevelBaseRes.getReleaseId();
		} else {
			testCaseContext.setTestCaseSuccess(false);
			testCaseContext.setTestCaseFailureReason("Consolidation packate generation failed");
		}
		return "";
	}

	public void verifyConsolidationPackage(TestCaseContext testCaseContext, String cycleName, String releaseId, JSONArray expectedFilesInPackage) throws Exception {
		Logger logger = testCaseContext.getLogger();
		String saveLocalDir = commonComponent.getPathByOSSpecific(propertyComponents.getArtifactoryDownloadLocalDir()).toString();

		boolean isSuccessDownload = downloadConsolidatedPackage(logger, cycleName, releaseId);
		if (isSuccessDownload) {
			File expectedFileInPackage = null;
			for (int i = 0; i < expectedFilesInPackage.length(); i++) {
				JSONObject object = expectedFilesInPackage.getJSONObject(i);
				expectedFileInPackage = new File(saveLocalDir + "/" + object.getString("filePath"));
				String expectedMd5Value = object.getString("md5Value");
				if (expectedFileInPackage.exists() && expectedMd5Value.equals(commonComponent.getMD5Sum(expectedFileInPackage))) {
					testCaseContext.setTestCaseSuccess(true);
				} else {
					testCaseContext.setTestCaseSuccess(false);
					if (!expectedFileInPackage.exists()) {
						testCaseContext.setTestCaseFailureReason("Incorrect consolidated package. [ExpectedFileInPackage = " + expectedFileInPackage.toString()
								+ " - isExistInPackage - " + expectedFileInPackage.exists() + "]");
						return;
					}
					String actualMd5Value = commonComponent.getMD5Sum(expectedFileInPackage);
					if (!expectedMd5Value.equals(actualMd5Value)) {
						testCaseContext.setTestCaseFailureReason(
								"Incorrect consolidated package . [ExpectedMd5Value = " + expectedMd5Value + " - actualMd5Value - " + actualMd5Value + "]");
					}
					return;
				}

			}
		} else {
			testCaseContext.setTestCaseSuccess(false);
			testCaseContext.setTestCaseFailureReason("consolidated package download failed.");
		}
	}

	private boolean downloadConsolidatedPackage(Logger logger, String cycleName, String releaseId) throws Exception {
		String saveLocalDir = commonComponent.getPathByOSSpecific(propertyComponents.getArtifactoryDownloadLocalDir()).toString();
		
		FileUtils.cleanDirectory(new File(saveLocalDir)); 
		
		Artifactory artifactory = artifactoryComponent.createArtifactory(propertyComponents.getArtifactoryUsername(), propertyComponents.getArtifactoryPassword(),
				propertyComponents.getArtifactoryBaseUrl());
		String uri = "/" + propertyComponents.getSiloName() + "/" + cycleName.toLowerCase() + "-release_" + releaseId;
		Folder folder = artifactory.repository(propertyComponents.getMavenRepoName()).folder(uri).info();
		List<Item> childrens = folder.getChildren();

		File file = null;
		for (Item item : childrens) {
			file = artifactoryComponent.downloadFile(artifactory, folder.getRepo(), folder.getPath() + item.getUri(), saveLocalDir + item.getUri());
			if (item.getUri().contains(".zip")) {
				boolean isUnzipSuccess = artifactoryComponent.unzip(file.toString(), saveLocalDir +"/"+artifactoryComponent.getArtifactName(item.getUri()));
				if (isUnzipSuccess)
					file.delete();
			}
		}
		return true;
	}
	
	public String getConsolidatedTaskIds(JSONArray tasks)  {
		List<String> taskIds = new ArrayList<>();
		for(int i=0;i<tasks.length();i++) {
			taskIds.add(tasks.getString(i));
		}
		String taskIdStr = String.join(",", taskIds);
		
		return taskIdStr;
	}
}
