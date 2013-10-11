/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2007 - 2008 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 *
 *
 **************************************************************************/

package com.adobe.livecycle.samples.formsservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A utility class to simplify loading of input.props file.
 * 
 * @author adarsh
 * 
 */
public class InputProps {

	public static final String SOAP_ENDPOINT_URL_KEY = "soapEndpointURL";

	private String soapEndpointURL;

	public static final String USERNAME_KEY = "username";

	private String userName;

	public static final String PASSWORD_KEY = "password";

	private String password;

	public static final String CONTENT_ROOT_URI_KEY = "contentRootURI";

	private String contentRootURI;

	public static final String INPUT_FORM_KEY = "inputForm";

	private String formsQuery;

	public static final String INPUT_DATA_KEY = "inputData";

	private String inputDataXML;

	public static final String OUTPUT_DIR_KEY = "outputDir";

	private String outputDir;

	public static final String TRANSFORM_TO_KEY = "transformTo";

	private String transformTo;

	public static final String USER_AGENT = "userAgent";

	private String userAgent;

	/**
	 * This constructor tries to load input.props as a resource from the
	 * classpath
	 * 
	 * @throws IOException
	 */
	public InputProps() throws IOException {
		Properties properties = new Properties();
		InputStream propsInputStream = getClass().getResourceAsStream(
				"/input.props");
		if (propsInputStream != null) {
			properties.load(propsInputStream);
		}
		init(properties);
	}

	/**
	 * This constructor loads input.props file from the given filename
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public InputProps(String fileName) throws FileNotFoundException,
			IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(fileName));
		init(properties);
	}

	public InputProps(Properties properties) {
		init(properties);
	}

	private void init(Properties properties) {
		setSoapEndpointURL(properties.getProperty(SOAP_ENDPOINT_URL_KEY,
				"http://localhost:8080/soap/services/FormsService"));
		setUserName(properties.getProperty(USERNAME_KEY, "administrator"));
		setPassword(properties.getProperty(PASSWORD_KEY, "password"));
		setFormsQuery(properties.getProperty(INPUT_FORM_KEY, "input.xdp"));
		setInputDataXML(properties.getProperty(INPUT_DATA_KEY, "input.xml"));
		setContentRootURI(properties.getProperty(CONTENT_ROOT_URI_KEY,
				new File("../collaterals/resources").getAbsolutePath()));

		setOutputDir(properties.getProperty(OUTPUT_DIR_KEY, "../collaterals/output"));
		setTransformTo(properties.getProperty(TRANSFORM_TO_KEY, "AUTO"));
		setUserAgent(properties.getProperty(USER_AGENT, "Mozilla/3.*"));
	}

	public String getSoapEndpointURL() {
		return soapEndpointURL;
	}

	public void setSoapEndpointURL(String soapEndpointURL) {
		this.soapEndpointURL = soapEndpointURL;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFormsQuery() {
		return formsQuery;
	}

	public void setFormsQuery(String formsQuery) {
		this.formsQuery = formsQuery;
	}

	public String getInputDataXML() {
		return inputDataXML;
	}

	public void setInputDataXML(String inputDataXML) {
		this.inputDataXML = inputDataXML;
	}

	public String getContentRootURI() {
		return contentRootURI;
	}

	public void setContentRootURI(String contentRootURI) {
		this.contentRootURI = contentRootURI;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getTransformTo() {
		return transformTo;
	}

	public void setTransformTo(String transformTo) {
		this.transformTo = transformTo;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
