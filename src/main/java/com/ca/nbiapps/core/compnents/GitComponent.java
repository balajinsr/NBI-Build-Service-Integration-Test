package com.ca.nbiapps.core.compnents;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

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
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
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
 * @author Balaji N
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
	
	public boolean addUpstream(Logger logger) throws Exception {
		try (Git git = getGit(logger)) {
			RemoteAddCommand remoteAddCommand = git.remoteAdd();
		    remoteAddCommand.setName("upstream");
		    remoteAddCommand.setUri(new URIish("https://github-isl-test-01.ca.com/bossa02/NBI-Applications-SECUREDEMO"));
		    remoteAddCommand.call(); 
			return true;
		} catch(Exception e) {
			throw e;
		}
	}
	
	public boolean removeUpstream(Logger logger) throws Exception {
		try (Git git = getGit(logger)) {
			RemoteRemoveCommand remoteRemoveCommand = git.remoteRemove();
			remoteRemoveCommand.setName("upstream");
			remoteRemoveCommand.call(); 
			return true;
		} catch(Exception e) {
			throw e;
		}
	}
	
	public Git getGit(Logger logger) throws Exception {
		String localRepoDir = getProperty("LOCAL_FORK_REPO_DIR");
		if (localRepoDir != null) {
			return Git.open(new File(localRepoDir));
		}
		return null;	
	}

	public boolean doCloneAndPull(Logger logger) throws Exception {
		try {
			String localRepoDir = getProperty("LOCAL_FORK_REPO_DIR");
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
		} catch (Exception e) {
			logger.error("Error to CloneAndPull - repo location -[" + getProperty("GIT_SSH_LOCATION") + "] " + e, e);
		}
		return false;
	}

	private void gitPull(Logger logger) throws Exception {
		String localRepoDir = getProperty("LOCAL_FORK_REPO_DIR");
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
			} catch (Exception e) {
				logger.error("Error to pull, repo location -[" + getProperty("GIT_SSH_LOCATION") + "] " + e, e);
			}
		} else {
			logger.error("LOCAL_FORK_REPO_DIR is null");
		}
	}
	
	
	public void reset(Git git, String commitId) throws Exception {
		ResetCommand resetCommand = git.reset();
		resetCommand.setRef(commitId);
		resetCommand.setMode(ResetType.HARD);
		resetCommand.call();
		
	}

	public void gitCommit(Git git, String commentMessage) throws Exception {
		git.commit().setMessage(commentMessage).call();
	}
	
	public void gitRemove(Git git, String filepattern) throws Exception {	
		git.rm().addFilepattern(filepattern).call();
	}
	
	public void gitAdd(Git git, String filePattern) throws Exception {
		git.add().addFilepattern(filePattern).call();
	}
	
	
	public String getLastestCommitId(Logger logger) throws Exception {
		try (Git git = getGit(logger); RevWalk walk = new RevWalk(git.getRepository())) {
			List<Ref> branches = git.branchList().setListMode(ListMode.ALL).call();

			for (Ref branch : branches) {
				RevCommit commit = walk.parseCommit(branch.getObjectId());
				return ObjectId.toString(commit.getId());
			}
		}
		return null;
	}
	
	
	public void gitPush(Git git, boolean isForce) throws Exception {
		PushCommand pushCommand = git.push();
		pushCommand.setRemote("origin");
		pushCommand.setForce(isForce);
		pushCommand.setTransportConfigCallback(new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				SshTransport sshTransport = (SshTransport) transport;
				sshTransport.setSshSessionFactory(sshSessionFactory);
			}
		});
		pushCommand.call();
	}

	public boolean addNewFile(Logger logger, String absoluteFilePath, String fileData) throws Exception {
		File file = new File(absoluteFilePath);
		if (!file.exists()) {
			try (FileWriter fw = new FileWriter(absoluteFilePath)) {
				fw.append(fileData);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error to pull, repo location -[" + getProperty("GIT_SSH_LOCATION") + "] " + e, e);
			}
		}
		return false;
	}

	public boolean modifyFile(Logger logger, String absoluteFilePath, String fileData) throws Exception {
		File file = new File(absoluteFilePath);
		if (file.exists()) {
			try (FileWriter fw = new FileWriter(absoluteFilePath, true)) {
				fw.append(fileData);
				return true;
			} catch (Exception e) {
				logger.error("Error in  modifyExistingFile - repo location -[" + getProperty("GIT_SSH_LOCATION") + "] " + e, e);
			}
		}
		return false;
	}

	public boolean deleteFile(Logger logger, String absoluteFilePath) throws Exception {
		try {
			File file = new File(absoluteFilePath);
			if (file.exists()) {
				return file.delete();
			}
		} catch (Exception e) {
			logger.error("Error in  modifyExistingFile - repo location -[" + getProperty("GIT_SSH_LOCATION") + "] " + e, e);
		}
		return false;
	}

	public boolean cloneRepo(Logger logger) throws Exception {
		try {
			String forkLoc = getProperty("LOCAL_FORK_REPO_DIR");
			File forkFile = new File(forkLoc);
			if(!forkFile.exists()) {
				CloneCommand cloneCommand = Git.cloneRepository();
				cloneCommand.setURI(getProperty("GIT_SSH_LOCATION"));
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
			logger.error("Error to clone , repo location -[" + getProperty("GIT_SSH_LOCATION") + "] " + e, e);
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
		head.setSha("41ef712cedae8e2454f3b786c74c8f431d26b33f");

		User user = new User();
		user.setLogin(getProperty("GIT_USERNAME"));

		Repo repo = new Repo();
		repo.setFork(true);
		repo.setName(getProperty("SILO_NAME"));
		repo.setSsh_url(getProperty("GIT_SSH_LOCATION"));

		head.setUser(user);
		head.setRepo(repo);

		Base base = new Base();
		base.setSha("d8f1a924267a85008bf4460ddb22824f3b07befa");
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
	
	public void pullRequest(Logger logger, String taskId) throws Exception {
		try {
			logger = getLogger("UnitTestCase", "INFO");
			PullRequestEvent pullReqEvent = getPullRequest(taskId);
			String url = getProperty("PULL_REQUEST_URL");
			HttpHeaders requestHeaders = restServiceClient.createHttpHeader("*/*", "UTF-8", "application/json");
			requestHeaders.add("X-GitHub-Event", "pull_request");
			
			if (taskId != null) {
				Type returnTypeOfObject = new TypeToken<PullRequestEvent>() {}.getType();
				String payLoad = toJsonFromObject(pullReqEvent, returnTypeOfObject);
				System.out.println("PayLoad:" + payLoad);
				Type returnTypeOfBaseResponse = new TypeToken<BaseResponse>() {}.getType();
				BaseResponse baseRes = (BaseResponse)restServiceClient.postRestAPICall(logger, url, requestHeaders, payLoad, ResponseModel.class, returnTypeOfBaseResponse);
				System.out.println(baseRes.toString());
			} else {
				// TODO: send an email
				logger.info("DT number not created in salesforce.. Try again.!!!");
			}

		} catch (Exception e) {
			throw e;
		}
	}

}
