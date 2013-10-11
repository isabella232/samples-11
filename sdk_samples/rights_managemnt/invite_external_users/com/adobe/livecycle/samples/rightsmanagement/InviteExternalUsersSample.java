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

import com.adobe.edc.server.spi.ersp.*;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.um.api.*;
import com.adobe.idp.um.api.infomodel.*;
import com.adobe.idp.um.api.impl.UMBaseLibrary;
import com.adobe.livecycle.usermanager.client.DirectoryManagerServiceClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InviteExternalUsersSample implements InvitedUserProvider
{
    private ServiceClientFactory _factory = null;

    private User createLocalPrincipalAccount(String email_address) throws Exception
    {
	String ret = null;

	//  Assume the local domain already exists!
	String domain = "EDC_EXTERNAL_REGISTERED";
		
	List aliases = new ArrayList();
	aliases.add( email_address );
		
	User local_user = UMBaseLibrary.createUser( email_address, domain, email_address );
	local_user.setCommonName( email_address );
	local_user.setEmail( email_address );
	local_user.setEmailAliases( aliases );
		
	//  You may wish to disable the local user until, for example, his registration is processed by a confirmation link
	//local_user.setDisabled( true );

	DirectoryManager directory_manager = new DirectoryManagerServiceClient( _factory );
	String ret_oid = directory_manager.createLocalUser( local_user, null );
	
	if( ret_oid == null )
	{
	    throw new Exception( "FAILED TO CREATE PRINCIPAL FOR EMAIL ADDRESS: " + email_address );
	}

	return local_user;
    }

    protected User[] createUsers( List emails ) throws Exception
    {
	ArrayList ret_users = new ArrayList();

	_factory = ServiceClientFactory.createInstance();

	Iterator iter = emails.iterator();

	while( iter.hasNext() )
	{
	    String current_email = (String)iter.next();

	    ret_users.add( createLocalPrincipalAccount( current_email ) );
	}

	return (User[])ret_users.toArray( new User[0] );
    }

    protected void doInvitations(List emails)
    {
	//  Here you may choose to send the users who were created an invitation email
	//  This step is completely optional, depending on your requirements.  
    }

    public InvitedUserProviderResult[] invitedUser(List emails)
    {
	//  This sample demonstrates the workflow for inviting a user via email

	try
	{

	    User[] principals = createUsers(emails);

	    InvitedUserProviderResult[] result = new InvitedUserProviderResult[principals.length];
	    for( int i = 0; i < principals.length; i++ )
	    {
		result[i] = new InvitedUserProviderResult();

		result[i].setEmail( (String)emails.get( i ) );
		result[i].setUser( principals[i] );
	    }

	    doInvitations(emails);

	    System.out.println( "SUCCESSFULLY INVITED " + result.length + " USERS" );

	    return result;

	}
	catch( Exception e )
	{
	    System.out.println( "FAILED TO INVITE USERS FOR INVITE USERS SAMPLE" );
	    e.printStackTrace();

	    return new InvitedUserProviderResult[0];
	}
    }
    public static void main(String args[])
    {
    	System.out.println("InviteExternalUsersSample Usage:Refer to online help for this sample to build and depoy this sample jar on server.");
    }
    
}
