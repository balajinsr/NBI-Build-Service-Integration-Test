package com.ca.nbiapps.core.compnents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ca.nbiapps.common.logger.AsynchLogger;
import com.ca.nbiapps.common.logger.LoggerPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class CommonComponent {

	@Autowired
	public PropertyComponents propertyComponents;

	public Path getPathByOSSpecific(String absoluteFilePath) {
		return Paths.get(Paths.get(absoluteFilePath).toString());
	}

	public CommonComponent() {
		String logFileLocation = null;
		try {
			logFileLocation = propertyComponents.getLogLocationPath();
			if (logFileLocation == null) {
				logFileLocation = "@CATALINA_HOME@/buildlogs";
			}
			String catalinaHome = System.getenv("CATALINA_HOME");
			if (catalinaHome != null && !"".equals(catalinaHome)) {
				logFileLocation = logFileLocation.replace("@CATALINA_HOME@", catalinaHome);
			} else {
				logFileLocation = logFileLocation.replace("@CATALINA_HOME@", "/opt/log");
			}
		} catch (Exception e) {
			if (logFileLocation == null) {
				logFileLocation = "/home/jenkin/logs";
			}
		}
	}

	private Level getLogLevel(String logLevel) {
		Level level = null;
		if (logLevel == null) {
			level = Level.INFO;
		} else {
			if (logLevel.equalsIgnoreCase("TRACE")) {
				level = Level.TRACE;
			} else if (logLevel.equalsIgnoreCase("DEBUG")) {
				level = Level.DEBUG;
			} else {
				level = Level.INFO;
			}
		}
		return level;
	}

	public Logger getLogger(String logFileName, String logLevel) throws Exception {
		Logger asynchLogger = null;
		Level level = getLogLevel(logLevel);
		try {
			if (LoggerPool.getHandleToLogger(logFileName) == null) {
				asynchLogger = (new AsynchLogger(getLoggerPath(logFileName), level)).getLogger(logFileName);
				LoggerPool.addLogger(logFileName, asynchLogger);
			}
			return LoggerPool.getHandleToLogger(logFileName);
		} catch (Exception e) {
			throw e;
		}
	}

	public String getLoggerPath(String siloName) throws Exception {
		String logFileLocation = propertyComponents.getLogLocationPath();
		if (logFileLocation == null) {
			logFileLocation = "@CATALINA_HOME@/buildlogs";
		}
		String catalinaHome = System.getenv("CATALINA_HOME");
		if (catalinaHome != null && !"".equals(catalinaHome)) {
			logFileLocation = logFileLocation.replace("@CATALINA_HOME@", catalinaHome);
		} else {
			logFileLocation = logFileLocation.replace("@CATALINA_HOME@", "/opt/arcot");
		}

		if (!"".equalsIgnoreCase(siloName)) {
			logFileLocation = logFileLocation + "/" + siloName;
		}
		return logFileLocation;
	}

	public String toJsonFromObject(Object object, Type returnTypeOfObject) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(object, returnTypeOfObject);
	}

	public String getMD5Sum(File file) throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			return DigestUtils.md5Hex(IOUtils.toByteArray(fileInputStream));
		}
	}
}
