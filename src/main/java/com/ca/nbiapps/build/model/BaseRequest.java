package com.ca.nbiapps.build.model;

public class BaseRequest {	
	private String siloName;
	private String cycleName;
	private String buildNumber;

	public String getSiloName() {
		return siloName;
	}
	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}
	public String getCycleName() {
		return cycleName;
	}
	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}
	
	@Override
	public String toString() {
		return "BaseRequest [siloName=" + siloName + ", cycleName=" + cycleName + "]";
	}
	public String getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
}
