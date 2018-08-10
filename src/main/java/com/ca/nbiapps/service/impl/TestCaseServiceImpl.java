package com.ca.nbiapps.service.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.nbiapps.core.compnents.BuildClientComponent;
import com.ca.nbiapps.core.compnents.ConsolidationComponent;
import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.core.compnents.TestCaseContext;
import com.ca.nbiapps.service.TestCaseService;
/**
 * 
 * @author Balaji N
 *
 */
@Service
public class TestCaseServiceImpl implements TestCaseService {

	@Autowired
	GitComponent gitComponent;

	@Autowired
	BuildClientComponent buildClientComponent;
	
	@Autowired
	ConsolidationComponent consolidationComponent;
	
	
	@Override
	public void process(TestCaseContext testCaseContext) throws Exception {
		JSONObject testCaseData = testCaseContext.getTestCaseData();
		JSONArray developerTasks = testCaseData.getJSONArray("developerTasks");
		String latestGitCommitSHAId = gitComponent.getLastestCommitId();
		
		// gitTaskCheck, buildProcessCheck, buildStatusCheck, buildconsolidationProcessCheck, buildConsolidationCheck 
		for (int i = 0; i < developerTasks.length(); i++) {
			JSONObject buildObj = developerTasks.getJSONObject(i);
			String taskId = buildObj.getString("taskId");
			JSONArray buildTasks = buildObj.getJSONArray("builds");
			gitComponent.gitClean();
			for (int j = 0; j < buildTasks.length(); j++) {
				JSONObject buildTask = buildTasks.getJSONObject(i);
				gitComponent.processDeveloperGitTask(testCaseContext, taskId, buildTask);
				buildClientComponent.pullRequest(testCaseContext, taskId, latestGitCommitSHAId);
				buildClientComponent.doBuildAssert(testCaseContext, taskId, buildTask);
				buildClientComponent.doBuildsConsolidation(testCaseContext, taskId);
			}
		}

		// consolidationProcessCheck, consolidationPackageCheck, consolidationDBCheck, consolidationManifestCheck
		String doConsolidateCheck = testCaseData.getString("doConsolidateCheck");
		if ("true".equals(doConsolidateCheck)) {
			JSONArray consolidationList = testCaseData.getJSONArray("consolidationList");
			for (int i = 0; i < consolidationList.length(); i++) {
				JSONObject consolidation = consolidationList.getJSONObject(i);
				JSONArray tasks = consolidation.getJSONArray("taskIds");
	
				String isAtLeastOneBuildSuccess = consolidation.getString("isAtLeastOneBuildSuccess");
				if ("true".equals(isAtLeastOneBuildSuccess)) {
					consolidationComponent.doConsolidationPackage(testCaseContext, tasks);
					JSONArray expectedFilesInPackage = consolidation.getJSONArray("expectedFilesInPackage");
					consolidationComponent.verifyConsolidationPackage(testCaseContext, expectedFilesInPackage);
				}
			}
		}
	}
}

