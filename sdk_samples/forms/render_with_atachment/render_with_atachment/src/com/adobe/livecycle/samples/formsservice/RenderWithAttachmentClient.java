/*
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2007 Adobe Systems Incorporated
 * All Rights Reserved
 * 
 * NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it.  If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 */
package com.adobe.livecycle.samples.formsservice;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import com.adobe.idp.Document;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.livecycle.formsservice.client.FormsResult;
import com.adobe.livecycle.formsservice.client.FormsServiceClient;
import com.adobe.livecycle.formsservice.client.PDFFormRenderSpec;
import com.adobe.livecycle.formsservice.client.URLSpec;

/**
 * This sample demonstrate how an xdp and (optional) data can be rendered to a PDF Form containing some attachments.
 */
public class RenderWithAttachmentClient {
	
	private Properties properties;
	private String contentRootURI;
	private String inFormName;
	private String ejbEndpoint;
	private String userName;
	private String password;
	private String attachments;
	private String outputFile;
	private String dataFileName;


	public RenderWithAttachmentClient(URL urlToInputProps) throws IOException {
		initProperties(urlToInputProps);
		extractProperties();
		checkProperties();		
	}

	public void runSample() throws Throwable {
		Properties oProps = new Properties();
		oProps.put(ServiceClientFactoryProperties.DSC_CREDENTIAL_USERNAME, userName);
		oProps.put(ServiceClientFactoryProperties.DSC_CREDENTIAL_PASSWORD, password);
		oProps.put(ServiceClientFactoryProperties.DSC_SERVER_TYPE, ServiceClientFactoryProperties.DSC_JBOSS_SERVER_TYPE);
		oProps.put(ServiceClientFactoryProperties.DSC_DEFAULT_EJB_ENDPOINT, ejbEndpoint);
		oProps.put(ServiceClientFactoryProperties.DSC_TRANSPORT_PROTOCOL, ServiceClientFactoryProperties.DSC_EJB_PROTOCOL);
		ServiceClientFactory factory = ServiceClientFactory.createInstance(oProps);
		URLSpec urlSpec = new URLSpec();
		urlSpec.setContentRootURI(contentRootURI);
		FormsServiceClient formsServiceClient = new FormsServiceClient(factory);
		Document inDataDoc = new Document(new byte[0]);
		if (!"".equals(dataFileName)) {
			inDataDoc = new Document(new File(contentRootURI + File.separator + dataFileName), false);
		}
		Document inFormDocument = new Document(new File(contentRootURI + File.separator + inFormName), false);
		FormsResult outFormsResult = formsServiceClient.renderPDFForm2(inFormDocument, inDataDoc, new PDFFormRenderSpec(), urlSpec, getAttachments(attachments));
		Document outDoc = outFormsResult.getOutputContent();
		outDoc.copyToFile(new File(contentRootURI + File.separator + outputFile));
	}
	
	/**
	 * Generates and returns a map with key as fileNames and value as document objects of the files
	 * lying under contentRootUri
	 * 
	 * @param attachments Comma seperated attachment names
	 * @return
	 */
	private Map<String, Document> getAttachments(String attachments) {
		Map<String, Document> map = new HashMap<String, Document>();
		StringTokenizer st = new StringTokenizer(attachments, ",");
		while (st.hasMoreTokens()) {
			String key = st.nextToken().trim();
			Document value = new Document(new File(contentRootURI + File.separator + key), false);
			map.put(key, value);
		}
		return map;
	}
	
	private void initProperties(URL url) throws IOException {
		this.properties = new Properties();
		this.properties.load(url.openStream());
	}
	
	/**
	 * Extracts individual property from loaded properties
	 */
	private void extractProperties() {
		this.contentRootURI = properties.getProperty("contentRootURI");
		this.inFormName = properties.getProperty("inputForm");
		this.ejbEndpoint = properties.getProperty("ejbEndpoint");
		this.userName = properties.getProperty("userName");
		this.password = properties.getProperty("password");
		this.dataFileName = properties.getProperty("inputData");
		this.attachments = properties.getProperty("attachments");
		this.outputFile = properties.getProperty("outputFile");
	}
	
	/**
	 * This method checks if REQUIRED properties are passed and throws RuntimeExceptions if they are not. This method also set 
	 * Default values to OPTIONAL properties.
	 */
	private void checkProperties() {
		if (contentRootURI == null) {
			throw new RuntimeException(
					"Content Root URI should be specified (with key \"contentRootURI\") in the properties file");
		}
		if (ejbEndpoint == null) {
			throw new RuntimeException(
					"EJB Endpoint (like jnp://localhost:1099) should be specified (with key \"ejbEndpoint\") in the properties file");
		}
		if (userName == null) {
			throw new RuntimeException(
					"User Name should be specified (with key \"userName\") in the properties file");
		}
		if (password == null) {
			throw new RuntimeException(
					"Password should be specified (with key \"password\") in the properties file");
		}
		if (attachments == null) {
			attachments = "";
		}
		if(dataFileName == null){
			dataFileName = "";
		}
		if (inFormName == null) {
			inFormName = "input.xdp";
		}
		if(outputFile == null){
			outputFile = "output.pdf";
		}		
	}

	public static void main(String[] args) throws Throwable {
		URL urlToInputProps = Thread.currentThread().getContextClassLoader().getResource("collaterals/input.props");
		RenderWithAttachmentClient client = new RenderWithAttachmentClient(urlToInputProps);
		client.runSample();
	}
}
