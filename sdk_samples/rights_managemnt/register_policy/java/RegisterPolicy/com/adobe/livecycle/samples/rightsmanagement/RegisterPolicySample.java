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

import java.util.Properties;

import com.adobe.edc.common.CommonException;
import com.adobe.edc.sdk.SDKException;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.idp.um.api.DirectoryManager;
import com.adobe.idp.um.api.UMConstants;
import com.adobe.idp.um.api.UMException;
import com.adobe.idp.um.api.infomodel.Principal;
import com.adobe.livecycle.rightsmanagement.client.PolicyManager;
import com.adobe.livecycle.rightsmanagement.client.RightsManagementClient;
import com.adobe.livecycle.rightsmanagement.client.infomodel.InfomodelObjectFactory;
import com.adobe.livecycle.rightsmanagement.client.infomodel.PDRLException;
import com.adobe.livecycle.rightsmanagement.client.infomodel.Permission;
import com.adobe.livecycle.rightsmanagement.client.infomodel.Policy;
import com.adobe.livecycle.rightsmanagement.client.infomodel.PolicyEntry;
import com.adobe.livecycle.rightsmanagement.client.infomodel.ValidityPeriod;
import com.adobe.livecycle.usermanager.client.DirectoryManagerServiceClient;

/**
 * @author Adobe Systems Incorporated
 * RegisterPolicySample:  demonstrates how to create a policy using the Rights Management Client SDK.
 */
public class RegisterPolicySample
{
	public final static String SAMPLE_POLICY_NAME 		 = "A Sample Policy";
	public final static String SAMPLE_POLICY_DESCRIPTION = "This is a sample of the LiveCycle Rights Management SDK.";
	
	private static String _user_name       = null;
	private static String _password        = null;
	private static String _server_url      = null;
	private static String _server_type     = null;
	private static String _connection_type = null;
		
	private static Properties			  _scf_props   = null;
	private static RightsManagementClient _rms_client  = null;
	private static ServiceClientFactory   _scf_factory = null;
	private static DirectoryManager 	  _dir_manager = null;
	
	private static void initializeClientConnections()
	{
		_scf_factory = ServiceClientFactory.createInstance(_scf_props);
		_rms_client  = new RightsManagementClient(_scf_factory);
		_dir_manager = new DirectoryManagerServiceClient(_scf_factory);
	}
	
	private static PolicyEntry createSamplePolicyEntry( Principal principal, int[] permissions )  throws PDRLException, SDKException
	{
		PolicyEntry ret_val = null;
		
		ret_val = InfomodelObjectFactory.createPolicyEntry();
		if( ret_val == null )
		{
			return null;
		}
			
		ret_val.setPrincipal(principal);
		for ( int i = 0; i < permissions.length; i++ )
		{
			Permission current_permission = InfomodelObjectFactory.createPermission(permissions[i]);
			ret_val.addPermission(current_permission);
		}					
		
		return ret_val;
	}
	
	static Policy makeSamplePolicy() throws CommonException, PDRLException, SDKException, UMException
	{
		Policy out_policy = InfomodelObjectFactory.createPolicy();
		
		final int[] PUBLISHER_PERMISSIONS = 
		{ 
		  Permission.OPEN_ONLINE, 
		  Permission.OPEN_OFFLINE, 
		  Permission.COPY, 
		  Permission.POLICY_SWITCH,
		  Permission.REVOKE,
		  Permission.CHANGES_ALL_EXCEPT_EXTRACT_CONTENT, 
		  Permission.PRINT_HIGH 
		};
		  
		final int[] USER_PERMISSIONS = 
		{ 
		  Permission.OPEN_ONLINE, 
		  Permission.OPEN_OFFLINE, 
		  Permission.CHANGES_COMMENTING_FORM_FILL_IN_AND_SIGNING 
		};

		//  Create policy entries for the publisher and the "all users" group using the above permission sets.
		Principal publisherPrincipal = InfomodelObjectFactory.createSpecialPrincipal(InfomodelObjectFactory.PUBLISHER_PRINCIPAL);
		Principal allPrincipalsGroup = _dir_manager.findPrincipal(UMConstants.SpecialDefaultPrincipals.DOMAIN_DEFAULT, UMConstants.SpecialDefaultPrincipals.GROUP_ALLPRINCIPALS);

		out_policy.addPolicyEntry(createSamplePolicyEntry(publisherPrincipal, PUBLISHER_PERMISSIONS));
		out_policy.addPolicyEntry(createSamplePolicyEntry(allPrincipalsGroup, USER_PERMISSIONS));
		
		//  Set the basic information about the policy
		
		out_policy.setName(SAMPLE_POLICY_NAME);
		out_policy.setPolicySetName(null);
		out_policy.setDescription(SAMPLE_POLICY_DESCRIPTION);
		out_policy.setOfflineLeasePeriod(31);
		out_policy.setTracked(true);		
		
		//  Set a validity period for the policy
		
		ValidityPeriod validity_period = InfomodelObjectFactory.createValidityPeriod();
		validity_period.setRelativeExpirationDays(20);
		out_policy.setValidityPeriod(validity_period);
	
		return out_policy;
	}
	
