package com.ca.nbiapps.integration.test;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import com.ca.nbiapps.build.util.FileUtils;

/**
 * @author Balaji N
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class BaseTest extends AbstractTestNGSpringContextTests {
	
	public void handleException(Logger logger,Exception e) {
		logger.error("Error: "+e,e);
	}
	
	public JSONObject getJSONObject(Logger logger, String testTemplateName) throws Exception {
		try (InputStream is = FileUtils.getResourceAsStream(logger, testTemplateName)) {
			return new JSONObject(IOUtils.toString(is));
		} catch (Exception e) {
			throw e;
		}
	}
}
