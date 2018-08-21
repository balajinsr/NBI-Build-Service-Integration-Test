package com.ca.nbiapps.build.model;

/**
 * @author Balaji N
 *
 */
public enum BuildTestStats {
	BUILD_ADJUST_TASKID_STATUS(), 
	BUILD_GIT_TASK(), 
	BUILD_PULL_REQUEST(), 
	BUILD_RESULT_FETCH(), 
	BUILD_STATUS_CHECK(), 
	BUILD_PACKAGE_DOWNLOAD(), 
	BUILD_PACKAGE_ASSERT(),
	BUILD_DBENTRIES_ASSERT(), 
	CON_PACKAGE_TASKIDS_STATUS(), 
	CON_PACKAGE(), 
	CON_PACKAGE_DOWNLOAD(),
	CON_PACKAGE_ASSERT(), 
	CON_MANIFEST_ASSERT();

	private String stepName;

	private BuildTestStats() {
		this.setStepName(this.name());
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
}
