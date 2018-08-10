package com.ca.nbiapps.core.compnents;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.ca.nbiapps.build.client.RestServiceClient;
import com.ca.nbiapps.build.model.Base;
import com.ca.nbiapps.build.model.BaseResponse;
import com.ca.nbiapps.build.model.Head;
import com.ca.nbiapps.build.model.PullRequest;
import com.ca.nbiapps.build.model.PullRequestEvent;
import com.ca.nbiapps.build.model.Repo;
import com.ca.nbiapps.build.model.ResponseModel;
import com.ca.nbiapps.build.model.TestCaseContext;
import com.ca.nbiapps.build.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
/**
 * 
 * @author Balaji N
 *
 */
@Component
public class BuildClientComponent {
	
	@Autowired
	PropertyComponents propertyComponents;
	
	@Autowired
	RestServiceClient restServiceClient;
	
	
	private PullRequestEvent getPullRequest(String taskId, String lastestCommitSHAId) throws Exception {
		PullRequestEvent pullReqEvent = new PullRequestEvent();
		pullReqEvent.setAction("opened");

		PullRequest pullReq = new PullRequest();
		pullReq.setTitle(taskId);

		Head head = new Head();
		head.setRef("master");
		head.setSha(lastestCommitSHAId);

		User user = new User();
		user.setLogin(propertyComponents.getGitUserName());

		Repo repo = new Repo();
		repo.setFork(true);
		repo.setName(propertyComponents.getSiloName());
		repo.setSsh_url(propertyComponents.getGitForkSshUrl());

		head.setUser(user);
		head.setRepo(repo);

		Base base = new Base();
		base.setSha(propertyComponents.getGitCommitSshId());
		base.setRepo(repo);

		pullReq.setBase(base);
		pullReq.setHead(head);

		pullReqEvent.setPull_request(pullReq);
		return pullReqEvent;
	}

	private String toJsonFromObject(Object object, Type returnTypeOfObject) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson(object, returnTypeOfObject);
	}

	public void pullRequest(TestCaseContext testCaseContext, String taskId, String lastestCommitSHAId) throws Exception {
		Logger logger = testCaseContext.getLogger();
		try {
			PullRequestEvent pullReqEvent = getPullRequest(taskId , lastestCommitSHAId);
			HttpHeaders requestHeaders = restServiceClient.createHttpHeader("*/*", "UTF-8", "application/json");
			requestHeaders.add("X-GitHub-Event", "pull_request");

			if (taskId != null) {
				Type returnTypeOfObject = new TypeToken<PullRequestEvent>() {
				}.getType();
				String payLoad = toJsonFromObject(pullReqEvent, returnTypeOfObject);
				System.out.println("PayLoad:" + payLoad);
				Type returnTypeOfBaseResponse = new TypeToken<BaseResponse>() {
				}.getType();
				BaseResponse baseRes = (BaseResponse) restServiceClient.postRestAPICall(logger, propertyComponents.getPullRequestServiceUrl(), requestHeaders, payLoad, ResponseModel.class, returnTypeOfBaseResponse);
				System.out.println(baseRes.toString());
			} else {
				// TODO: send an email
				logger.info("DT number not created in salesforce.. Try again.!!!");
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * @param testCaseContext
	 * @param taskId
	 * @param buildTask
	 */
	public void doBuildAssert(TestCaseContext testCaseContext, String taskId, JSONObject buildTask) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param testCaseContext
	 */
	public void waitForBuildComplete(TestCaseContext testCaseContext) {
		// TODO Auto-generated method stub
		
	}
	
}
