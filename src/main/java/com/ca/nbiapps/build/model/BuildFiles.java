package com.ca.nbiapps.build.model;

/**
 * @author Balaji N
 *
 */
public class BuildFiles {
	String filePath;
	String md5Value;
	String action;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMd5Value() {
		return md5Value;
	}

	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "BuildFiles [filePath=" + filePath + ", md5Value=" + md5Value + ", action=" + action + "]";
	}
}
