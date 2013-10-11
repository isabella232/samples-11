package com.adobe.livecycle.samples.centralmigrationbridge.dataaccess;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLWriter {
	
	private Document moDom = null;
	private Element rootElement = null;
	
	public XMLWriter(String sRootElement) throws ParserConfigurationException{
		createDocument(sRootElement);
	}
	
	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 * @throws ParserConfigurationException 
	 */
	protected void createDocument(String sRootElement) throws ParserConfigurationException {

		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			//get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//create an instance of DOM
			moDom = db.newDocument();
			
			//create the root element 
			rootElement = moDom.createElement(sRootElement);
			moDom.appendChild(rootElement);

		}catch(ParserConfigurationException pce) {
			throw pce;
		}

	}
	
	
	public void addChild(String parent, String key, String value) {
		Element fieldName = moDom.createElement(key);
		Text fieldValue = moDom.createTextNode(value);
		fieldName.appendChild(fieldValue);
		
		
		if(parent == null)
		{
			rootElement.appendChild(fieldName);
		}
		else {
			NodeList nodes = moDom.getElementsByTagName(parent);
			if(nodes.getLength() != 0)
				//add to the first node
				nodes.item(0).appendChild(fieldName);
			else {
				rootElement.appendChild(moDom.createElement(parent)).appendChild(fieldName);
			}
		}
			
	}

	
	/**
	 * This method uses Xerces specific classes
	 * prints the XML document to file.
	 * @throws IOException 
     */
	public void printToStream(OutputStream os) throws IOException{
		try
		{
			//print
			OutputFormat format = new OutputFormat(moDom);
			format.setIndenting(true);
			XMLSerializer serializer = new XMLSerializer(os, format);
			serializer.serialize(moDom);
		} catch(IOException ie) {
			throw ie;	
		}
	}

	
}
