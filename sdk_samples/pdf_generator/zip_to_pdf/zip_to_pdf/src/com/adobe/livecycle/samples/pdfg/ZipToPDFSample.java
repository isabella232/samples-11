package com.adobe.livecycle.samples.pdfg;

/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright [first year code created] Adobe Systems Incorporated
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



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

 

import com.adobe.idp.Document;

import com.adobe.idp.dsc.DSCException;

import com.adobe.idp.dsc.InvocationRequest;

import com.adobe.idp.dsc.InvocationResponse;

import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;

import com.adobe.idp.dsc.clientsdk.ServiceClientFactoryProperties;
import com.adobe.livecycle.generatepdf.client.CreatePDFResult;
import com.adobe.livecycle.generatepdf.client.HtmlToPdfResult;


public class ZipToPDFSample {
	    /**
	     * @param args
	     */
		private static Properties orechestrationProp = null;

	    public static void main(String[] args) throws Exception {
			try {
				orechestrationProp = loadProperties();
			} catch (IOException e) {
				System.out.println("Error:Could not load properties file ZipToPDFSample.properties");
				return;
			}
			boolean atLeastOneFileConverted = false;
			String serviceName = null;
			String operationName = null;
			boolean isLogFileCreatedForZip2pdf = false;
			String infilePath = orechestrationProp.getProperty("inputDocument");
			if (! infilePath.toLowerCase().endsWith("zip")){
				throw new Exception("Input file is not a zip file");
			}
			
			File zipFile = new File(infilePath);
			if (!zipFile.exists()) {
				throw new Exception("Input file " + infilePath + " does not exist");
			}
			
			String outputZipFileName = zipFile.getName();
			String xmpFilePath = orechestrationProp.getProperty("xmpDocument");
			String fileTypeSettings = orechestrationProp.getProperty("fileTypeSettings");
	        String pdfSettings = orechestrationProp.getProperty("pdfSettings");
	        String securitySettings = orechestrationProp.getProperty("securitySettings");
	        String configFilePath = orechestrationProp.getProperty("settingsDocument");
	        
			String outputDir = orechestrationProp.getProperty("outputDir");
			
			//Creating a temporary folder to unzip the zip file
			File unzipFolder = new File (outputDir + "//" + "unzip");
			if (unzipFolder.exists()){
				deleteDirectory(unzipFolder);
			}
			unzipFolder.mkdirs();
			
			File logFile = new File (outputDir + "//" + getInputFileName(infilePath)+".log");
			//Removing the log file if already present
			if (logFile.exists()) {
				logFile.delete();
			}
			extractZipFile(zipFile, unzipFolder.getAbsolutePath());
			
			
			File [] filesInZip = unzipFolder.listFiles();
			
			PrintWriter logfileWriter = null;
			File outputPDFdir = null;
			
			//creating tmp output directory
			outputPDFdir = new File(outputDir + "//out");
			outputPDFdir.mkdirs();
		
			try {
				//Converting each file in zip to pdf
				for (int i = 0; i < filesInZip.length ; i++) {
					File file = filesInZip[i];
					if (file.isDirectory()) continue;
					Document docFromFile = new Document (file, false);
					try {
						String fName = file.getName();
						String fNameInLowerCase = fName.toLowerCase(); 
						operationName = "CreatePDF2";
						if (fNameInLowerCase.endsWith(".ps") || fNameInLowerCase.endsWith(".prn") || fNameInLowerCase.endsWith(".eps")) {
							serviceName = "DistillerService";
							
						} else {
							serviceName = "GeneratePDFService";
							if (fNameInLowerCase.endsWith(".htm") || fNameInLowerCase.endsWith(".html") || fNameInLowerCase.endsWith(".zip")) {
								operationName = "HtmlFileToPDF";
							}
								
						}
						Document outputDoc = invokeService(serviceName, operationName, docFromFile, fName, fileTypeSettings, pdfSettings, securitySettings, configFilePath, xmpFilePath);	// call appropriate service & operation based on the input
						if (outputDoc == null) {
							throw new Exception("Error occured converting the file");
						}
						File outputFile = new File (outputPDFdir, file.getName() + ".pdf");
						outputDoc.copyToFile(outputFile);
						atLeastOneFileConverted = true;
					} catch (Exception e) {
						if (!isLogFileCreatedForZip2pdf) {
							logFile.createNewFile();
							logfileWriter = new PrintWriter(new FileWriter(logFile));
						}
						logfileWriter.println("File : " + file.getName());
						logfileWriter.println();
						e.printStackTrace(logfileWriter);
						
						logfileWriter.println();
						logfileWriter.println();
						isLogFileCreatedForZip2pdf = true;
					}
				}
				
				if (atLeastOneFileConverted){
					createZip(new File (outputDir, outputZipFileName), outputPDFdir);
				}
				deleteDirectory(outputPDFdir);
				
			} finally {
				if (logfileWriter != null) {
					try {
						logfileWriter.flush();
						logfileWriter.close();
					} catch (Throwable th) {
						//ignore
					}
				}
				//clean up
				deleteDirectory(unzipFolder);
			}
			
			System.out.println("Please check the results in " + outputDir + " directory");
	    }

