package com.adobe.livecycle.samples.centralmigrationbridge.dataaccess;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import com.adobe.idp.Document;
import com.adobe.livecycle.samples.centralmigrationbridge.exception.DataAccessSampleException;

public class OCSDataAccess {
	
	
	private static final String HAT_FIELD_REGEX="^\\Q^\\E(field)(\\p{Space})(\\p{Graph}+)";
	private static final String HAT_GLOBAL_REGEX="^\\Q^\\E(global)(\\p{Space})(\\p{Graph}+)";
	private static final String HAT_FORM_REGEX="^\\Q^\\E(form)(\\p{Space})(\\p{Graph}+)";
	private static final String HAT_JOB_REGEX="^\\Q^\\E(job)(\\p{Space})(\\p{Graph}+)";
	
	//container nodes
	
	private static final String JOBS_NODE="jobs";
	private static final String FORMS_NODE="forms";
	private static final String FIELDS_NODE="fields";
	private static final String GLOBALS_NODE="globals";
	
	private static final String FORM_NODE="form";
	private static final String JOB_NODE="job";
	
	
	private XMLWriter moXmlStream;
	
	private Matcher hatField;
	private Matcher hatGlobal;
	private Matcher hatForm;
	private Matcher hatJob;
	
	private int numRecords = 0;
	
	//by default process only first 100,000 bytes
	private int maxBytesToProcess = 100000;
	
	private int numBytesRead = 0;
	

	public OCSDataAccess(final int maxBytes) {
		if(maxBytes > 0)
			maxBytesToProcess = maxBytes;
		
		hatField = Pattern.compile(HAT_FIELD_REGEX).matcher("");
		hatGlobal = Pattern.compile(HAT_GLOBAL_REGEX).matcher("");
		hatForm = Pattern.compile(HAT_FORM_REGEX).matcher("");
		hatJob = Pattern.compile(HAT_JOB_REGEX).matcher("");
	}
	
	
    protected void initialize() throws IOException, ParserConfigurationException {
    	moXmlStream = new XMLWriter("File");
    }
    
    
    
    protected void addElementWithText(String parent, String sElement, String sText) throws IOException {
    	if(sText != null) {
    		moXmlStream.addChild(parent.trim(),sElement.trim(), sText == null ? "" : sText.trim());
    	}
   }
    
    public Document convertToXml(Document oDoc) throws DataAccessSampleException {
    	Document oXmlDoc = null;
    	
    	//null check to avoid null pointer if input doc is null
    	InputStream oStream = oDoc != null ? oDoc.getInputStream() : null;
    	
    	if(oStream != null) {
    		try {
	    		try {
					convertToXml(oStream);
				} catch (Exception e) {
					throw new DataAccessSampleException(e);
				}
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				try {
					printToStream(baos);
				} catch (IOException e) {
					throw new DataAccessSampleException(e);
				}
				
				oXmlDoc = new Document(baos.toByteArray());
	    	}
        	finally	{
        		try{
        			if(oStream != null)
        				oStream.close();
        		}
        		catch(IOException e) {
        			
        		}
        	}
    	}

    	return oXmlDoc;
    }

   

    protected void handleJob(String line) throws IOException{
    	
    	StringBuffer sb = new StringBuffer();
    	boolean bQuoteMatcher = false;
    	//leave ^job 
    	for(int i="^job ".length(); i < line.length(); i++) {
    		switch(line.charAt(i)) {
    		case '\"':
    			bQuoteMatcher = (!bQuoteMatcher);
    			sb.append(line.charAt(i));
    			break;
    		case ' ':
    			if(!bQuoteMatcher) {
    				addElementWithText(JOBS_NODE,JOB_NODE, sb.toString());
    				sb.setLength(0);
    			}
    			else
    				sb.append(line.charAt(i));
    			break;
    		default:
    			sb.append(line.charAt(i));
    		}
    			
    	}
    	
    	if(sb.length() > 0)
			addElementWithText(JOBS_NODE,JOB_NODE, sb.toString());
    }

    protected void handleField(String line, BufferedReader br) throws IOException {
    	//one char extra for space and ^ 
    	String key = line.substring("^fields".length());
    	br.mark(200);
    	String value = br.readLine();
    	
    	//if there is no value specified for a field then add an empty node
    	if(value == null || value.length() == 0 || value.charAt(0) == '^') {
    		br.reset();
    		value=""; //add empty value
    	}
    	else
    		numBytesRead += value.getBytes().length;
    	
    	addElementWithText(FIELDS_NODE, key, value);
    }

    protected void handleGlobal(String line, BufferedReader br) throws IOException {
    	//one char extra for space and ^ 
    	String key = line.substring("^global".length());
    	br.mark(200);
    	String value = br.readLine();
    	
    	//if there is no value specified for a field then add an empty node
    	if(value == null || value.length() == 0 || value.charAt(0) == '^') {
    		br.reset();
    		value=""; //add empty value
    	}
    	else
    		numBytesRead += value.getBytes().length;

    	addElementWithText(GLOBALS_NODE, key, value);
    }

    protected void handleForm(String line) throws IOException {
    	numRecords++;
    	//one char extra for space and ^ 
    	String formName = line.substring("^form".length());
    	
    	addElementWithText(FORMS_NODE, FORM_NODE, formName == null ? "" : formName);
    }

    
    protected void skipThisLine(InputStream datStream) throws Exception {
    	while(datStream.read() != '\n')
    		;
    }

    
    protected void convertToXml(InputStream datStream) throws Exception {
    	BufferedReader rd = null;
    	try {
    		initialize();
            // Create the reader
            rd = new BufferedReader(new InputStreamReader(datStream));
            
            // Retrieve all lines that match pattern
            
            String line = null;
            while (numBytesRead < maxBytesToProcess && (line = rd.readLine()) != null) {

            	numBytesRead += line.getBytes().length;

                hatField.reset(line);
            	hatJob.reset(line);
            	hatForm.reset(line);
            	hatGlobal.reset(line);

            	if (hatField.find()) {
                	handleField(line,rd);
                }
                else if (hatJob.find()) {
                	handleJob(line);
                }
                else if (hatForm.find()) {
                	handleForm(line);

                	if( numRecords > 1)
                		break;
                }
                else if (hatGlobal.find()) {
                	handleGlobal(line,rd);
                }
                else
                {
                	//leave it
                }
            	
            }
        }
    	catch (IOException e) {
        }
    	finally{
    		try{
    			rd.close();
    		}
    		catch(Exception e){
    			
    		}
    	}
    }
    

	protected void printToStream(OutputStream xmlStream) throws IOException {
    	moXmlStream.printToStream(xmlStream);
    }

    
    
}	
