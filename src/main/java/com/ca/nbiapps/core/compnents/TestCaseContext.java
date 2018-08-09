package com.ca.nbiapps.core.compnents;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
/**
 * 
 * @author Balaji N
 *
 */
@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "prototype")
public class TestCaseContext {
	private Logger logger;
	private JSONObject testCaseData;
	private String testCaseName;
	private boolean testCaseSuccess;
	private String testCaseFailureReason;
	
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
	public String getTestCaseFailureReason() {
		return testCaseFailureReason;
	}
	public void setTestCaseFailureReason(String testCaseFailureReason) {
		this.testCaseFailureReason = testCaseFailureReason;
	}
	public Logger getLogger() {
		return logger;
	}
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
