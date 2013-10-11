/*
 ADOBE SYSTEMS INCORPORATED
 Copyright 2007 Adobe Systems Incorporated
 All Rights Reserved
 
 NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the 
 terms of the Adobe license agreement accompanying it.  If you have received this file from a 
 source other than Adobe, then your use, modification, or distribution of it requires the prior 
 written permission of Adobe.
*/

package com.adobe.livecycle.samples.rightsmanagement;

import com.adobe.idp.dsc.component.Bootstrap;
import com.adobe.idp.dsc.component.BootstrapContext;

public class BootstrapImpl implements Bootstrap {

	private BootstrapContext m_ctx;

	public void setBootstrapContext(BootstrapContext aCtx) {
		System.out.println("Set bootstrap context: "
				+ aCtx.getComponent().getComponentId());
		m_ctx = aCtx;
	}

	public void onUnInstall() {
		System.out.println("Called onUnInstall: "
				+ m_ctx.getComponent().getComponentId());

	}

	public void onInstall() {
		System.out.println("Called onInstall: "
				+ m_ctx.getComponent().getComponentId());

	}
}