	private static void registerSamplePolicy()
	{
		PolicyManager policy_manager = _rms_client.getPolicyManager();
		
		try
		{	
			policy_manager.registerPolicy(makeSamplePolicy());
			
			System.out.println( "Success!  Created policy: " + SAMPLE_POLICY_NAME );
		}
		catch(Exception e)
		{
			
			System.out.println("An exception occurred while registering the sample policy: " + e.getMessage());
		}
	}
	
	private static void initializeConnectionParameters(String[] args) throws IllegalArgumentException 
	{
		_server_url      = args[0];
		_user_name       = args[1];
		_password        = args[2];
		_server_type     = args[3];
		_connection_type = args.length <= 4 ? "SOAP" : args[4];				
		
		if( !_connection_type.equalsIgnoreCase("SOAP") && 
			!_connection_type.equalsIgnoreCase("EJB") )
		{
			throw new IllegalArgumentException("The 'connection type' parameter may only be 'SOAP' or 'EJB'");
		}
	}
	
	private static void initializeSCFProperties()
	{
		_scf_props = new Properties();
		
		_scf_props.setProperty( ServiceClientFactoryProperties.DSC_CREDENTIAL_USERNAME, _user_name );
		_scf_props.setProperty( ServiceClientFactoryProperties.DSC_CREDENTIAL_PASSWORD, _password );
		
		if(_connection_type.equalsIgnoreCase("SOAP"))
		{
			_scf_props.setProperty( ServiceClientFactoryProperties.DSC_SERVER_TYPE, _server_type );
			_scf_props.setProperty( ServiceClientFactoryProperties.DSC_DEFAULT_SOAP_ENDPOINT, _server_url );
			_scf_props.setProperty( ServiceClientFactoryProperties.DSC_TRANSPORT_PROTOCOL, "SOAP" );
		}
		else if(_connection_type.equalsIgnoreCase("EJB"))
		{
			_scf_props.setProperty( ServiceClientFactoryProperties.DSC_SERVER_TYPE, _server_type );
			_scf_props.setProperty( ServiceClientFactoryProperties.DSC_DEFAULT_EJB_ENDPOINT, _server_url );
			_scf_props.setProperty( ServiceClientFactoryProperties.DSC_TRANSPORT_PROTOCOL, "EJB" );
						
			//  NOTE:  these environment parameters should be specified at the command line with -D for JBoss EJB 
			//  Different environment parameters may be necessary for other app servers.
			//
			//_scf_props.setProperty( "java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory" );
			//_scf_props.setProperty( "java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces" );
		}
	}
	
	private static void printUsage()
	{
		System.out.println( "RegisterPolicySample usage:");
		System.out.println( "RegisterPolicySample <server url> <user name> <password> <server type> [connection type]");
		System.out.println( "  where 'server type' is the type of server (JBoss |Websphere |WebLogic), and 'connection type' optionally specifies SOAP or EJB.  SOAP is the default");
	}
	
	/**
	 * @param args
	 * Expects four arguments in the args array:  URL, username, password, and server type
	 */
	public static void main(String[] args) 
	{
		try
		{		
			//  Initialize the connection parameters from our arguments
			
			if(args.length < 4 || args.length > 5)
			{
				printUsage();
				
				return;
			}
			
			initializeConnectionParameters(args);
			initializeSCFProperties();
			initializeClientConnections();
			registerSamplePolicy();							
		}
		catch(IllegalArgumentException e)
		{
			System.out.println("One or more parameters to the sample was incorrect:");
			System.out.println(e.getMessage());
			
			printUsage();
		}
	}
}
