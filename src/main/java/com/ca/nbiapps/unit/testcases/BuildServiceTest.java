package com.ca.nbiapps.unit.testcases;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.model.Folder;
import org.jfrog.artifactory.client.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.ca.nbiapps.build.client.RestServiceClient;
import com.ca.nbiapps.core.compnents.ArtifactoryComponent;
import com.ca.nbiapps.core.compnents.GitComponent;
import com.ca.nbiapps.integration.test.BaseTest;

/**
 * 
 * @author Balaji N
 *
 */
public class BuildServiceTest extends BaseTest {
	
	@Autowired
	GitComponent gitComponent;
	
	@Autowired
	RestServiceClient restServiceClient;
	
	@Autowired
	ArtifactoryComponent artifactoryComponent;
	
	@Test
	public void testGitClone(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			gitComponent.cloneRepo(logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testAddUpstream(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			gitComponent.addUpstream(logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRemoveUpstream(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			gitComponent.removeUpstream(logger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testgetArtifactoryDownload(final ITestContext testContext) {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			String saveLocalDir = artifactoryComponent.getPathByOSSpecific(gitComponent.getProperty("LOCAL_DIR_LOC")).toString();
			Artifactory artifactory= artifactoryComponent.createArtifactory("nbi-app-build","AP6awfCdSn1Z5FifXU2nFRPHi7T","http://itc-dsdc.ca.com:80/artifactory");
			File file = artifactoryComponent.downloadFile(artifactory, "maven-release-local", "NBI-Applications-SECUREDEMO/preview-release_22-Jun-2018_14505227643/nbiservice-acspage.zip", saveLocalDir+File.separator+"nbiservice-acspage.zip");
			logger.info("File Download successfully - "+file.toString());
			boolean isUnzipSuccess = artifactoryComponent.unzip(file.toString(), saveLocalDir+File.separator+"nbiservice-acspage");
			if(isUnzipSuccess) file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGetArtifactsList() {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			Artifactory artifactory= artifactoryComponent.createArtifactory("nbi-app-build","AP6awfCdSn1Z5FifXU2nFRPHi7T","http://itc-dsdc.ca.com:80/artifactory");
			Folder folder = artifactory.repository(gitComponent.getProperty("MAVEN_RELEASE_LOCAL")).folder("/NBI-Applications-SECUREDEMO/preview-release_22-Jun-2018_14505227643").info();
			List<Item> childrens = folder.getChildren();
			List<Item> filterList = childrens.stream().filter(s->s.getUri().contains(".zip")).collect(Collectors.toList());
			logger.info("filterList : " +filterList.toString());
			logger.info("nbiservice-acspage.zip - "+folder.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetPathByOSSpecific() {
		Logger logger  = null;
		try {
			logger = gitComponent.getLogger("unitTestLog", "INFO");
			String artifactName = artifactoryComponent.getArtifactName("/NBI-Applications-SECUREDEMO/preview-release_22-Jun-2018_14505227643/nbiservice-acspage.zip");
			logger.info(artifactName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
