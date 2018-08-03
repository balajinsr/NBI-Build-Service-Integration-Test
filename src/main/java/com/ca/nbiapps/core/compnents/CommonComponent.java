package com.ca.nbiapps.core.compnents;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.ca.nbiapps.build.constants.Constants;
import com.ca.nbiapps.build.util.PropertyUtils;
import com.ca.nbiapps.common.logger.AsynchLogger;
import com.ca.nbiapps.common.logger.LoggerPool;

/**
 * @author Balaji N
 */
@Component
public class CommonComponent {
	public String getProperty(String key) throws Exception {
		String propertyFile = "test.properties";
		try {
			return PropertyUtils.getValueFromProperties(key, propertyFile);
		} catch (Exception e) {
			Logger logger = getDefaultLogger();
			logger.error("Property - [" + key + "] read failed from file - [" + propertyFile + "]");
			return null;
		}
	}

	private Logger getDefaultLogger() throws Exception {
		return getLogger("Default-TestNG", "INFO");
	}

	public CommonComponent() {
		String logFileLocation = null;
		try {
			logFileLocation = PropertyUtils.getValueFromProperties("LOG_LOCATION_PATH", Constants.ASYN_LOGGER_PROP.getFileName());
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

	public String getLoggerPath(String siloName) throws IOException {
		String logFileLocation = null;
		try {
			logFileLocation = PropertyUtils.getValueFromProperties("LOG_LOCATION_PATH", Constants.ASYN_LOGGER_PROP.getFileName());
			if (logFileLocation == null) {
				logFileLocation = "@CATALINA_HOME@/buildlogs";
			}
			String catalinaHome = System.getenv("CATALINA_HOME");
			if (catalinaHome != null && !"".equals(catalinaHome)) {
				logFileLocation = logFileLocation.replace("@CATALINA_HOME@", catalinaHome);
			} else {
				logFileLocation = logFileLocation.replace("@CATALINA_HOME@", "/opt/arcot");
			}
		} catch (IOException e) {
			throw e;
		}
		if (!"".equalsIgnoreCase(siloName)) {
			logFileLocation = logFileLocation + "/" + siloName;
		}
		return logFileLocation;
	}
}
