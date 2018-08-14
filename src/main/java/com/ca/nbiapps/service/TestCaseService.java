package com.ca.nbiapps.service;

import com.ca.nbiapps.build.model.TestCaseContext;

public interface TestCaseService {
	public void process(TestCaseContext testCaseContext) throws Exception;
	public void reset(TestCaseContext testCaseContext) throws Exception;
}
