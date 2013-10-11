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

import com.adobe.idp.Document;
import com.adobe.livecycle.samples.centralmigrationbridge.dataaccess.OCSDataAccess;
import com.adobe.livecycle.samples.centralmigrationbridge.exception.DataAccessSampleException;
import com.adobe.logging.AdobeLogger;

public class DataAccessSampleServiceImpl implements
		DataAccessSampleService {

	//members correspond to configuration values
	protected String mCentralInstallDir;

	AdobeLogger mLogger = AdobeLogger.getAdobeLogger(this.getClass());

	public Document centralDataAccess(Document inData, int maxBytesToProcess)throws DataAccessSampleException {
		OCSDataAccess odaObj = new OCSDataAccess(maxBytesToProcess);
		return odaObj.convertToXml(inData);
	}
}
