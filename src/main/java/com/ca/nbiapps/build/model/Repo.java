package com.ca.nbiapps.build.model;

public class Repo {
	private String html_url;
	private String name;
	private String ssh_url;
	private boolean fork;

	public boolean isFork() {
		return fork;
	}

	public void setFork(boolean fork) {
		this.fork = fork;
	}

	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSsh_url() {
		return ssh_url;
	}

	public void setSsh_url(String ssh_url) {
		this.ssh_url = ssh_url;
	}
}
