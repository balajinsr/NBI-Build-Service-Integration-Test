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
	public String siloName;

	@Value("${integration.test.git_username}")
	public String gitUserName;

	@Value("${integration.test.local_fork_repo_dir}")
	public String localForkReopDir;

	@Value("${integration.test.git_fork_ssh_url}")
	public String gitForkSshUrl;

	@Value("${integration.test.git_upstream_ssh_url}")
	public String gitUpstreamSshUrl;

	@Value("${integration.test.reset_git_commit_id}")
	public String gitCommitSshId;
	// git properties - end

	// logger properties - start
	@Value("${integration.test.log_location_path}")
	public String logLocationPath;
	// logger properties - end

	// build service data and urls - start
	@Value("${integration.test.test_data_base_path}")
	public String testDataBasePath;

	@Value("${integration.test.pull_request_url}")
	public String pullRequestServiceUrl;
	// build service data and urls - end

	// artifactory properties - start
	@Value("${integration.test.maven_repo_name}")
	public String mavenRepoName;

	@Value("${integration.test.artifactory_base_url}")
	public String artifactoryBaseUrl;

	@Value("${integration.test.artifactory_apistore_base_url}")
	public String artifactoryApiStoreBaseUrl;

	@Value("${integration.test.artifacts_downloaded_local_dir}")
	public String artifactoryDownloadLocalDir;
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

	public String getGitCommitSshId() {
		return gitCommitSshId;
	}

	public void setGitCommitSshId(String gitCommitSshId) {
		this.gitCommitSshId = gitCommitSshId;
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

	public String getPullRequestServiceUrl() {
		return pullRequestServiceUrl;
	}

	public void setPullRequestServiceUrl(String pullRequestServiceUrl) {
		this.pullRequestServiceUrl = pullRequestServiceUrl;
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

}
