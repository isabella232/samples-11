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
package com.adobe.livecycle.samples.centralmigrationbridge.exception;


public class DataAccessSampleException  extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1462052444118192935L;

	public DataAccessSampleException(String msg) {
		super(msg);
	}

	public DataAccessSampleException(Exception e) {
		super(e);
	}

	public DataAccessSampleException(Throwable e) {
		super(e);
	}
	
	
}
