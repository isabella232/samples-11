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

package com.adobe.livecycle.samples.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;

import com.adobe.idp.Document;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.livecycle.output.client.OutputClient;
import com.adobe.livecycle.output.client.PrintFormat;
import com.adobe.livecycle.output.client.PrintedOutputOptionsSpec;

public class LcPrint {
	
	private Properties properties;
	private String ejbEndPoint;
	private String userName;
	private String password;
	private String outputFormat;
	
	public LcPrint(URL urlToInputProps) throws IOException { 
		initProperties(urlToInputProps);
		extractProperties();
		checkProperties();
	}
	
	/**
	 * This method sends a call to the server for rendering. 
	 */
	public void runSample(String contentRootUri, String formFileName, Document dataDocument, String outputFileUri) throws Throwable {

		Properties oProps = new Properties();
		oProps.put(ServiceClientFactoryProperties.DSC_CREDENTIAL_USERNAME, userName);
		oProps.put(ServiceClientFactoryProperties.DSC_CREDENTIAL_PASSWORD, password);
		oProps.put(ServiceClientFactoryProperties.DSC_SERVER_TYPE, ServiceClientFactoryProperties.DSC_JBOSS_SERVER_TYPE);
		oProps.put(ServiceClientFactoryProperties.DSC_DEFAULT_EJB_ENDPOINT, ejbEndPoint);
		oProps.put(ServiceClientFactoryProperties.DSC_TRANSPORT_PROTOCOL, ServiceClientFactoryProperties.DSC_EJB_PROTOCOL);
		ServiceClientFactory factory = ServiceClientFactory.createInstance(oProps);
		
		PrintFormat format;
		String extn;
		if ("PCL".equalsIgnoreCase(outputFormat)) {
			format = PrintFormat.PCL;
			extn = "pcl";
		} else if ("PostScript".equalsIgnoreCase(outputFormat)) {
			format = PrintFormat.PostScript;
			extn = "ps";
		} else if ("ZPL".equalsIgnoreCase(outputFormat)) {
			format = PrintFormat.ZPL;
			extn = "zpl";
		} else {
			throw new UnsupportedOperationException("Valid Output formats are: PCL,PostScript,ZPL");
		}
		
		OutputClient outputClient = new OutputClient(factory);
		
		PrintedOutputOptionsSpec printedOutputOptionsSpec = new PrintedOutputOptionsSpec();

		if("".equals(outputFileUri)){
			outputFileUri = contentRootUri + File.separator + "output." + extn;
		}
		
		printedOutputOptionsSpec.setFileURI(outputFileUri);
		outputClient.generatePrintedOutput(format, formFileName, 
				contentRootUri, "", printedOutputOptionsSpec, dataDocument);
	}
	
	/**
	 * Loads the properties from passed URL
	 */
	private void initProperties(URL url)
			throws IOException {
		this.properties = new Properties();
		this.properties.load(url.openStream());
	}

	/**
	 * Extracts individual property from loaded properties
	 */	
	private void extractProperties() {
		this.ejbEndPoint = properties.getProperty("ejbEndpoint").trim();
		this.userName = properties.getProperty("userName").trim();
		this.password = properties.getProperty("password").trim();
		this.outputFormat = properties.getProperty("outputFormat").trim();
	}

	/**
	 * This method checks if REQUIRED properties are passed and throws RuntimeExceptions if they are not.
	 */
	private void checkProperties() {
		if (ejbEndPoint == null) {
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
		if (outputFormat == null) {
			throw new RuntimeException(
					"Output Format should be specified (with key \"outputFormat\") in the properties file. It can be one of the following: PCL,PostScript,ZPL");
		}
	}	
	
	/**
	 * This method prompts the user to enter path to xdp/xml/output file.
	 */
	private void runInteractive() throws Throwable {
		final PrintStream OUT = System.out;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		OUT.println("Path of Form to render (REQUIRED) : ");
		String formName = br.readLine();
		File formFile = new File(formName);
		if(!(formFile.exists() && formFile.isFile() && formFile.canRead())){
			throw new FileNotFoundException("File ["+formName+"] doesn't exists OR is a directory OR is not readable");
		}
		String contentRootUri = formFile.getParentFile().getCanonicalPath();
		
		OUT.println("XML Data file path     (OPTIONAL) : ");
		String dataName = br.readLine();
		Document dataDocument;
		if(!"".equals(dataName)){
			File dataFile = new File(dataName);
			if(!(dataFile.exists() && dataFile.isFile() && dataFile.canRead())){
				throw new FileNotFoundException("File ["+dataName+"] doesn't exists OR is a directory OR is not readable");
			}
			dataDocument = new Document(dataFile, false);
		} else {
			dataDocument = new Document(new byte[0]);
		}
		
		OUT.println("Path of Output File    (OPTIONAL) : ");
		
		String outputFileUri = br.readLine();
		runSample(contentRootUri, formFile.getName(), dataDocument, outputFileUri);
	}

	public static void main(String[] args) throws Throwable
	{
		URL url = Thread.currentThread().getContextClassLoader().getResource("input.props");
		LcPrint fsPrint = new LcPrint(url);
		fsPrint.runInteractive();
	}
}
