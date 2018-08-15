package com.ca.nbiapps.core.compnents;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
public class ConsolidationComponent extends ArtifactoryComponent {

	@Autowired
	public PropertyComponents propertyComponents;

	@Autowired
	RestServiceClient restServiceClient;

	@Autowired
	CommonComponent commonComponent;

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
		String saveLocalDir = commonComponent.getPathByOSSpecific(propertyComponents.getArtifactoryDownloadLocalDir()).toString()+File.separator+testCaseContext.getTestCaseName();
		saveLocalDir = saveLocalDir+"/"+releaseId;
		String artifactsUri = "/" + propertyComponents.getSiloName() + "/" + cycleName.toLowerCase() + "-release_" + releaseId;
		boolean isSuccessDownload = downloadPackage(logger, cycleName, saveLocalDir, artifactsUri);
		if (isSuccessDownload) {
			commonComponent.assertPackageFiles(testCaseContext, saveLocalDir, expectedFilesInPackage);
		} else {
			testCaseContext.setTestCaseSuccess(false);
			testCaseContext.setTestCaseFailureReason("consolidated package download failed.");
		}
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
