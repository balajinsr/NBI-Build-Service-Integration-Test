package com.ca.nbiapps.common.logger;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * This method deals with the life cycle of various Loggers
 * 1. Add Loggers to a Collection
 * 2. Get a Logger object From the Collection by passing log Name.
 * 3. Remove a Logger from the Collection
 * 
 */
public class LoggerPool{
	
	private static ConcurrentHashMap<String, Logger> LOGGER_MAP = 
			new ConcurrentHashMap<String, Logger>();
	/**
	 * Default constructor
	 */
	public LoggerPool(){}
	
	/**
	 * addLogger(String,Logger) : It adds a new logger in the logger Poll if already exists then replaces with new one.
	 * @param logName : The connection name for which logger will be added in the pool.
	 * @param asynchLogger : Logger to be added in the pool
	 */
	public static void addLogger(String logName, Logger asynchLogger){
		if (LOGGER_MAP.containsKey(logName)){
			LOGGER_MAP.replace(logName, asynchLogger);
		}else{
			LOGGER_MAP.put(logName, asynchLogger);
		}
	}
	
	/**
	 * removeLogger(String) : Removes logger from the poll for a particular uniqueName name.
	 * @param uniqueName : The unique name against logger will be removed from the pool.
	 */
	public static void removeLogger(String uniqueName){
		if (LOGGER_MAP.containsKey(uniqueName)){
			LOGGER_MAP.remove(uniqueName);
		}
	}
	
	/**
	 * getHandleToLogger(String) : Provides The logger from the pool against a unique name.
	 * @param uniqueName : The connection name to be provided to get logger from the pool.
	 * @return : Returns Logger
	 * @throws Exception
	 */
	public static Logger getHandleToLogger(String uniqueName) throws Exception {
		if (LOGGER_MAP.containsKey(uniqueName)){
			return (Logger)LOGGER_MAP.get(uniqueName);
		}else{
			return null;
		}
	}

}
