package com.ca.nbiapps.build.client;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ca.nbiapps.build.model.ResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Balaji N
 */
@Component
public class RestServiceClient {
	
	
	public Object getRestAPICall(Logger logger, String url, Class<?> className, Type returnTypeOfObject) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> responseJSON = restTemplate.getForEntity(url, className);
		Gson gson = new GsonBuilder().create();
		String jsonStr = gson.toJson(responseJSON.getBody());
		return gson.fromJson(jsonStr, returnTypeOfObject);
	}
	
	public Object getSubJSONParseCall(Logger logger, Type returnTypeOfObject, ResponseModel responseModel) throws Exception {
		Gson gson = new GsonBuilder().create();
		String jsonStr = gson.toJson(responseModel.getData());
		return gson.fromJson(jsonStr, returnTypeOfObject);
	}

	public Object postRestAPICall(Logger logger, String url, HttpHeaders requestHeaders, String jsonPayLoadToPost, Class<?> className) throws Exception {
		HttpEntity<String> requestEntity = new HttpEntity<String>(jsonPayLoadToPost, requestHeaders);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> responseJSON = restTemplate.exchange(url, HttpMethod.POST, requestEntity, className);
		return responseJSON.getBody();
	}

	public Object postRestAPICall(Logger logger, String url, HttpHeaders requestHeaders, String jsonPayLoadToPost, Class<?> className, Type returnTypeOfObject) throws Exception {
		HttpEntity<String> requestEntity = new HttpEntity<String>(jsonPayLoadToPost, requestHeaders);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<?> responseJSON = restTemplate.exchange(url, HttpMethod.POST, requestEntity, className);
		Gson gson = new GsonBuilder().create();
		String jsonStr = gson.toJson(responseJSON.getBody());
		return gson.fromJson(jsonStr, returnTypeOfObject);
	}
	
	public HttpHeaders createHttpHeader(String acceptMediaType, String acceptCharset, String contentType) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Accept", acceptMediaType);
		requestHeaders.set("Accept-Charset", acceptCharset);
		requestHeaders.set("Content-Type", contentType);
		return requestHeaders;
	}
}
