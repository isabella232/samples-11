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

import com.adobe.idp.dsc.registry.service.ServiceConfigurationFilter;
import com.adobe.idp.dsc.registry.service.client.ServiceRegistryClient;
// import com.adobe.workflow.WorkflowConstants;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.idp.dsc.registry.infomodel.ServiceConfiguration;
import com.adobe.idp.dsc.registry.component.ComponentRegistry;
import com.adobe.idp.dsc.registry.component.client.ComponentRegistryClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * sample code show listing of deployed applications
 */

public class DeployedList {

	protected static ServiceRegistryClient _src = null;

	protected static void init(String propFileName) {

		System.out.println("SDK sample--list deployed LC Applications init() called...");

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
			_src = new ServiceRegistryClient(serviceClientFactory);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("init(): Exception:\n" + e.getMessage());
		}
	}

	/**
	 * @param args expects a path to the properties file
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("Usage: java com.adobe.livecycle.samples.foundationAPIDeployComponent [properties-file] [component-to-deploy]\n");
			System.out
					.println("   - properties-file: Properties file containing configuration required to connect to the LiveCycle Server");
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

			init(propertiesFile);

			try {
				ServiceConfigurationFilter aFilter = new ServiceConfigurationFilter();
				// first filter out non-workflow types
				aFilter.addCondition(ServiceConfigurationFilter.TYPE,"WKF");
				// then filter out those are not activated
				//aFilter.addCondition(ServiceConfigurationFilter.STATE,
				//		ServiceConfiguration.RUNNING);
				// which leaves us services that are currently activated
				Iterator _it = _src.getServiceConfigurations(aFilter)
						.iterator();
				System.out.println("Deployed Applications: ");
				while (_it.hasNext()) {
					ServiceConfiguration _sc = (ServiceConfiguration) _it
							.next();
					// print the IDs in the list
					System.out.println("       " + _sc.getServiceId());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
