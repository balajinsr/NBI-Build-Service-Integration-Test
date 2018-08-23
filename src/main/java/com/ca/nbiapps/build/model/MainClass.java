/**
 * 
 */
package com.ca.nbiapps.build.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author Balaji N
 *
 */
public class MainClass {
 public static void main(String[] args) throws Exception {
	
	 File absolutePomFileLocation = new File("C:/pom.xml");  
	 Writer writer = new FileWriter("C:/pom1.xml");
	 Model model = createModelFromPomFile(absolutePomFileLocation);
	 model.setVersion("3.0.0");    

	
	 new MavenXpp3Writer().write(writer, model );    
	 writer.close();
}
 
 private static Model createModelFromPomFile(File absolutePomFileLocation) throws IOException, XmlPullParserException{
		Model pomModel = null;
		MavenXpp3Reader pomReader = new MavenXpp3Reader();
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(absolutePomFileLocation);
			pomModel = pomReader.read(fileReader);
		} finally {
			if(fileReader != null) {
				fileReader.close();
			}
		}
		return pomModel;
	}
}
