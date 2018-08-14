package com.ca.nbiapps.service.impl;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.nbiapps.build.model.BuildData;
import com.ca.nbiapps.build.model.TestCaseContext;
import com.ca.nbiapps.core.compnents.BuildClientComponent;
import com.ca.nbiapps.core.compnents.ConsolidationComponent;
import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.core.compnents.PropertyComponents;
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

	@Autowired
	PropertyComponents propertyComponents;

	@Override
	public void process(TestCaseContext testCaseContext) throws Exception {
		JSONObject testCaseData = testCaseContext.getTestCaseData();
		JSONArray developerTasks = testCaseData.getJSONArray("developerTasks");
		String baseGitCommitSHAId = gitComponent.getLastestCommitId();
		testCaseContext.setBaseGitCommitId(baseGitCommitSHAId);
		Long beforeTestCaseBuildNumber = buildClientComponent.getPreviousBuildNumber(testCaseContext.getLogger());
		testCaseContext.setBuildNumber(beforeTestCaseBuildNumber);
		
		
		// gitTaskCheck, buildProcessCheck, buildStatusCheck,
		// buildconsolidationProcessCheck, buildConsolidationCheck
		for (int i = 0; i < developerTasks.length(); i++) {
			JSONObject buildObj = developerTasks.getJSONObject(i);
			String taskId = buildObj.getString("taskId");
			JSONArray buildTasks = buildObj.getJSONArray("builds");

			salesForceComponent.adjustTaskIdStatusForAcceptTheBuild(testCaseContext, taskId);
			if(!testCaseContext.isTestCaseSuccess()) {
				return;
			}
			for (int j = 0; j < buildTasks.length(); j++) {
				JSONObject buildTask = buildTasks.getJSONObject(i);
				gitComponent.processDeveloperGitTask(testCaseContext, taskId, buildTask);
				if(!testCaseContext.isTestCaseSuccess()) {
					return;
				}
				String headGitCommitSHAId = gitComponent.getLastestCommitId();
				testCaseContext.setHeadGitCommitId(headGitCommitSHAId);
				Long previousBuildNumber = buildClientComponent.getPreviousBuildNumber(testCaseContext.getLogger());			
				buildClientComponent.pullRequest(testCaseContext, taskId);
				if(!testCaseContext.isTestCaseSuccess()) {
					return;
				}
				BuildData buildData = buildClientComponent.waitForBuildCompleteAndGetBuildResults(testCaseContext, taskId, previousBuildNumber);
				if(!testCaseContext.isTestCaseSuccess()) {
					return;
				}
				
				buildClientComponent.doBuildAssert(testCaseContext, taskId, buildData, buildTask);
				if(!testCaseContext.isTestCaseSuccess()) {
					return;
				}
			}
		}

		// consolidationProcessCheck, consolidationPackageCheck,
		// consolidationDBCheck, consolidationManifestCheck
		boolean doConsolidateCheck = testCaseData.getBoolean("doConsolidationCheck");
		if (doConsolidateCheck) {
			JSONArray consolidationList = testCaseData.getJSONArray("consolidationAssertList");
			for (int i = 0; i < consolidationList.length(); i++) {
				JSONObject consolidation = consolidationList.getJSONObject(i);
				JSONArray tasks = consolidation.getJSONArray("taskIds");

				boolean atLeastOneBuildContainsPackage = consolidation.getBoolean("atLeastOneBuildContainsPackage");
				if (atLeastOneBuildContainsPackage) {
					boolean doTaskStatusChangeAuto = consolidation.getBoolean("doTaskStatusChangeAuto");
					if (doTaskStatusChangeAuto) {
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

	@Override
	public void reset(TestCaseContext testCaseContext) throws Exception {
		JSONObject jsonTemplateObject = testCaseContext.getTestCaseData();
		Logger logger = testCaseContext.getLogger();
		boolean rebaseOrigin = (boolean) jsonTemplateObject.get("rebase-origin");
		String resetCommitId = propertyComponents.getGitResetCommitSshId();
		gitComponent.gitResetHard(logger, resetCommitId);
		if (rebaseOrigin) {
			gitComponent.gitPush(logger, true, "origin");
		}
		boolean resetDB = (boolean) jsonTemplateObject.get("resetDB");
		if (resetDB) {
			//buildClientComponent.resetDB(testCaseContext);
		}
		boolean rebaseUpstream = (boolean) jsonTemplateObject.get("rebase-upstream");
		if (rebaseUpstream) {
			gitComponent.gitPush(logger, true, "upstream");
		}
	}
}
