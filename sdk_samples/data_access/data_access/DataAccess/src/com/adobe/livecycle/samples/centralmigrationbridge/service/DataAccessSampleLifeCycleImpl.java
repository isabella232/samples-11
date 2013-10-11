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

import com.adobe.idp.dsc.component.ComponentContext;
import com.adobe.idp.dsc.component.LifeCycle;
import com.adobe.logging.AdobeLogger;

public class DataAccessSampleLifeCycleImpl implements LifeCycle {

	private static final AdobeLogger mLogger = AdobeLogger.getAdobeLogger(DataAccessSampleLifeCycleImpl.class);

	private ComponentContext mComponentContext;

	// Sets the component s context

	public void setComponentContext(ComponentContext aContext) {

		if (mLogger.isLoggable(Level.FINE)) {

			mLogger.log(Level.FINE, "setComponentContext: "	+ aContext.getComponent().getComponentId());

		}

		mComponentContext = aContext;

	}

	// Invoked when the component is started

	public void onStart() {

		mLogger.log(Level.INFO, "Called onStart: " + mComponentContext.getComponent().getComponentId());

	}

	// Invoked when the component is stopped

	public void onStop() {

		mLogger.log(Level.INFO, "Called onStop: " + mComponentContext.getComponent().getComponentId());

	}

}
