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

package com.adobe.livecycle.samples.authentication;

import java.util.List;
import java.util.Map;

import com.adobe.idp.um.spi.authentication.AuthConfigBO;
import com.adobe.idp.um.spi.authentication.AuthProvider;
import com.adobe.idp.um.spi.authentication.AuthResponse;
import com.adobe.idp.um.spi.authentication.AuthScheme;
import com.adobe.livecycle.samples.authentication.provider.AuthenticationController;

/**
 * This class is a sample implementation of the AuthProvider SPI. The authentication logic
 * used in this provider is quite simple. If the password supplied is reverse of the userid
 * passed then authentication is successful otherwise it fails.
 */

public class SampleAuthenticationManager implements AuthProvider {

	private static AuthenticationController sampleAuthentication = new AuthenticationController();

	public AuthResponse authenticate(Map credential, List authConfigs) {
		String	authType = (String) credential.get(AuthProvider.AUTH_TYPE);

		//This implementation only deals with username/password based authentication. There are other types of AuthSchemes
		//also for which an AuthProvider can be implemented
		if (!AuthScheme.AUTHTYPE_USERNAME_PWD.equals(authType)){
			return null;
		}
		AuthResponse authResponse = null;

		// check the credential map to find out what the credentials are
		String	userName = (String) credential.get(AuthProvider.USER_NAME);
		String	password = (String) credential.get(AuthProvider.PASSWORD);
		
		for (int i = 0;i < authConfigs.size();i++) {
			AuthConfigBO authConfigBO = (AuthConfigBO)authConfigs.get(i);
			System.out.println(authConfigBO.getCustomConfiguration());
			authResponse = sampleAuthentication.doAuth(userName, password, authConfigBO.getDomainName());
			if (AuthResponse.AUTH_SUCCESS.equals(authResponse.getAuthStatus())) {
				return authResponse;
			}
		}
		return authResponse;
	}

	/**
	 * This method returns the config name of this Provider. This name would be the configuration node whose first-level 
	 * children are extracted from the configuration file.
	 */
	public String getConfigName() {
		return "SampleAuthenticationManager";
	}

}
