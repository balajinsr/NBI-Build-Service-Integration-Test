
package com.ca.nbiapps.build.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Balaji N
 *
 */
@JsonAutoDetect
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BuildData {
	public String siloName;
	public String taskId;
	
	public Long buildNumber;
	public String buildStatus;
	public String artifactUploadStatus;
	public String buildFailedReason;
	public boolean artifactsAvailable;
	public List<BuildFiles> buildFilesInPackage = new ArrayList<>();
	public List<BuildFiles> buildFIlesInDB = new ArrayList<>();
	
	public String getSiloName() {
		return siloName;
	}
	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public Long getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(Long buildNumber) {
		this.buildNumber = buildNumber;
	}
	public String getBuildStatus() {
		return buildStatus;
	}
	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}
	public String getArtifactUploadStatus() {
		return artifactUploadStatus;
	}
	public void setArtifactUploadStatus(String artifactUploadStatus) {
		this.artifactUploadStatus = artifactUploadStatus;
	}
	public String getBuildFailedReason() {
		return buildFailedReason;
	}
	public void setBuildFailedReason(String buildFailedReason) {
		this.buildFailedReason = buildFailedReason;
	}
	public boolean isArtifactsAvailable() {
		return artifactsAvailable;
	}
	public void setArtifactsAvailable(boolean artifactsAvailable) {
		this.artifactsAvailable = artifactsAvailable;
	}
	public List<BuildFiles> getBuildFilesInPackage() {
		return buildFilesInPackage;
	}
	public void setBuildFilesInPackage(List<BuildFiles> buildFilesInPackage) {
		this.buildFilesInPackage = buildFilesInPackage;
	}
	public List<BuildFiles> getBuildFIlesInDB() {
		return buildFIlesInDB;
	}
	public void setBuildFIlesInDB(List<BuildFiles> buildFIlesInDB) {
		this.buildFIlesInDB = buildFIlesInDB;
	}
	@Override
	public String toString() {
		return "BuildData [siloName=" + siloName + ", taskId=" + taskId + ", buildNumber=" + buildNumber + ", buildStatus=" + buildStatus + ", artifactUploadStatus="
				+ artifactUploadStatus + ", buildFailedReason=" + buildFailedReason + ", artifactsAvailable=" + artifactsAvailable + ", buildFilesInPackage=" + buildFilesInPackage
				+ ", buildFIlesInDB=" + buildFIlesInDB + "]";
	}
	
	
}
