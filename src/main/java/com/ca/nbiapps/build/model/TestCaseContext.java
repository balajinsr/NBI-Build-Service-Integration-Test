package com.ca.nbiapps.build.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * 
 * @author Balaji N
 *
 */
public class TestCaseContext {
	private Logger logger;
	private JSONObject testCaseData;

	// testcase results.
	private List<StepResults> stepResults = new ArrayList<>();
	private String testCaseName;
	private boolean testCaseSuccess;
	
	
	// build data.
	private Long buildNumber;
	private boolean gitTaskSuccess;
	private String baseGitCommitId;
	private String headGitCommitId;
	
	// consolidate data.
	private List<String> releaseIdList;
	
	public JSONObject getTestCaseData() {
		return testCaseData;
	}
	public void setTestCaseData(JSONObject testCaseData) {
		this.testCaseData = testCaseData;
	}
	public String getTestCaseName() {
		return testCaseName;
	}
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	public boolean isTestCaseSuccess() {
		return testCaseSuccess;
	}
	public void setTestCaseSuccess(boolean testCaseSuccess) {
		this.testCaseSuccess = testCaseSuccess;
	}
	
	public Logger getLogger() {
		return logger;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	public Long getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(Long buildNumber) {
		this.buildNumber = buildNumber;
	}
	public String getBaseGitCommitId() {
		return baseGitCommitId;
	}
	public void setBaseGitCommitId(String baseGitCommitId) {
		this.baseGitCommitId = baseGitCommitId;
	}
	public String getHeadGitCommitId() {
		return headGitCommitId;
	}
	public void setHeadGitCommitId(String headGitCommitId) {
		this.headGitCommitId = headGitCommitId;
	}
	public List<String> getReleaseIdList() {
		return releaseIdList;
	}
	public void setReleaseIdList(List<String> releaseIdList) {
		this.releaseIdList = releaseIdList;
	}
	public boolean isGitTaskSuccess() {
		return gitTaskSuccess;
	}
	public void setGitTaskSuccess(boolean gitTaskSuccess) {
		this.gitTaskSuccess = gitTaskSuccess;
	}
	public List<StepResults> getStepResults() {
		return stepResults;
	}
	public void setStepResults(List<StepResults> stepResults) {
		this.stepResults = stepResults;
	}
	
}