		private static void createZip(File outputZipFile, File outputPDFdir) throws Exception {
			File [] files = outputPDFdir.listFiles(new Filter("pdf"));
			ZipOutputStream zos = null;
			try {
				zos = new ZipOutputStream(new FileOutputStream(outputZipFile));
				// Setting the compression level to highest
				zos.setLevel(9);
				byte[] readBuffer = new byte[32768];
				for (File file: files) {
					int bytesIn = 0;
					FileInputStream fin = null;
					try {
						fin = new FileInputStream(file);
						ZipEntry zipEntry = new ZipEntry(file.getName());
						zos.putNextEntry(zipEntry);
						while ((bytesIn = fin.read(readBuffer)) != -1) {
							zos.write(readBuffer, 0, bytesIn);
						}
					} catch (IOException e) {
						throw e;
					} finally {
						if (fin != null) {
							try {
								fin.close();
							} catch (IOException e) {
							}

						}
					}
				}
			} catch (Exception th) {
				throw th;
			}
			finally {
				if (zos != null) {
					try {
						zos.close();
					} catch (Exception ex) {
						//ignore
					}
				}
			}
		}

		
		/**
		 * This method extract the given zip file contents into the parent directory of the input file
		 * @param inputFile - Zip file whose contents need to be extracted 
		 * @param destinationName 
		 * @throws Exception 
		 */
		private static void extractZipFile(File inputFile, String destinationName) throws Exception
	    {
			ZipInputStream zipInputStream = null;
	        ZipEntry zipEntry = null;
	        FileOutputStream fileOutputStream = null;
			try
	        {
	            byte[] buf = new byte[32768];
	            
				zipInputStream = new ZipInputStream(
								new FileInputStream(inputFile));
				zipEntry = zipInputStream.getNextEntry();
	            	
	            while (zipEntry != null) 
	            { 
	                //for each entry to be extracted
	            	String entryName = zipEntry.getName();
	                int noOfBytesRead;
	                fileOutputStream = null;
	                
	                String zipEntryPath = destinationName+ File.separator +entryName.toLowerCase();
	                File newFile = new File(zipEntryPath);
	  
	                // Creating the file or directory for the zip entry
	                // If the file already exists in the directory, it's deleted 
	                // and a new file is created
	                if (zipEntry.isDirectory()) {
	                	if (! newFile.exists()) {
	                	newFile.mkdirs();
	                	}
	                	zipEntry = zipInputStream.getNextEntry();
	                } else {
	                	if (newFile.exists()) {
	                		newFile.delete();
	                	}
	                	// If the parent directories of the file name is not 
	                	// already created create it
	                	File parentFile = newFile.getParentFile();
	                	if (parentFile != null && ! parentFile.exists()) {
	                		parentFile.mkdirs();
	                	}
	                	newFile.createNewFile();
	                	fileOutputStream = new FileOutputStream(zipEntryPath);             

	                	while ((noOfBytesRead = zipInputStream.read(buf)) > -1) {
	                		fileOutputStream.write(buf, 0, noOfBytesRead);
	                	}
	                	fileOutputStream.close(); 
	                	zipInputStream.closeEntry();
	                	zipEntry = zipInputStream.getNextEntry();
	                }
	                

	            }//while
	        }
	        catch (Exception e)	{
				throw e;
	        } finally {
		        if (fileOutputStream != null) {
		        	try {
		        		fileOutputStream.close();
		        	} catch (Exception e) {
		           		// Ignore
		           	}
	        	}
	        	if (zipInputStream != null) {
	        		try {
						zipInputStream.closeEntry();
					} catch (Exception e) {
						// Ignore
					}
					try {
						zipInputStream.close();
					}  catch (Exception e) {
	            		// Ignore
	            	}
				}
	        }
	    }

		
		private static Document invokeService(String serviceName, String operationName, Document docFromFile, String fileName, String fileTypeSettings, String pdfSettings, String securitySettings, String configFilePath, String xmpFilePath) throws Exception {

			Document outputDoc = null;
			String serverURL = orechestrationProp.getProperty("serverURL");
	    	String userName = orechestrationProp.getProperty("userName");
	    	String password = orechestrationProp.getProperty("password");
	    	try {
	    		if (serverURL == null || serverURL.trim().equals("")) {
	            	throw new Exception ("ServerURL not specified in the ZipToPDFSample.properties file");
	            } else {
	            	serverURL = serverURL.trim();
	            }
	        	
	    		if (userName == null || userName.trim().equals("") || password == null || password.trim().equals("")){
	    			throw new Exception ("Error: username and(or) password not specified in ZipToPDFSample.properties file");
	    		}

	            Properties oProps = new Properties();

	            oProps.put(ServiceClientFactoryProperties.DSC_CREDENTIAL_PASSWORD, password);

	            oProps.put(ServiceClientFactoryProperties.DSC_CREDENTIAL_USERNAME, userName);

	            oProps.put(ServiceClientFactoryProperties.DSC_DEFAULT_SOAP_ENDPOINT, serverURL);

	            oProps.put(ServiceClientFactoryProperties.DSC_TRANSPORT_PROTOCOL, ServiceClientFactoryProperties.DSC_SOAP_PROTOCOL);
	            ServiceClientFactory factory = ServiceClientFactory.createInstance(oProps);
	            
	            Document xmpDoc = null;
	            if (xmpFilePath != null && !xmpFilePath.trim().equals("")) {
	            	File xmpFile = new File (xmpFilePath);
	            	if (xmpFile.exists()) {
	            		xmpDoc = new Document(xmpFile, false);
	            	} else {
	            		System.out.println("Warning:XMP file " + xmpFilePath + " doesn't exist. Going ahead with conversion without XMP data");
	            	}
	            	
	            }
	            Document configDoc = null;
	            if (configFilePath != null && !configFilePath.trim().equals("")) {
	            	File configFile = new File (configFilePath);
	            	if (configFile.exists()) {
	            		configDoc = new Document (configFile, false);
	            	} else {
	            		System.out.println( "Warning: Config file " +  configFilePath +" doesn't exist. Going ahead with conversion with default");
	            	}
	            }

	            HashMap <String, Object>params = new HashMap<String, Object>();
	            
	            params.put("inputDocument", docFromFile);
	            params.put("fileName", fileName);
	            params.put("xmpDocument", xmpDoc);
	            params.put("settingsDocument", configDoc);
	            params.put("fileTypeSettings", fileTypeSettings);
	            params.put("pdfSettings", pdfSettings);
	            params.put("securitySettings", securitySettings);

	            InvocationRequest request =
	            	       factory.createInvocationRequest(serviceName, operationName, params, true);
	            
	            InvocationResponse response = null;
				try {
					response = factory.getServiceClient().invoke(request);
				} catch (DSCException e) {
					throw e;
				}

	            if(operationName.equals("HtmlFileToPDF")){
					HtmlToPdfResult result=(HtmlToPdfResult)response.getOutputParameter("Result");
					outputDoc = result.getCreatedDocument();
				}else if ((operationName.equals("CreatePDF2"))){
						CreatePDFResult result=(CreatePDFResult)response.getOutputParameter("Result");
						outputDoc = (Document)result.getCreatedDocument();
				}
	    	} catch (Exception e) {
	    		throw e;
	    	}
			return outputDoc;
		}

		private static String getInputFileName(String infilePath) {
			String fileName = null;
			if (infilePath != null) {
				int index = infilePath.lastIndexOf('\\');
				// Handling the filepath for linux
				if (index == -1 ) {
					index = infilePath.lastIndexOf('/');
				}
				if (index != -1) {
					fileName = infilePath.substring(index + 1);
				}
			}
			return fileName;
		}

		private static Properties loadProperties() throws IOException {
			Properties props = new Properties();
			InputStream is = ZipToPDFSample.class.getResourceAsStream("ZipToPDFSample.properties");
			props.load(is);
			is.close();
			return props;
		}

		private static boolean deleteDirectory(File path) {
		    if( path != null && path.exists() ) {
		      File[] files = path.listFiles();
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
		    return( path.delete() );
		  }


		static class Filter implements FilenameFilter {
			  protected String pattern;
			  public Filter (String str) {
			    pattern = str;
			  }
			  
			  public boolean accept (File dir, String name) {
			    return name.toLowerCase().endsWith(pattern.toLowerCase());
			  }
		}
		
}
