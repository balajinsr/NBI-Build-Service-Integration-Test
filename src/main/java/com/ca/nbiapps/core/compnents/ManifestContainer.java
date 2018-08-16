package com.ca.nbiapps.core.compnents;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import com.ca.nbiapps.build.model.BuildFiles;

/**
 * @author Balaji N
 *
 */
public class ManifestContainer {	
	
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static XPathFactory xPathFactory = XPathFactory.newInstance();
	private static DocumentBuilder builder;
	
	XPath xpath = null;
	Document doc =  null;
	public ManifestContainer(String absoluteXMLFileName) throws Exception {
		builder = factory.newDocumentBuilder();
		xpath = xPathFactory.newXPath();
		doc = builder.parse(absoluteXMLFileName);
	}
	
	private Object getNodesData(String xpathExpersion) throws Exception {
		XPathExpression xPathExpr = xpath.compile(xpathExpersion);
		return xPathExpr.evaluate(doc, XPathConstants.NODESET);
	}
	
	public List<String> getXMLDataList(String xpathExp) throws Exception {
		Object result = getNodesData(xpathExp);
		NodeList nodes = (NodeList) result;
		List<String> list = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			list.add(nodes.item(i).getNodeValue());
		}
		return list;
	}
	
	public void prepareManifestDataList(String baseXpathExp, String fileType, List<BuildFiles> resultBuildFiles) throws Exception {
		String xpathCheckSumExp = baseXpathExp+"/Checksum_"+fileType+"/arrayParam/text()";
		String xpathFileName = baseXpathExp+"/"+fileType+"/arrayParam/text()";
		List<String> checkSumList = getXMLDataList(xpathCheckSumExp);
		List<String> fileList = getXMLDataList(xpathFileName);
		
		BuildFiles buildFiles = null;
		for(int i=0;i < checkSumList.size();i++) {
			buildFiles = new BuildFiles();
			buildFiles.setFilePath(fileList.get(i));
			buildFiles.setMd5Value(checkSumList.get(i));
			buildFiles.setAction(fileType);
			resultBuildFiles.add(buildFiles);
		}
	}
	
	
	public void prepareSQLConfigDataList(String baseXpathExp, String fileType, List<BuildFiles> resultBuildFiles) throws Exception {
		String xpathFileName = baseXpathExp+"/"+fileType+"/arrayParam/text()";
		List<String> fileList = getXMLDataList(xpathFileName);
		
		BuildFiles buildFiles = null;
		for(int i=0;i < fileList.size();i++) {
			buildFiles = new BuildFiles();
			buildFiles.setFilePath(fileList.get(i));
			buildFiles.setAction(fileType);
			resultBuildFiles.add(buildFiles);
		}
	}
}
