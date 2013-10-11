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

package com.adobe.livecycle.samples.directory.provider;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.adobe.idp.common.errors.exception.IDPException;
import com.adobe.idp.um.api.UMConstants;
import com.adobe.idp.um.spi.directoryservices.DSGroupContainmentRecord;
import com.adobe.idp.um.spi.directoryservices.DSPrincipalCollection;
import com.adobe.idp.um.spi.directoryservices.DSPrincipalIdRecord;
import com.adobe.idp.um.spi.directoryservices.DSPrincipalRecord;
import com.adobe.idp.um.spi.directoryservices.DirectoryProviderConfig;

/**
 * This class is an intermediary module which will talk to directory underneath
 * to fetch users and groups in a domain. Hence, it will act as a Controller
 * interacting with custom directories.
 */

public class DirectoryController {

	private SampleStateTracker state;
	private String principalType;
	private String domainName;

	public DirectoryController(SampleStateTracker state, String principalType, String domainName) {
		super();
		this.state = state;
		this.principalType = principalType;
		this.domainName = domainName;
	}

	/**
	 * This method returns a batch of principals (users or groups) from the directory. 
	 *  
	 * @return DSPrincipalCollection
	 */

	public DSPrincipalCollection getPrincipalsBatch() {
		if (state.getCurrentBatchNo() >= state.getTotalBatches()) {
			return null;
		}
		DSPrincipalCollection collection = new DSPrincipalCollection();
		List<Integer> principalIdList = state.getCurrentBatchList();
		for (int i = 0;i < principalIdList.size();i++) {
			DSPrincipalRecord dspr = getPrincipalRecord(principalIdList.get(i));
			collection.addDSPrincipalRecord(dspr);
		}
		state.setCurrentBatchNo(state.getCurrentBatchNo() + 1);
		collection.setState(state);
		return collection;
	}
	
	private DSPrincipalRecord getPrincipalRecord(int id) {
		
		DSPrincipalRecord record = new DSPrincipalRecord();
		record.setDomainName(domainName);
		record.setPrincipalType(principalType);
		record.setDisabled(false);
		
		record.setCanonicalName("CanonicalName " + principalType + id);
		if (UMConstants.PrincipalTypes.PRINCIPALTYPE_USER.equals(principalType)) {
			record.setUserid("userid" + id);
		}

		record.setCommonName("CommonName " + principalType + id);
		record.setDescription("Description " + id);
		record.setDomainName(domainName);
		String email = "mailMe" + id + "@" + domainName + ".com";
		record.setEmail(email);
		record.setEmailAliases(Arrays.asList(new String[] { "alias1." + email, "alias2." + email,
				"alias3." + email, "alias4." + email}));
		record.setFamilyName("FamilyName" + id);
		record.setGivenName("GivenName" + id);
		record.setGroupType(DSPrincipalRecord.GROUPTYPE_PRINCIPALS);
		record.setInitials("Mr");
		record.setLocale("en-US");
		record.setOrg("Org" + id);
		record.setOriginalName("OriginalName " + principalType + id);
		record.setPostalAddress("PostalAddress" + id);
		record.setTelephoneNumber("987654321");
		record.setTimezone("GMT");
		record.setVisibility(2);

		return record;
	}

	/**
	 * This method will return all the users which belong to a particular group. The group members are 
	 * formed by randomly selecting users.
	 * 
	 * @param DSPrincipalIdRecord group
	 * @param int maxLimit
	 * @return
	 * @throws IDPException
	 */
	public static DSGroupContainmentRecord getGroupMembers(DSPrincipalIdRecord group, int maxLimit)
			throws IDPException {
		DSGroupContainmentRecord record = new DSGroupContainmentRecord();
		record.setDomainName(group.getDomainName());
		record.setCanonicalName(group.getCanonicalName());
		// Randomly select 10 users
		Random random = new Random();
		for (int i = 0;i < 10;i++) {
			int id = random.nextInt(maxLimit);
			DSPrincipalIdRecord member = new DSPrincipalIdRecord();
			member.setDomainName(group.getDomainName());
			member.setCanonicalName("CanonicalName " + UMConstants.PrincipalTypes.PRINCIPALTYPE_USER + id);
			record.addPrincipalMember(member);
		}
		return record;

	}

	/**
	 * This method can be used to validate the settings of the custom directory
	 * whether it's running as expected or not
	 * 
	 * @param DirectoryProviderConfig
	 *            config
	 * @return boolean
	 */
	public static boolean testConfiguration(DirectoryProviderConfig config) {
		System.out.println("Testing configuration unimplemented");
		return true;
	}

}
