package com.ca.nbiapps.core.compnents;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Balaji N
 *
 */
@Component
public class PropertyComponents {

	// git properties - start
	@Value("${integration.test.silo_name}")
	private String siloName;

	@Value("${integration.test.git_username}")
	private String gitUserName;

	@Value("${integration.test.local_fork_repo_dir}")
	private String localForkReopDir;

	@Value("${integration.test.git_fork_ssh_url}")
	private String gitForkSshUrl;

	@Value("${integration.test.git_upstream_ssh_url}")
	private String gitUpstreamSshUrl;
	// git properties - end

	// logger properties - start
	@Value("${integration.test.log_location_path}")
	private String logLocationPath;
	// logger properties - end

	// build service data and urls - start
	@Value("${integration.test.test_data_base_path}")
	private String testDataBasePath;

	@Value("${integration.test.build_service_base_url}")
	private String buildServiceBaseUrl;
	// build service data and urls - end

	// artifactory properties - start
	@Value("${integration.test.maven_repo_name}")
	private String mavenRepoName;

	@Value("${integration.test.artifactory_base_url}")
	private String artifactoryBaseUrl;

	@Value("${integration.test.artifactory_apistore_base_url}")
	private String artifactoryApiStoreBaseUrl;

	@Value("${integration.test.artifacts_downloaded_local_dir}")
	private String artifactoryDownloadLocalDir;
	
	@Value("${integration.test.artifactory_username}")
	private String artifactoryUsername;
	
	@Value("${integration.test.artifactory_password}")
	private String artifactoryPassword;
	
	
	@Value("${integration.test.externalService.connectiontimeout}")
	public String connectionTimeOut;
	
	@Value("${integration.test.externalService.readtimeout}")
	public String readtimeout;
	// artifactory properties - end

	public String getSiloName() {
		return siloName;
	}

	public void setSiloName(String siloName) {
		this.siloName = siloName;
	}

	public String getGitUserName() {
		return gitUserName;
	}

	public void setGitUserName(String gitUserName) {
		this.gitUserName = gitUserName;
	}

	public String getLocalForkReopDir() {
		return localForkReopDir;
	}

	public void setLocalForkReopDir(String localForkReopDir) {
		this.localForkReopDir = localForkReopDir;
	}

	public String getGitForkSshUrl() {
		return gitForkSshUrl;
	}

	public void setGitForkSshUrl(String gitForkSshUrl) {
		this.gitForkSshUrl = gitForkSshUrl;
	}

	public String getGitUpstreamSshUrl() {
		return gitUpstreamSshUrl;
	}

	public void setGitUpstreamSshUrl(String gitUpstreamSshUrl) {
		this.gitUpstreamSshUrl = gitUpstreamSshUrl;
	}

	public String getLogLocationPath() {
		return logLocationPath;
	}

	public void setLogLocationPath(String logLocationPath) {
		this.logLocationPath = logLocationPath;
	}

	public String getTestDataBasePath() {
		return testDataBasePath;
	}

	public void setTestDataBasePath(String testDataBasePath) {
		this.testDataBasePath = testDataBasePath;
	}

	

	public String getBuildServiceBaseUrl() {
		return buildServiceBaseUrl;
	}

	public void setBuildServiceBaseUrl(String buildServiceBaseUrl) {
		this.buildServiceBaseUrl = buildServiceBaseUrl;
	}

	public String getMavenRepoName() {
		return mavenRepoName;
	}

	public void setMavenRepoName(String mavenRepoName) {
		this.mavenRepoName = mavenRepoName;
	}

	public String getArtifactoryBaseUrl() {
		return artifactoryBaseUrl;
	}

	public void setArtifactoryBaseUrl(String artifactoryBaseUrl) {
		this.artifactoryBaseUrl = artifactoryBaseUrl;
	}

	public String getArtifactoryApiStoreBaseUrl() {
		return artifactoryApiStoreBaseUrl;
	}

	public void setArtifactoryApiStoreBaseUrl(String artifactoryApiStoreBaseUrl) {
		this.artifactoryApiStoreBaseUrl = artifactoryApiStoreBaseUrl;
	}

	public String getArtifactoryDownloadLocalDir() {
		return artifactoryDownloadLocalDir;
	}

	public void setArtifactoryDownloadLocalDir(String artifactoryDownloadLocalDir) {
		this.artifactoryDownloadLocalDir = artifactoryDownloadLocalDir;
	}

	public String getArtifactoryUsername() {
		return artifactoryUsername;
	}

	public void setArtifactoryUsername(String artifactoryUsername) {
		this.artifactoryUsername = artifactoryUsername;
	}

	public String getArtifactoryPassword() {
		return artifactoryPassword;
	}

	public void setArtifactoryPassword(String artifactoryPassword) {
		this.artifactoryPassword = artifactoryPassword;
	}

	public String getConnectionTimeOut() {
		return connectionTimeOut;
	}

	public void setConnectionTimeOut(String connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	public String getReadtimeout() {
		return readtimeout;
	}

	public void setReadtimeout(String readtimeout) {
		this.readtimeout = readtimeout;
	}
}
