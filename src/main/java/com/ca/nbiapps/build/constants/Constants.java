package com.ca.nbiapps.build.constants;

/**
 * 
 * @author Balaji N
 *
 */
public enum Constants {
	BUILD_SERVICE_CLIENT_LOGGER_PROPS("build-service-client-logger.properties");

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
