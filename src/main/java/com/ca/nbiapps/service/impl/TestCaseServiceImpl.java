package com.ca.nbiapps.service.impl;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.nbiapps.build.model.BuildData;
import com.ca.nbiapps.build.model.BuildTestStats;
import com.ca.nbiapps.build.model.TestCaseContext;
import com.ca.nbiapps.core.compnents.BuildClientComponent;
import com.ca.nbiapps.core.compnents.ConsolidationComponent;
import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.core.compnents.PropertyComponents;
import com.ca.nbiapps.core.compnents.SalesForceComponent;
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
		Logger logger = testCaseContext.getLogger();
		JSONObject testCaseData = testCaseContext.getTestCaseData();
		JSONArray developerBuildTasks = testCaseData.getJSONArray("developer-build-Tasks");
		String baseGitCommitSHAId = gitComponent.getLastestCommitId();
		testCaseContext.setBaseGitCommitId(baseGitCommitSHAId);
		testCaseContext.setHeadGitCommitId(baseGitCommitSHAId);
		
		// gitTaskCheck, buildProcessCheck, buildStatusCheck,
		// buildconsolidationProcessCheck, buildConsolidationCheck
		for (int i = 0; i < developerBuildTasks.length(); i++) {
			JSONObject buildTask = developerBuildTasks.getJSONObject(i);
			String taskId = buildTask.getString("taskId");
			
			salesForceComponent.adjustTaskIdStatusForAcceptTheBuild(testCaseContext, taskId);
			if(!testCaseContext.isTestCaseSuccess()) {
				return;
			}
			gitComponent.processDeveloperGitTask(testCaseContext, taskId, buildTask);
			if(!testCaseContext.isTestCaseSuccess()) {
				return;
			}
			String headGitCommitSHAId = gitComponent.getLastestCommitId();
			testCaseContext.setHeadGitCommitId(headGitCommitSHAId);
			Long previousBuildNumber = buildClientComponent.getPreviousBuildNumber(testCaseContext.getLogger());			
			buildClientComponent.pullRequest(testCaseContext, taskId, i);
			if(!testCaseContext.isTestCaseSuccess()) {
				return;
			}
			BuildData buildData = buildClientComponent.waitForBuildCompleteAndGetBuildResults(testCaseContext, taskId, previousBuildNumber, i);
			testCaseContext.getBuildData().add(buildData);
			logger.info("BuildResults: :: "+buildData.toString());
			
			if(!testCaseContext.isTestCaseSuccess()) {
				return;
			} 
			
			buildClientComponent.doBuildAssert(testCaseContext, taskId, buildData, buildTask);
			if(!testCaseContext.isTestCaseSuccess()) {
				return;
			}
			
			if(!buildData.isArtifactsAvailable() && "Build Failed".equals(buildData.getBuildStatus()) && buildData.getBuildFailedReason() != null && !"".equals(buildData.getBuildFailedReason())) {
				logger.info("Build Assert true - Expected Build failed reason: "+buildData.getBuildFailedReason());
				return;
			}
			
		}

		if(!testCaseContext.isTestCaseSuccess()) {
			return;
		}
		
		// consolidationProcessCheck, consolidationPackageCheck,
		// consolidationDBCheck, consolidationManifestCheck
		boolean doConsolidateCheck = testCaseData.getBoolean("doConsolidationCheck");
		if (doConsolidateCheck) {
			JSONArray consolidationList = testCaseData.getJSONArray("consolidationAssertList");
			for (int i = 0; i < consolidationList.length(); i++) {
				JSONObject consolidation = consolidationList.getJSONObject(i);
				JSONArray tasks = consolidation.getJSONArray("taskIds");

				boolean isArtifactsAvailable = consolidation.getBoolean("isArtifactsAvailable");
				boolean isOnlyDeleteInstructionsAvailable = consolidation.getBoolean("isOnlyDeleteInstructionsAvailable");
				if (isArtifactsAvailable || isOnlyDeleteInstructionsAvailable) {
					boolean doTaskStatusChangeAuto = consolidation.getBoolean("doTaskStatusChangeAuto");
					String taskIds = consolidationComponent.getConsolidatedTaskIds(tasks);
					if (doTaskStatusChangeAuto) {						
						salesForceComponent.adjustTaskIdStatusToDoConsolidationPackage(testCaseContext, taskIds, "Preview");
						if(!testCaseContext.isTestCaseSuccess()) {
							return;
						}
					}
					String releaseId = consolidationComponent.doConsolidationPackage(testCaseContext, "Preview", taskIds);
					if(!testCaseContext.isTestCaseSuccess()) {
						return;
					}
					
					if(isArtifactsAvailable) {
						JSONArray expectedFilesInPackage = consolidation.getJSONArray("expectedFilesInPackage");
						consolidationComponent.verifyConsolidationPackage(testCaseContext, "Preview", releaseId, expectedFilesInPackage);
					}
					
					if(isOnlyDeleteInstructionsAvailable) {
						testCaseContext.setTestCaseSuccess(true);
						if(!isArtifactsAvailable) {
							gitComponent.setSkippedStepResults(testCaseContext, "Preview", BuildTestStats.CON_PACKAGE_DOWNLOAD.name(), "No package to download.");
							gitComponent.setSkippedStepResults(testCaseContext, "Preview", BuildTestStats.CON_PACKAGE_ASSERT.name(), "No Consolidated package available.");
						}
						
						if(isOnlyDeleteInstructionsAvailable) {
							//TODO: manifest assert.
						}	
					} 
					if(!testCaseContext.isTestCaseSuccess()) {
						return;
					}	
				}
			}
		} else {
			gitComponent.setSkippedStepResults(testCaseContext, "Preview", BuildTestStats.CON_PACKAGE.name(), "Not required to do consolidated package..");
			gitComponent.setSkippedStepResults(testCaseContext, "Preview", BuildTestStats.CON_PACKAGE_DOWNLOAD.name(), "No package to download.");
			gitComponent.setSkippedStepResults(testCaseContext, "Preview", BuildTestStats.CON_PACKAGE_ASSERT.name(), "No consolidated package available to do assert.");
		}
	}

	@Override
	public void reset(TestCaseContext testCaseContext) throws Exception {
		JSONObject jsonTemplateObject = testCaseContext.getTestCaseData();
		Logger logger = testCaseContext.getLogger();
		boolean rebaseOrigin = (boolean) jsonTemplateObject.get("rebase-origin");
		String baseGitCommitId = testCaseContext.getBaseGitCommitId();
		String headGitCommitId = testCaseContext.getHeadGitCommitId();
		logger.info("Rebase the git state from shaId-"+headGitCommitId+", to shaId - "+baseGitCommitId);
		if(!baseGitCommitId.equals(headGitCommitId)) {
			gitComponent.gitResetHard(logger, baseGitCommitId);
			if (rebaseOrigin) {
				gitComponent.gitPush(logger, true, "origin");
				logger.info("Sync the origin to SHAID:  "+baseGitCommitId);
			}
			boolean resetDB = (boolean) jsonTemplateObject.get("resetDB");
			if (resetDB) {
				//buildClientComponent.resetBuildDBEntries(testCaseContext);
			}
			boolean rebaseUpstream = (boolean) jsonTemplateObject.get("rebase-upstream");
			if (rebaseUpstream) {
				gitComponent.gitPush(logger, true, "upstream");
				logger.info("Sync the upstream to SHAID:  "+baseGitCommitId);
			}
		}
	}
}
