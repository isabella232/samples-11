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
