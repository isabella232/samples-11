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

package com.adobe.livecycle.samples.authentication.provider;

import com.adobe.idp.um.spi.authentication.AuthResponse;
import com.adobe.idp.um.spi.authentication.AuthResponseImpl;
import com.adobe.idp.um.spi.authentication.AuthScheme;

/**
 * This class does a simple authentication for users in a domain. It will act as
 * a Controller interacting with third party authentication logic. 
 * 
 * @since Feb 20, 2007
 */
public class AuthenticationController {

	/**
	 * This method returns an object of type
	 * com.adobe.idp.um.spi.authentication.AuthResponse, which contains the
	 * information about the success/failure of the authentication operation.
	 * @param userName the loginId 
	 * @param password password
	 * @param domainName  domainName within which the authentication has to be done. Note this is the Domain name as 
	 * defined in User Management 
	 */
	public AuthResponse doAuth(String userName, String password,String domainName) {
		AuthResponse response = new AuthResponseImpl();
		response.setAuthType(AuthScheme.AUTHTYPE_USERNAME_PWD);
		response.setUsername(userName);

		//Sanity checks
		if (isEmpty(userName) || isEmpty(password)) {
			System.out.println("User Name or Password is null");
			response.setErrorMessage("Sorry!! User Name and Password could not be empty! Please provide User Name & Password!");
			return response;
		}

		//Specify the domainName for which this response is being returned
		response.setDomain(domainName);
		
		if (isValidUser(userName, password)) {
			response.setAuthStatus(AuthResponse.AUTH_SUCCESS);
			System.out.println("User " + userName + " is valid !!!");
		} else {
			response.setAuthStatus(AuthResponse.AUTH_FAILED);
			System.out.println("User " + userName + " is not a valid user!!!");
		}
		return response;
	}
	
	private boolean isEmpty(String s){
		return s == null || s.trim().length() == 0;
	}

	/**
	 * The method provides one of the simpler way to authenticate a user. It
	 * will return TRUE if username and password are reverse of each other.
	 */
	private boolean isValidUser(String userName, String password) {
		boolean valid = false;
		if (userName.length() == password.length()) {
			int i;
			for (i = 0; i < userName.length()
					&& (userName.charAt(i) == password.charAt(password.length()
							- i - 1)); i++)
				;
			if (i == userName.length())
				valid = true;
		}
		return valid;
	}
}
