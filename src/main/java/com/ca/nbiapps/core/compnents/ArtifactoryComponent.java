package com.ca.nbiapps.core.compnents;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Balaji N
 */
@Component
public class ArtifactoryComponent extends CommonComponent {
	public Artifactory createArtifactory(String username, String password, String artifactoryUrl) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(artifactoryUrl)){
            throw new IllegalArgumentException("Arguments passed to createArtifactory are not valid");
        }
        return ArtifactoryClientBuilder.create()
                .setUrl(artifactoryUrl)
                .setUsername(username)
                .setPassword(password)
                .build();
    }
	
	
	public String getArtifactName(String uri) {
		if(uri == null || StringUtils.isEmpty(uri)) {
			throw new IllegalArgumentException("Arguments passed to get artifactName is not valid");
	    }
		Path path = Paths.get(uri);
		if(path.getFileName() != null) {
			return path.getFileName().toString().replace(".zip", "");
		}
		return "";
	}
	
	 public java.io.File downloadFile(Artifactory artifactory, String repo, String filePath, String fileDownloadToLocation) throws Exception {
	        if (artifactory == null || StringUtils.isEmpty(repo) || StringUtils.isEmpty(filePath)){
	            throw new IllegalArgumentException("Arguments passed to downloadFile are not valid");
	        }

	        java.io.File targetFile = null;
	        try(InputStream inputStream = artifactory.repository(repo)
	                .download(filePath)
	                .doDownload()) {
	        	targetFile = new java.io.File(fileDownloadToLocation);
		        FileUtils.copyInputStreamToFile(inputStream, targetFile);
	        } 
	        return targetFile;
	    }
	
	public boolean unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        
        try(ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
        	ZipEntry entry = zipIn.getNextEntry();
	        // iterates over entries in the zip file
	        while (entry != null) {
	            String filePath = destDirectory + File.separator + entry.getName();
	            if (!entry.isDirectory()) {
	                // if the entry is a file, extracts it
	                extractFile(zipIn, filePath);
	            } else {
	                // if the entry is a directory, make the directory
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            entry = zipIn.getNextEntry();
	        }
        }
        return true;
		
    }
   
    private boolean extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    	int BUFFER_SIZE = 4096;
    	
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
	        byte[] bytesIn = new byte[BUFFER_SIZE];
	        int read = 0;
	        while ((read = zipIn.read(bytesIn)) != -1) {
	            bos.write(bytesIn, 0, read);
	        }
        }
        return true;
    }
    
    @Deprecated
	public void downloadFile(Logger logger, String fileURL, String saveDir) throws IOException {
		int BUFFER_SIZE = 4096;
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = null;
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			httpConn = (HttpURLConnection) url.openConnection();
			int responseCode = httpConn.getResponseCode();
	
			// always check HTTP response code first
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String fileName = "";
				String disposition = httpConn.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();
	
				if (disposition != null) {
					// extracts file name from header field
					int index = disposition.indexOf("filename=");
					
					if (index > 0) {
						fileName = disposition.substring(index + 10,  disposition.indexOf(";", disposition.indexOf(";")+1) - 1);
					}
				} else {
					// extracts file name from URL
					fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
				}
	
				logger.info("Content-Type = " + contentType);
				logger.info("Content-Disposition = " + disposition);
				logger.info("Content-Length = " + contentLength);
				logger.info("fileName = " + fileName);
	
				// opens input stream from the HTTP connection
				inputStream = httpConn.getInputStream();
				String saveFilePath = saveDir + File.separator + fileName;
				
				File outFile = new File(saveFilePath);
				if(!outFile.exists()) {
					outFile.getParentFile().mkdirs();
				}
				
				// opens an output stream to save into file
				outputStream = new FileOutputStream(saveFilePath);
	
				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				logger.info("File downloaded from - "+fileURL+" to "+saveFilePath);
			} else {
				logger.info("No file to download. Server replied HTTP code: " + responseCode);
			}
		} finally {
			try {
				if(outputStream!=null) {
					outputStream.close();
				}
				
				if(inputStream!=null) {
					inputStream.close();
				}
				
				if(httpConn!=null) {
					httpConn.disconnect();
				}
			} catch(Exception e) {
				logger.error("Error: "+e,e);
			}
		}	
	}
}
