package com.ca.nbiapps.core.compnents;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidParameterException;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.RemoteRemoveCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.HttpTransport;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import org.json.JSONArray;
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
import com.ca.nbiapps.build.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 *  @author Balaji N
 */
@Component
public class GitComponent extends CommonComponent {

	@Autowired
	RestServiceClient restServiceClient;

	public SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
		@Override
		protected void configure(Host arg0, Session session) {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		}

		public String getOsName() {
			return System.getProperty("os.name");
		}

		public String getSSHFileLocation() {
			if (getOsName().startsWith("Windows")) {
				String sshPath = System.getenv("systemdrive") + File.separator + System.getenv("homepath");
				return sshPath + File.separator + ".ssh" + File.separator + "github_rsa";
			} else {
				return "~" + File.separator + ".ssh" + File.separator + "github_rsa";
			}

		}

		@Override
		protected JSch createDefaultJSch(FS fs) throws JSchException {
			JSch defaultJSch = super.createDefaultJSch(fs);
			defaultJSch.addIdentity(getSSHFileLocation());

			// defaultJSch.addIdentity("C:/Users/nanba03/.ssh/github_rsa");
			// String knownHostPublicKey = "ssh-rsa
			// AAAAB3NzaC1yc2EAAAADAQABAAABAQCbEYp+IK6IEYPvf2mLnwKDWvcEg9ONlq64EAaNyRCs4JU03sOyExMUzAw6FP1Yv6CZYapge7NFify5cLUgKN7bRmCXKXr3bTnb7O5xinrao4dVGZOKvuf9KE7r0Pktb7vh1hxVQ5NTXW4eqN0dVm7Gd0n8LfxWjdypSBcjwBgfoIw7BnQlKQotcf3djdCQpvF8DWuzf1giMO5J6vPd4ajvYvL+pzwUZ2RNzwyuWHPKrSK3mmII026FxQ1igIlhCH1mU25F4OfgBUqJXPzs9jW6pArbsu/u28OOVJZBzrRYw/anKxW68b1HBO6jatfM/ipS4QqZ/6Fkiuk3FM6kADVZ";
			// defaultJSch.setKnownHosts(new
			// ByteArrayInputStream(knownHostPublicKey.getBytes()));
			return defaultJSch;
		}
	};

	public boolean addUpstream() throws Exception {
		String upstreamGitUrl = propertyComponents.getGitUpstreamSshUrl();
		try (Git git = getGit()) {
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
			remoteAddCommand.setName("upstream");
			remoteAddCommand.setUri(new URIish(upstreamGitUrl));
			remoteAddCommand.call();
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public boolean addOrigin() throws Exception {
		String upstreamGitUrl = propertyComponents.getGitUpstreamSshUrl();
		try (Git git = getGit()) {
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
			remoteAddCommand.setName("upstream");
			remoteAddCommand.setUri(new URIish(upstreamGitUrl));
			remoteAddCommand.call();
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean removeUpstream() throws Exception {
		try (Git git = getGit()) {
			RemoteRemoveCommand remoteRemoveCommand = git.remoteRemove();
			remoteRemoveCommand.setName("upstream");
			remoteRemoveCommand.call();
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	private Git getGit() throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			return Git.open(new File(localRepoDir));
		}
		throw new InvalidParameterException("Git fork location property [LOCAL_FORK_REPO_DIR] value is invalid!");
	}

	public boolean doCloneAndPull(Logger logger) throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			File repoLocalDir = new File(localRepoDir);
			if (!repoLocalDir.exists()) {
				cloneRepo(logger);
			} else {
				gitPull(logger);
			}
			return true;
		} else {
			logger.error("LOCAL_FORK_REPO_DIR is null");
		}
		return false;
	}

	private void gitPull(Logger logger) throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			try (Git git = Git.open(new File(localRepoDir))) {
				PullCommand pullCommand = git.pull();
				pullCommand.setTransportConfigCallback(new TransportConfigCallback() {
					@Override
					public void configure(Transport transport) {
						SshTransport sshTransport = (SshTransport) transport;
						sshTransport.setSshSessionFactory(sshSessionFactory);
					}
				});
				PullResult pullResult = pullCommand.call();
				logger.info("git pull is " + (pullResult.isSuccessful() ? "Success" : "Failed"));
				logger.info("MergeStatus: " + pullResult.getMergeResult().getMergeStatus().toString());
				logger.info("FetchResult: " + pullResult.getFetchResult().getMessages());
			}
		} else {
			logger.error("LOCAL_FORK_REPO_DIR is null");
		}
	}

	public boolean gitResetHard(Logger logger, String commitId) throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			try (Git git = Git.open(new File(localRepoDir))) {
				ResetCommand resetCommand = git.reset();
				resetCommand.setRef(commitId);
				resetCommand.setMode(ResetType.HARD);
				resetCommand.call();
				return true;
			} 
		}
		return false;
	}

	public void gitCommit(String commentMessage) throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			try (Git git = Git.open(new File(localRepoDir))) {
				git.commit().setMessage(commentMessage).call();
			}
		}
	}

	public void gitRemove(String filepattern) throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			try (Git git = Git.open(new File(localRepoDir))) {
				git.rm().addFilepattern(filepattern).call();
			}
		}
	}

	public void gitAdd(String filePattern) throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			try (Git git = Git.open(new File(localRepoDir))) {
				git.add().addFilepattern(filePattern).call();
			}
		}
	}

	public String getLastestCommitId() throws Exception {
		try (Git git = getGit(); RevWalk walk = new RevWalk(git.getRepository())) {
			List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();

			for (Ref branch : branches) {
				RevCommit commit = walk.parseCommit(branch.getObjectId());
				return ObjectId.toString(commit.getId());
			}
		}
		throw new InvalidActivityException("Unable to get the latest commit id value.!");
	}

	public void gitPush(Logger logger, boolean isForce, String remoteName) throws Exception {
		String localRepoDir = propertyComponents.getLocalForkReopDir();
		if (localRepoDir != null) {
			try (Git git = Git.open(new File(localRepoDir))) {
				PushCommand pushCommand = git.push();
				pushCommand.setRemote(remoteName);
				if (isForce) {
					pushCommand.setForce(isForce);
				}
				pushCommand.setTransportConfigCallback(new TransportConfigCallback() {
					  @Override
					  public void configure(Transport transport) {
					    if( transport instanceof SshTransport ) {
					      SshTransport sshTransport = (SshTransport) transport;
					      sshTransport.setSshSessionFactory( sshSessionFactory );
					    } else if( transport instanceof HttpTransport ) {
					      // configure HTTP protocol specifics
					    }
					  }
					} );
				
				pushCommand.call();
			} 
		}
	}

	public boolean cloneRepo(Logger logger) throws Exception {
		try {
			String forkLoc = propertyComponents.getLocalForkReopDir();
			File forkFile = new File(forkLoc);
			if (!forkFile.exists()) {
				CloneCommand cloneCommand = Git.cloneRepository();
				cloneCommand.setURI(propertyComponents.getGitForkSshUrl());
				cloneCommand.setDirectory(forkFile);
				cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
					@Override
					public void configure(Transport transport) {
						SshTransport sshTransport = (SshTransport) transport;
						sshTransport.setSshSessionFactory(sshSessionFactory);
					}
				});
				cloneCommand.call();
				logger.info("git clone is success");
			} else {
				logger.info("git clone already done!. Skipping");
			}
			return true;
		} catch (Exception e) {
			logger.error("Error to clone , repo location -[" + propertyComponents.getGitForkSshUrl() + "] " + e, e);
		}
		return false;
	}

	private PullRequestEvent getPullRequest(String taskId) throws Exception {
		PullRequestEvent pullReqEvent = new PullRequestEvent();
		pullReqEvent.setAction("opened");

		PullRequest pullReq = new PullRequest();
		pullReq.setTitle(taskId);

		Head head = new Head();
		head.setRef("master");
		head.setSha(getLastestCommitId());

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

	private void pullRequest(TestCaseContext testCaseContext, String taskId) throws Exception {
		Logger logger = testCaseContext.getLogger();
		try {
			PullRequestEvent pullReqEvent = getPullRequest(taskId);
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

	public void processDeveloperGitTask(TestCaseContext testCaseContext) throws Exception {
		Logger logger = testCaseContext.getLogger();
		JSONArray buildTasks = testCaseContext.getTestCaseData().getJSONArray("buildtasks");
		for (int i = 0; i < buildTasks.length(); i++) {
			JSONObject buildTask = buildTasks.getJSONObject(i);
			String taskId = buildTask.getString("taskId");
			if (changeFiles(logger, buildTask)) {
				gitCommit(taskId);
				gitPush(logger, false, "origin");
				pullRequest(testCaseContext, taskId);
			}
		}
	}

	public boolean changeFiles(Logger logger, JSONObject buildTask) throws Exception {
		JSONArray commitFileList = buildTask.getJSONArray("commitFileList");
		String srcBasePath = propertyComponents.getTestDataBasePath();
		String destBasePath = propertyComponents.getLocalForkReopDir();
		boolean changeFileStatus = true;
		for (int j = 0; j < commitFileList.length(); j++) {
			JSONObject fileObj = commitFileList.getJSONObject(j);
			String fromPath = fileObj.getString("filePath");
			String action = fileObj.getString("action");
			String md5Value = fileObj.getString("md5Value");
			Path from = getPathByOSSpecific(srcBasePath + File.separator + fromPath);
			Path to = getPathByOSSpecific(destBasePath + File.separator + fromPath);

			if (action.equalsIgnoreCase("delete")) {
				Files.delete(to);
				gitRemove(fromPath);
			} else {
				Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
				changeFileStatus = checkMd5(md5Value, to);
				if (!changeFileStatus) {
					break;
				}
				gitAdd(fromPath);
			}

		}
		return true;
	}

	private boolean checkMd5(String md5Value, Path to) {
		return "bbd636bad4c05715566999fd407b8897".equals(md5Value);
	}

}
