/* *****************************************************************************
 *
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2004-2007 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Adobe Systems Incorporated and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Adobe Systems
 * Incorporated and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright
 * law. Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained from
 * Adobe Systems Incorporated.
 *
 */
package com.adobe.livecycle.samples.foundationAPI;

import com.adobe.idp.applicationmanager.client.ApplicationManager;
import com.adobe.idp.applicationmanager.application.ApplicationStatus;
import com.adobe.idp.applicationmanager.application.ApplicationDocument;
import com.adobe.idp.Document;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.idp.dsc.registry.infomodel.ServiceConfiguration;
import com.adobe.idp.dsc.registry.service.client.ServiceRegistryClient;
import com.adobe.idp.dsc.registry.component.ComponentRegistry;
import com.adobe.idp.dsc.registry.component.client.ComponentRegistryClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * samples show LiveCycle Archive creation and deployment
 */

public class CreateAndDeployLCA {

	protected static ApplicationManager applicationManager = null;

	protected static void init(String propFileName) {

		System.out.println("SDK sample--create and deploy LC archive init() called...");

		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(propFileName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error creating FileInputStream from ["
					+ propFileName + "]", e);
		}
		Properties properties = new Properties();
		try {
			properties.load(fileInputStream);
		} catch (IOException e) {
			throw new RuntimeException("Error loading properties from ["
					+ propFileName + "]", e);
		}

		try {
			ServiceClientFactory serviceClientFactory = ServiceClientFactory
					.createInstance(properties);
			if (serviceClientFactory == null)
				System.out.println("failed getting ServiceClientFactory.");
			applicationManager = new ApplicationManager(serviceClientFactory);
			if (applicationManager == null)
				System.out.println("failed getting ApplicationManager.");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("init(): Exception:\n" + e.getMessage());
		}
	}

	protected static void createLCA(String file) {
		ApplicationStatus result = null;
		try {
			ApplicationDocument applicationDescriptorXmlBean = ApplicationDocument.Factory
					.parse(new FileInputStream(file));
			if (applicationDescriptorXmlBean == null)
				System.out.println("please make sure input xml file is valid.");

			result = applicationManager
					.exportApplicationArchive(applicationDescriptorXmlBean);

			Document applicationArchiveDocument = result.getArchiveDocument();
			if (applicationArchiveDocument != null) {
				InputStream archiveInputStream = applicationArchiveDocument
						.getInputStream();

				// name the output .lca file which can be used to test deploying
				// lca files.
				FileOutputStream fos = new FileOutputStream("LCASample.lca");
				byte[] buffer = new byte[10240];
				int num_bytes = 0;
				for (int count = 0; count < applicationArchiveDocument.length(); count += num_bytes) {
					num_bytes = archiveInputStream.read(buffer);
					fos.write(buffer, 0, num_bytes);
				}
				fos.close();
			} else
				System.out.println("Exported document not found\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		String status = "";
		String messageString = null;
		if(result.getStatusCode() == ApplicationStatus.STATUS_EXPORT_COMPLETE){
			status = "Complete";
		} else if (result.getStatusCode() == ApplicationStatus.STATUS_EXPORT_ERROR){
			status = "Error";
			messageString = "Error text: " + result.getErrorString();
		}
		System.out.println("exportApplicationArchive result Status: " + status); 
		if(messageString != null) {
			System.out.println(messageString); 
		}
	}

	protected static void deployLCA(String file) {
		ApplicationStatus result = null;
		try {
			FileInputStream stream = new FileInputStream(file);
			int size = (int) stream.getChannel().size();
			byte[] applicationArchiveByteArray = new byte[size];
			stream.read(applicationArchiveByteArray, 0, size);
			stream.close();

			result = applicationManager
					.importApplicationArchive(applicationArchiveByteArray);

			String status = "";
			String messageString = null;
			if(result.getStatusCode() == ApplicationStatus.STATUS_IMPORT_COMPLETE){
				status = "Complete";
			} else if (result.getStatusCode() == ApplicationStatus.STATUS_IMPORT_ERROR){
				status = "Error";
				messageString = "Error text: " + result.getErrorString();
			}
			System.out.println("importApplicationArchive result Status: " + status); 
			if(messageString != null) {
				System.out.println(messageString); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 *            expects a path name to either a .xml file or a .lca file
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			System.out
					.println("Usage: java com.adobe.livecycle.samples.foundationAPI.CreateAndDeployLCA [properties-file] [xml-or-lca]\n");
			System.out
					.println("   - properties-file: Properties file containing configuration required to connect to the LiveCycle Server");
			System.out
					.println("   - XML-or-LCA: the full path to either a .XML or a .LCA file depends on create or deploy\n");
		} else {
			// get the properties-file
			String propertiesFile = "Jboss.properties";
			propertiesFile = args[0];
			File file = new File(propertiesFile);
			if (!file.exists()) {
				System.out.println("Error: properties-file not found ["
						+ file.getPath() + "]");
				return;
			}

			// get the xml or the lca file
			String comp = "";
			if (args.length > 1)
				comp = args[1];

			// check the file exists
			file = new File(comp);
			if (!file.exists()) {
				System.out.println("Error: input file not found ["
						+ file.getPath() + "]");
				return;
			}

			init(propertiesFile);

			// create LCA from given application.xml file
			if (args[1].endsWith(".xml"))
				createLCA(args[1]);

			// deploy given LCA
			if (args[1].endsWith(".lca"))
				deployLCA(args[1]);

		}
	}
}
