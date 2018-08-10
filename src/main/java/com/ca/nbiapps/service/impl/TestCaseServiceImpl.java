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
	
	@Autowired
	SalesForceComponent salesForceComponent;
	
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
			
			salesForceComponent.adjustTaskIdStatusForAcceptTheBuild(testCaseContext);
			for (int j = 0; j < buildTasks.length(); j++) {
				JSONObject buildTask = buildTasks.getJSONObject(i);
				gitComponent.processDeveloperGitTask(testCaseContext, taskId, buildTask);
				buildClientComponent.pullRequest(testCaseContext, taskId, latestGitCommitSHAId);
				buildClientComponent.waitForBuildComplete(testCaseContext);
				buildClientComponent.doBuildAssert(testCaseContext, taskId, buildTask);
			}
		}

		// consolidationProcessCheck, consolidationPackageCheck, consolidationDBCheck, consolidationManifestCheck
		boolean doConsolidateCheck = testCaseData.getBoolean("doConsolidationCheck");
		if (doConsolidateCheck) {
			JSONArray consolidationList = testCaseData.getJSONArray("consolidationAssertList");
			for (int i = 0; i < consolidationList.length(); i++) {
				JSONObject consolidation = consolidationList.getJSONObject(i);
				JSONArray tasks = consolidation.getJSONArray("taskIds");
	
				boolean atLeastOneBuildContainsPackage = consolidation.getBoolean("atLeastOneBuildContainsPackage");
				if (atLeastOneBuildContainsPackage) {
					boolean doTaskStatusChangeAuto = consolidation.getBoolean("doTaskStatusChangeAuto");
					if(doTaskStatusChangeAuto) {
						salesForceComponent.adjustTaskIdStatusToDoConsolidationPackage(testCaseContext, tasks);
					}
					consolidationComponent.doConsolidationPackage(testCaseContext, tasks);
					JSONArray expectedFilesInPackage = consolidation.getJSONArray("expectedFilesInPackage");
					consolidationComponent.verifyConsolidationPackage(testCaseContext, expectedFilesInPackage);
				}
			}
		} else {
			
		}
		
		
		
	}
}

