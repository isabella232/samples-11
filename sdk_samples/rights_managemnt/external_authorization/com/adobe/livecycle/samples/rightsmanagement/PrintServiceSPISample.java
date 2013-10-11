package com.adobe.livecycle.samples.rightsmanagement;

import com.adobe.edc.server.spi.authorization.*;
import com.adobe.idp.Context;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.livecycle.rightsmanagement.client.RightsManagementClient;
import com.adobe.livecycle.rightsmanagement.client.EventManager;
import com.adobe.livecycle.rightsmanagement.client.infomodel.Event;
import com.adobe.livecycle.rightsmanagement.client.infomodel.EventSearchFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *  PrintServiceSPISample:  allows a given document to be printed only once.
 */

public class PrintServiceSPISample implements ExternalAuthorizer
{
    private EventManager	       _evt_manager = null;
    private ServiceClientFactory   _sc_factory  = null;
    private RightsManagementClient _rm_client   = null;
	
    public ExternalAuthPropertyDTO[] getProviderProperties()
    {
	return new ExternalAuthPropertyDTO[0];
    }
	
    public ExternalAuthResultDTO evaluate( ExternalAuthDTO auth_info )
    {
	ExternalAuthResultDTO ret_val = new ExternalAuthResultDTO();
		
	//  If this is a document registration, we have no need to run
	//  the SPI further.
		
	if( auth_info.getOperationType().isOperationSecureDocument() )
	{
	    ret_val.setPermissions( auth_info.getPermissions() );
			
	    return ret_val;
	}
		
	//  We are viewing the document -- so if it has been printed, we 
	//  want to ensure that the permissions we return do not include
	//  the print permission.
		
	System.out.println( this.getClass().getName() + ": --- VIEWING DOCUMENT WITH LICENSE ID " + auth_info.getLicenseId() );
		
	try
	{
	    initializeRightsManagementAPI();
			
	    Context context = (Context)auth_info.getContext();
	    String auth_user_id = context.getAuthenticatedUser().getOid();
		
	    //  If the document was printed, then reset the permissions that are returned to the server
 	    //  so they do not include print.
	
	    if( hasDocumentBeenPrinted( auth_info.getLicenseId(), auth_user_id ) )
	    {
		System.out.println( this.getClass().getName() + ": --- REMOVING PRINT PERMISSION FOR DOCUMENT WITH LICENSE ID " + auth_info.getLicenseId() );
				
		ArrayList final_permissions = removePrintFromPermissions( auth_info.getPermissions() );
				
		ret_val.setPermissions(final_permissions);
	    }			
	    else
	    {
		ret_val.setPermissions( auth_info.getPermissions() );
	    }
	}
	catch( Exception e )
	{
	    ret_val.setPermissions( auth_info.getPermissions() );			
	}
		
	return ret_val;
    }
	
    private ArrayList removePrintFromPermissions( ArrayList permissions_list )
    {
	ArrayList ret_val = new ArrayList();
		
	for( int i = 0; i < permissions_list.size(); i++ )
	{
	    String current_permission = (String)permissions_list.get(i);
			
	    if( !( current_permission.indexOf( "print" ) >= 0 ) )
	    {
		ret_val.add( current_permission );
	    }
	}
		
	return ret_val;
    }
	
    private boolean hasDocumentBeenPrinted( String license_id, String user_id )
    {
	try
	{		
	    EventSearchFilter print_search = new EventSearchFilter(); 
		
	    //  Configure an event search for the print event.
	
	    print_search.setDocumentId( license_id );
	    print_search.setEventNamespace( "com.adobe.edc.documentevent" );
	    print_search.setEventName( "Print High Resolution" );
	    print_search.setFirstTime( new Date( 100, 1, 1 ) );
	    print_search.setLastTime( new Date( ) );
	    print_search.setWasAllowed( true );
	    print_search.setUserOid( user_id );
			
	    System.out.println( "SEARCHING FOR PRINT EVENTS FOR DOCUMENT " + license_id + " AND USER " + user_id );

	    //  Use the Rights Management Client SDK API to find print events for this document.

	    Event[] out_events = _evt_manager.searchForEvents( print_search, 10 );

	    boolean was_printed = false;
			
	    if( out_events != null )
	    {
		int n_print_events = out_events.length;

		was_printed = ( n_print_events > 0 );
				
		if( was_printed )
		{
		    System.out.println( "DOCUMENT WAS PRINTED: " + n_print_events  + " TIMES" );
		}
	    }
			
	    return was_printed;
	}
	catch( Exception e )
	{
	    e.printStackTrace();
	    
	    return false;
	}
    }
	
    private void initializeRightsManagementAPI() throws Exception
    {
	try
	{	
	    _sc_factory  = ServiceClientFactory.createInstance();
	    _rm_client   = new RightsManagementClient( _sc_factory );		
	    _evt_manager = _rm_client.getEventManager();
	}
	catch( Exception e )
	{
	    System.out.println( this.getClass().getName() + ": --- failed to initialize Rights Management API" );				
			
	    throw e;			
	}		
    }
    public static void main(String args[])
    {
    	System.out.println("InviteExternalUsersSample Usage:Refer to online help for this sample to build and depoy sample jar on server.");
    }
}
