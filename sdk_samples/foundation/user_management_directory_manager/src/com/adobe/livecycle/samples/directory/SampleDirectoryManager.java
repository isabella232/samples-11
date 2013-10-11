/*
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2007 Adobe Systems Incorporated
 * All Rights Reserved
 *  
 * NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the 
 * terms of the Adobe license agreement accompanying it. If you have received this file from a 
 * source other than Adobe, then your use, modification, or distribution of it requires the prior 
 * written permission of Adobe.
 */

package com.adobe.livecycle.samples.directory;

import com.adobe.idp.common.errors.exception.IDPException;
import com.adobe.idp.um.api.UMConstants;
import com.adobe.idp.um.spi.directoryservices.DSGroupContainmentRecord;
import com.adobe.idp.um.spi.directoryservices.DSPrincipalCollection;
import com.adobe.idp.um.spi.directoryservices.DSPrincipalIdRecord;
import com.adobe.idp.um.spi.directoryservices.DirectoryGroupProvider;
import com.adobe.idp.um.spi.directoryservices.DirectoryProviderConfig;
import com.adobe.idp.um.spi.directoryservices.DirectoryUserProvider;
import com.adobe.livecycle.samples.directory.provider.DirectoryController;
import com.adobe.livecycle.samples.directory.provider.SampleStateTracker;

/**
 * This class provides a sample implementation of User and Group provider SPIs. This sample would 
 * send 10 batches of 10 users each and 3 batches of 10 groups each for synchronization. Each group
 * would contain 10 randomly selected Users.		 
 */

public class SampleDirectoryManager implements DirectoryUserProvider,
		DirectoryGroupProvider {

	private static final int TOTAL_USER_BATCHES = 10;
	private static final int TOTAL_USERS_IN_A_BATCH = 10;
	private static final int TOTAL_USERS = TOTAL_USERS_IN_A_BATCH * TOTAL_USER_BATCHES;
	private static final int SAMPLE_USER_SIZE = (TOTAL_USERS*11)/10;
	

	private static final int TOTAL_GROUPS_IN_A_BATCH = 10;
	private static final int TOTAL_GROUP_BATCHES = 3;
	
	private SampleStateTracker initializeState(String principalType) {
		SampleStateTracker state = null;
		if (UMConstants.PrincipalTypes.PRINCIPALTYPE_USER.equals(principalType)) {
			state = new SampleStateTracker(0, TOTAL_USER_BATCHES, TOTAL_USERS_IN_A_BATCH);
		} else {
			state = new SampleStateTracker(0, TOTAL_GROUP_BATCHES, TOTAL_GROUPS_IN_A_BATCH);
		}
		return state;
	}

	/* This method is used to obtain all the Users and Groups from any directory. The config object passed to this 
	 * method would determine whether to return Users or groups. If config.getUserConfig() is not null then users would
	 * be returned and if config.getGroupConfig() is not null then groups would be returned.
	 */
	
	public DSPrincipalCollection getPrincipals(DirectoryProviderConfig config,
			Object state) throws IDPException {
		
		if (config.getUserConfig() == null && config.getGroupConfig() == null)  {
			return null;
		}
		
		String principalType = UMConstants.PrincipalTypes.PRINCIPALTYPE_USER;

		if (config.getGroupConfig()  != null) {
			principalType = UMConstants.PrincipalTypes.PRINCIPALTYPE_GROUP;
		}
		SampleStateTracker stateTracker = null;
		if (state != null) {
			stateTracker = (SampleStateTracker)state;
		} else {
			stateTracker = initializeState(principalType);
		}

		DirectoryController directoryController = new DirectoryController(stateTracker, principalType, config.getDomain());
		return directoryController.getPrincipalsBatch();
	}

	/*
	 * Once all the users & groups have been retrieved by calling getPrincipals, LiveCycle will call this method 
	 * to obtain user-group associations. Here it just need to pass all those users which belong to the group 
	 * being sent here.
	 */
	public DSGroupContainmentRecord getGroupMembers(
			DirectoryProviderConfig config, DSPrincipalIdRecord group)
			throws IDPException {

		System.out.println("Sending Members of Group : " + group.getCanonicalName());
		DSGroupContainmentRecord dsGroupContainmentRecord = DirectoryController.getGroupMembers(group, SAMPLE_USER_SIZE);
		return dsGroupContainmentRecord;
	}

	/*
	 * This is a utility method that could be used to validate the settings of
	 * the custom directory whether it's running as expected or not.
	 */

	public boolean testConfiguration(DirectoryProviderConfig config) {
		return DirectoryController.testConfiguration(config);

	}

}
