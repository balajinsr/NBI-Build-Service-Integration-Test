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
	private String testCaseName;
	private JSONObject testCaseData;
	private boolean testCaseSuccess;

	// testcase results.
	private List<StepResults> stepResults = new ArrayList<>();
	
	// git data
	private String baseGitCommitId;
	private String headGitCommitId;
	
	// build data.
	private List<BuildData> buildData = new ArrayList<>();
	
	// consolidate data.
	private List<ReleaseData> releaseDataList = new ArrayList<>();

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public JSONObject getTestCaseData() {
		return testCaseData;
	}

	public void setTestCaseData(JSONObject testCaseData) {
		this.testCaseData = testCaseData;
	}

	public boolean isTestCaseSuccess() {
		return testCaseSuccess;
	}

	public void setTestCaseSuccess(boolean testCaseSuccess) {
		this.testCaseSuccess = testCaseSuccess;
	}

	public List<StepResults> getStepResults() {
		return stepResults;
	}

	public void setStepResults(List<StepResults> stepResults) {
		this.stepResults = stepResults;
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

	public List<BuildData> getBuildData() {
		return buildData;
	}

	public void setBuildData(List<BuildData> buildData) {
		this.buildData = buildData;
	}

	public List<ReleaseData> getReleaseDataList() {
		return releaseDataList;
	}

	public void setReleaseDataList(List<ReleaseData> releaseDataList) {
		this.releaseDataList = releaseDataList;
	}
}
