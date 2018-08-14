package com.ca.nbiapps.build.model;

public class TaskLevelBaseReq {
	private String siloName;
	private String cycleName;
	private String taskIds;
	private String clientIp;
	private String deviceInfo;
	private String releaseId;
	private String regReleaseId;
	private int deploymentAction;
	private String otherInstructions;
	
	public String getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}
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
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	
	public String toString() {
		return "clientIp: " + clientIp + ", Silo Name: " + siloName + ", Cycle Name: " + cycleName + ", taskIds: " + taskIds + ", Device Info: " + deviceInfo+", Release Number: " + releaseId + ", otherInstructions: " + otherInstructions;
		
	}
	public String getRegReleaseId() {
		return regReleaseId;
	}
	public void setRegReleaseId(String regReleaseId) {
		this.regReleaseId = regReleaseId;
	}
	public String getTaskIds() {
		return taskIds;
	}
	public void setTaskIds(String taskIds) {
		this.taskIds = taskIds;
	}
	
	public int getDeploymentAction() {
		return deploymentAction;
	}
	public void setDeploymentAction(int deploymentAction) {
		this.deploymentAction = deploymentAction;
	}
	public String getOtherInstructions() {
		return otherInstructions;
	}
	public void setOtherInstructions(String otherInstructions) {
		this.otherInstructions = otherInstructions;
	}
}
