package com.adobe.livecycle.samples.centralmigrationbridge.client;

import java.util.HashMap;
import java.util.Map;

import com.adobe.idp.Document;
import com.adobe.idp.dsc.DSCException;
import com.adobe.idp.dsc.InvocationRequest;
import com.adobe.idp.dsc.InvocationResponse;
import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.livecycle.samples.centralmigrationbridge.exception.DataAccessSampleException;

public class DataAccessSampleServiceClient {
	private ServiceClientFactory m_ServiceClientFactory = null;
	
	private final String SERVICE_NAME = "DataAccessSample";
	
	private boolean synchronous = true;
	
	/**
	 * For internal use only.
	 */
	
	public boolean isSynchronous() 
	{
		return synchronous;
	}

	
	/**
	 * Sets the Output central service to be invoked synchronously. 
	 * A synchronous request is blocked until the Output central service finishes processing the request and returns 
	 * the result back to the caller. 
	 * @param synchronous A Boolean value that specifies whether the Output central service is invoked synchronously.
	 */
	public void setSynchronous(boolean synchronous) 
	{
		this.synchronous = synchronous;
	}
	
	/**
	 * A constructor that creates an <code>OutputCentralClient</code> object.
	 * @param serviceClientFactory A <code>ServiceClientFactory</code> object that contains 
	 * connection properties. 
	 * For information about setting connection properties, see <i>Invoking LiveCycle Services</i>. 
	 */
	public DataAccessSampleServiceClient(ServiceClientFactory serviceClientFactory)
	{
		m_ServiceClientFactory = serviceClientFactory;
	}
	

	
	/**
	 * 
	 * @param inDataDoc - Dat file document
	 * @param maxBytesToProcess 
	 * @return
	 * @throws DataAccessSampleException
	 * @throws DSCException
	 */
	
	public Document centralDataAccess(Document inData, int maxBytesToProcess)throws DataAccessSampleException, DSCException {
		Map oInput = new HashMap();
		oInput.put("inDataDoc", inData);
		oInput.put("inMaxBytesToProcess", maxBytesToProcess);

		try
		{
			InvocationResponse oResponse = invokeRequest("centralDataAccess", oInput);
			return (Document)oResponse.getOutputParameter("outXMLDoc");
		}
		catch(DSCException e)
		{
			if(e.getCause() instanceof DataAccessSampleException)
			{
				DataAccessSampleException oe = (DataAccessSampleException)e.getCause();
				throw oe;
			}
			else
			{
				throw e;
			}
		}
		catch(Exception e)
		{
			throw new DataAccessSampleException(e);
		}
	
	}

	private InvocationResponse invokeRequest(String sOperationName, Map oInput) throws DSCException
	{
		InvocationRequest oRequest = 
			m_ServiceClientFactory.createInvocationRequest(
					SERVICE_NAME,
					sOperationName,
					oInput,
					synchronous);
		return m_ServiceClientFactory.getServiceClient().invoke(oRequest);
	}

}

