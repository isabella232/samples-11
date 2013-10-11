/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2008 Adobe Systems Incorporated
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
**************************************************************************/
package com.adobe.livecycle.samples.centralmigrationbridge.service;

import java.util.logging.Level;

import com.adobe.idp.dsc.component.Bootstrap;
import com.adobe.idp.dsc.component.BootstrapContext;
import com.adobe.logging.AdobeLogger;

public class DataAccessSampleBootstrapImpl implements Bootstrap {

	private static final AdobeLogger mLogger = AdobeLogger.getAdobeLogger(DataAccessSampleBootstrapImpl.class);

	private BootstrapContext mBootstrapContext;

 

	public void setBootstrapContext(BootstrapContext aCtx) {

		mLogger.log(Level.INFO, "Set bootstrap context: " + aCtx.getComponent().getComponentId());

		mBootstrapContext = aCtx;	

	}

 

	//Invoked when the component is uninstalled

	public void onUnInstall() {

		mLogger.log(Level.INFO, "Called onUnInstall: " + mBootstrapContext.getComponent().getComponentId());

	}

 

	//Invoked when the component is installed

	public void onInstall() {

		mLogger.log(Level.INFO, "Called onInstall: " + mBootstrapContext.getComponent().getComponentId());

	}


}
