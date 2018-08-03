package com.ca.nbiapps.build.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 * 
 * @author Balaji N
 *
 */
@JsonAutoDetect
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseModel {
	private Object data;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
