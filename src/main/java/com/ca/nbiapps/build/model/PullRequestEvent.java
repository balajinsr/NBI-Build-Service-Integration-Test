package com.ca.nbiapps.build.model;

public class PullRequestEvent {
	private String action;
	private PullRequest pull_request;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public PullRequest getPull_request() {
		return pull_request;
	}

	public void setPull_request(PullRequest pull_request) {
		this.pull_request = pull_request;
	}
}
