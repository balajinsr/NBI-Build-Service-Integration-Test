package com.ca.nbiapps.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.core.compnents.TestCaseContext;
import com.ca.nbiapps.service.TestCaseService;
/**
 * 
 * @author Balaji N
 *
 */
@Service
public class TestCaseServiceImpl implements TestCaseService {

	@Autowired
	GitComponent gitComponent;

	@Override
	public void process(TestCaseContext testCaseContext) throws Exception {
		gitComponent.processDeveloperGitTask(testCaseContext);
		
	}
}
