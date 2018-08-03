package com.ca.nbiapps.build.constants;

/**
 * 
 * @author Balaji N
 *
 */
public enum Constants {
	ASYN_LOGGER_PROP("asyn-logger.properties");

	private String fileName;

	private Constants(String fileName) {
		this.setFileName(fileName);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
