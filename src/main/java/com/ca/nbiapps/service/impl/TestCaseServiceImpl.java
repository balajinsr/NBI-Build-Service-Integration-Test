package com.ca.nbiapps.service.impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ca.nbiapps.build.util.FileUtils;
import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.service.TestCaseService;

@Service
public class TestCaseServiceImpl implements TestCaseService {

	@Autowired
	GitComponent gitComponent;

	@Override
	public boolean process(String testName) throws Exception {
		boolean processStatus = false;
		Logger logger = gitComponent.getLogger(gitComponent.getProperty("SILO_NAME"), "INFO");
		JSONObject jsonTemplateObject = getJSONObject(logger, "testcasesTemplates/add-ACSPAGE-Test.json");
		gitComponent.cloneRepo(logger);
		String commitId = gitComponent.getLastestCommitId(logger);
		processDeveloperGitTask(logger, jsonTemplateObject);
		return processStatus;
	}

	private JSONObject getJSONObject(Logger logger, String testTemplateName) throws Exception {
		try (InputStream is = FileUtils.getResourceAsStream(logger, testTemplateName)) {
			return new JSONObject(IOUtils.toString(is));
		} catch (Exception e) {
			throw e;
		}
	}

	private boolean processDeveloperGitTask(Logger logger, JSONObject jsonTemplateObject) throws Exception {
		JSONArray buildTasks = jsonTemplateObject.getJSONArray("buildtasks");
		Git git = gitComponent.getGit(logger);
		for (int i = 0; i < buildTasks.length(); i++) {
			JSONObject buildTask = buildTasks.getJSONObject(i);
			String taskId = buildTask.getString("taskId");
			if(changeFiles(logger, buildTask)) {
				gitComponent.gitCommit(git, taskId);
				gitComponent.gitPush(git, false);
				gitComponent.pullRequest(logger, taskId);
			}
		}
		return true;
	}
	
	public boolean changeFiles(Logger logger, JSONObject buildTask) throws Exception{
		JSONArray commitFileList = buildTask.getJSONArray("commitFileList");
		String srcBasePath = gitComponent.getProperty("TEST-SRC-DATA-BASE-LOC");
		String destBasePath = gitComponent.getProperty("LOCAL_FORK_REPO_DIR");
		boolean changeFileStatus = true;
		for (int j = 0; j < commitFileList.length(); j++) {
			JSONObject fileObj = commitFileList.getJSONObject(j);
			String fromPath = fileObj.getString("filePath");
			String action = fileObj.getString("action");
			String md5Value = fileObj.getString("md5Value");
			Path from = Paths.get(Paths.get(srcBasePath+File.separator+fromPath).toString()); 
			Path to = Paths.get(Paths.get(destBasePath+File.separator+fromPath).toString()); 
			
			if(action.equalsIgnoreCase("delete")) {
				Files.delete(to);
				gitComponent.gitRemove(gitComponent.getGit(logger), fromPath);
			} else {
				Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
				changeFileStatus = checkMd5(md5Value, to);
				if(!changeFileStatus) {
					break;
				}
				gitComponent.gitAdd(gitComponent.getGit(logger), fromPath);
			}
			
		}
		return true;
	}

	private boolean checkMd5(String md5Value, Path to) {
		return "bbd636bad4c05715566999fd407b8897".equals(md5Value);
	}

}
