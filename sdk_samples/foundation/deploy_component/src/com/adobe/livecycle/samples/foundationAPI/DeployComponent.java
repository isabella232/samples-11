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

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Element;

import com.adobe.idp.Document;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.registry.component.ComponentRegistry;
import com.adobe.idp.dsc.registry.component.client.ComponentRegistryClient;
import com.adobe.idp.dsc.registry.infomodel.Component;
import com.adobe.idp.dsc.registry.infomodel.ServiceConfiguration;
import com.adobe.idp.dsc.registry.service.ServiceRegistry;
import com.adobe.idp.dsc.registry.service.client.ServiceRegistryClient;
import com.adobe.idp.dsc.util.DOMUtil;

/**
 * samples show LiveCycle component deployment
 */

public class DeployComponent {
	protected static ComponentRegistry _cr = null;

	protected static ServiceRegistry _sr = null;

	protected static void init(String propFileName) {

		System.out.println("SDK sample--deploy LC component init() called...");

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
			_cr = new ComponentRegistryClient(serviceClientFactory);
			_sr = new ServiceRegistryClient(serviceClientFactory);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("init(): Exception:\n" + e.getMessage());
		}
	}

	protected static ServiceConfiguration getServiceConfiguration(
			Component comp, String serviceName) {

		List svcs = _sr.getServiceConfigurations(comp);
		for (int i = 0; i < svcs.size(); i++) {
			Object next = svcs.get(i);
			ServiceConfiguration nextSvc = (ServiceConfiguration) next;
			String name = nextSvc.getServiceId();

			if (name.equals(serviceName)) {
				return nextSvc;
			}

		}

		return null;
	}

	protected static boolean isServiceDeployed(Component comp,
			String inServiceName) {

		ServiceConfiguration conf = getServiceConfiguration(comp, inServiceName);
		if (conf != null && conf.isActive())
			return true;
		return false;
	}

	/**
	 * @param args expects a path name to the component jar
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out
					.println("Usage: java com.adobe.livecycle.samples.foundationAPIDeployComponent [properties-file] [component-to-deploy]\n");
			System.out
					.println("   - properties-file: Properties file containing configuration required to connect to the LiveCycle Server");
			System.out
					.println("   - component-to-deploy: the full path to the component jar file to deploy\n");
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

			// get the component jar file
			String compJar = "";
			if (args.length > 1)
				compJar = args[1];

			// check the file exists
			file = new File(compJar);
			if (!file.exists()) {
				System.out.println("Error: Component jar file not found ["
						+ file.getPath() + "]");
				return;
			}

			init(propertiesFile);

			try {
				/* Install the Component */
				Document _archiveDocument = new Document(new File(args[1]),
						false);
				Component _comp = _cr.install(_archiveDocument);

				/* Start the Component if necessary */
				if (_comp.getState() == Component.STOPPED)
					_comp = _cr.start(_comp);

				Element[] elements = _comp.getServiceDescriptors();

				for (int j = 0; j < elements.length; j++) {
					if (elements[j].hasAttribute("name")) {
						String serviceName = elements[j].getAttribute("name");
						// Some Services are automatically installed and started.
						if (!isServiceDeployed(_comp, serviceName)) {
							ServiceConfiguration _ds = null;

							System.out.println("Creating and Staring Service "
									+ serviceName);
							_ds = _sr
									.createAndDeploy(
											_comp,
											serviceName,
											0,
											0,
											DOMUtil
													.toString(_comp
															.getServiceDescriptor(serviceName)),
											null);

							if (_ds != null) {
								_sr.start(_ds);
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			System.out.println("done deploying " + args[1]);
		}
	}
}
