package com.ca.nbiapps.integration.test;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * @author Balaji N
 */
@ContextConfiguration(locations = { "/applicationContext.xml" })
public class BaseTest extends AbstractTestNGSpringContextTests {

}
