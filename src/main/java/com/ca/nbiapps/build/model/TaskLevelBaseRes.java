package com.ca.nbiapps.build.model;

public class TaskLevelBaseRes extends BaseResponse {
	private String releaseId;
	private String releaseLocation;
	public String getReleaseId() {
		return releaseId;
	}
	public void setReleaseId(String releaseId) {
		this.releaseId = releaseId;
	}
	public String getReleaseLocation() {
		return releaseLocation;
	}
	public void setReleaseLocation(String releaseLocation) {
		this.releaseLocation = releaseLocation;
	}
}
