package com.ca.nbiapps.build.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;


@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class PushRequest {
	private String ref;
	private String head;
	private String before;
	private Integer size;
	private Integer distinct_size;
	private List<Commits> commits;
	private Repository repository;
	
	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public Integer getDistinct_size() {
		return distinct_size;
	}

	public void setDistinct_size(Integer distinct_size) {
		this.distinct_size = distinct_size;
	}

	public List<Commits> getCommits() {
		return commits;
	}

	public void setCommits(List<Commits> commits) {
		this.commits = commits;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

}

class Commits {
	private Author author;
	private String message;
	private String sha;
	private String url;
	private String distinct;
	public Author getAuthor() {
		return author;
	}
	public void setAuthor(Author author) {
		this.author = author;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDistinct() {
		return distinct;
	}
	public void setDistinct(String distinct) {
		this.distinct = distinct;
	}
	
	
}

class Author {
	private String email;
	private String name;
	private String username;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}

