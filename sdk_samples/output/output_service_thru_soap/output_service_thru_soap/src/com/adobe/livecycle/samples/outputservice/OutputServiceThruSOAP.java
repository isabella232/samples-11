/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2007 - 2008 Adobe Systems Incorporated
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
 *
 *
 **************************************************************************/
package com.adobe.livecycle.samples.outputservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;


import com.adobe.idp.services.BLOB;
import com.adobe.idp.services.OutputException;
import com.adobe.idp.services.OutputService;
import com.adobe.idp.services.OutputServiceServiceLocator;
import com.adobe.idp.services.PDFOutputOptionsSpec;
import com.adobe.idp.services.PrintFormat;
import com.adobe.idp.services.PrintedOutputOptionsSpec;
import com.adobe.idp.services.RenderOptionsSpec;
import com.adobe.idp.services.TransformationFormat;
import com.adobe.idp.services.holders.BLOBHolder;
import com.adobe.idp.services.holders.OutputResultHolder;

/**
 * A sample that uses Apache AXIS generated SOAP client creating Document Output
 * Streams using Output Service. For its usage and directory structure, please read
 * Readme_OutputServiceThruSOAP.doc
 * 
 * The code here is very similar to the code used in the java client SDK of
 * Output. Do refer to <a
 * href="http://livedocs.adobe.com/livecycle/es/sdkHelp/programmer/javadoc/com/adobe/livecycle/output/client/package-detail.html">
 * Output java client Javadocs</a> and <i>Creating Document Output Streams
 * topic</i> in <a
 * href="http://livedocs.adobe.com/livecycle/es/sdkHelp/programmer/sdkHelp/wwhelp/wwhimpl/js/html/wwhelp.htm">
 * Adobe® LiveCycle® ES (Enterprise Suite) SDK Help</a>
 * 
 * @author adarsh
 * 
 */
public class OutputServiceThruSOAP {

	/**
	 * Usage: java
	 * com.adobe.livecycle.samples.outputservice.OutputServiceThruSOAP
	 * [generatePDFOutput2|generatePrintedOutput2] ...
	 * 
	 * Settings can be modified in file input.props
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String[] args) {

		// We sanitize command line arguments here. It is not needed when no
		// command line arguments are passed. Both generatePDFOutput and
		// generatePrintedOutput shall be invoked then.
		Set<String> methods = new HashSet<String>();
		for (String method : args) {
			if (method.equalsIgnoreCase("generatePDFOutput2")
					|| method.equalsIgnoreCase("generatePrintedOutput2")) {
				methods.add(method.toLowerCase());
			} else {
				printUsage();
				return;
			}
		}
		// end of sanitizing command line arguments.

		OutputServiceThruSOAP outputServiceSample = new OutputServiceThruSOAP();
		switch (methods.size()) {
		case 0:
			outputServiceSample.runSample();
			break;
		case 1:
		case 2:
			outputServiceSample.runSample(methods);
			break;
		default:
			printUsage();
			break;
		}
	}

	/**
	 * Method to run the sample when no command line arguments are given. By
	 * default the build script (build.xml) runs this.
	 */
	public void runSample() {
		InputProps inputProps = getInputProps();
		callGeneratePDFOutput(inputProps);
		callGeneratePrintedOutput(inputProps);
	}

	/**
	 * Method to invoke the sample when command line arguments are given.
	 * 
	 * @param operations
	 */
	public void runSample(Set<String> operations) {
		InputProps inputProps = getInputProps();
		for (String method : operations) {
			if ("generatePDFOutput2".equalsIgnoreCase(method)) {
				callGeneratePDFOutput(inputProps);
			}
			if ("generatePrintedOutput2".equalsIgnoreCase(method)) {
				callGeneratePrintedOutput(inputProps);
			}
		}
	}

	private static void printUsage() {
		System.out
				.println("Usage: java com.adobe.livecycle.samples.formsservice.OutputServiceThruSOAP");
		System.out.println("Settings can be modified in file input.props");
	}

	/**
	 * Generates a PDF or a PDF/A output stream given a form design and data to
	 * merge with the form design. What form design to render and how, can be
	 * set in input.props file. This method prepares the input arguments and
	 * calls
	 * {@link OutputService#generatePDFOutput2(TransformationFormat, String, BLOB, PDFOutputOptionsSpec, RenderOptionsSpec, BLOB, BLOBHolder, BLOBHolder, OutputResultHolder)}
	 * 
	 * @param inputProps
	 *            The object created using input.props file. see
	 *            {@link InputProps}
	 */
	public void callGeneratePDFOutput(InputProps inputProps) {
		try {
			// Get the output service object.
			OutputService outputService = getOutputService(inputProps);

			// prepare the input arguments
			TransformationFormat transformationFormat = getTransformationFormat(inputProps);
			/**
			 * Here we use the default empty PDFOutputOptionsSpec
			 */
			PDFOutputOptionsSpec pdfOutputOptionsSpec = new PDFOutputOptionsSpec();
			// The default values of look ahead and recordlevel are incorrect.
			// Correct this here.
			pdfOutputOptionsSpec.setLookAhead(500);
			pdfOutputOptionsSpec.setRecordLevel(1);
			/**
			 * Here we use the default empty RenderOptionsSpec
			 */
			RenderOptionsSpec renderOptionsSpec = new RenderOptionsSpec();
			/**
			 * The inputData object contains the ../resources/input.xml file by
			 * default.
			 */
			BLOB inputData = readInputDataXML(inputProps);

			// the output arguments. The first three output arguments below are
			// simply components of OutputResultHolder provided as a
			// convenience.
			BLOBHolder generatePDFOutputPDFDoc = new BLOBHolder();
			BLOBHolder generatePDFOutputMetaDataDoc = new BLOBHolder();
			BLOBHolder generatePDFOutputResultDoc = new BLOBHolder();
			/**
			 * @{link OutputResultHolder} holds all the output of
			 *        generatePDFOutput method.
			 */
			OutputResultHolder generatePDFOutputResult = new OutputResultHolder();
			
			/**
			 * BLOB which holds the InputForm
			 */
			BLOB formDocument = readFormDocument(inputProps);

			/**
			 * invoke the actual method of OutputService.
			 */
			outputService.generatePDFOutput2(transformationFormat, 
					inputProps.getContentRootURI(), 
					formDocument, pdfOutputOptionsSpec, 
					renderOptionsSpec, inputData, 
					generatePDFOutputPDFDoc, 
					generatePDFOutputMetaDataDoc, 				
					generatePDFOutputResult);

			/**
			 * Write the output file.
			 */
			writeOutput(generatePDFOutputResult.value.getGeneratedDoc(),
					inputProps, "generatePDFOutput.pdf");

		} catch (OutputException e) {
			System.err.println("Server has returned an OutputException.");
			e.printStackTrace();
		} catch (RemoteException e) {
			System.err.println("A communication exception has occured.");
			e.printStackTrace();
		}
	}

	/**
	 * Generates a PCL, PostScript, or ZPL output stream given a form design and
	 * data to merge with the form design. What form design to render and how,
	 * can be set in input.props file. This method prepares the input arguments
	 * and calls
	 * {@link OutputService#generatePrintedOutput2(PrintFormat, String, BLOB, String, PrintedOutputOptionsSpec, BLOB, BLOBHolder, BLOBHolder, OutputResultHolder)}
	 * 
	 * @param inputProps
	 *            The object created using input.props file. see
	 *            {@link InputProps}
	 */
	public void callGeneratePrintedOutput(InputProps inputProps) {
		try {
			// Get the output service object.
			OutputService outputService = getOutputService(inputProps);

			// prepare the input arguments
			PrintFormat printFormat = getPrintFormat(inputProps);
			String XDCURI = inputProps.getXDCURI();
			/**
			 * Here we use the default empty PrintedOutputOptionsSpec
			 */
			PrintedOutputOptionsSpec printedOutputOptionsSpec = new PrintedOutputOptionsSpec();
			// The default values of look ahead, copies and recordlevel are
			// incorrect. Correct this here.
			printedOutputOptionsSpec.setLookAhead(500);
			printedOutputOptionsSpec.setRecordLevel(1);
			printedOutputOptionsSpec.setCopies(1);
			/**
			 * The inputData object contains the ../resources/input.xml file by
			 * default.
			 */
			BLOB inputData = readInputDataXML(inputProps);

			// the output arguments. The first three output arguments below are
			// simply components of OutputResultHolder provided as a
			// convenience.
			BLOBHolder generatePrintedOutputPrintedDoc = new BLOBHolder();
			BLOBHolder generatePrintedOutputMetaDataDoc = new BLOBHolder();
			BLOBHolder generatePrintedOutputResultDoc = new BLOBHolder();
			/**
			 * @{link OutputResultHolder} holds all the output of
			 *        generatePrintedOutput method.
			 */
			OutputResultHolder generatePrintedOutputResult = new OutputResultHolder();
			
			/**
			 * Document which holds the InputForm
			 */
			BLOB formDocument = readFormDocument(inputProps);
			
			/**
			 * invoke the actual method of OutputService.
			 */
			outputService
					.generatePrintedOutput2(printFormat, inputProps.
							getContentRootURI(), formDocument, XDCURI, 
							printedOutputOptionsSpec, inputData,
							generatePrintedOutputPrintedDoc,
							generatePrintedOutputMetaDataDoc,
							generatePrintedOutputResult);

			/**
			 * Write the output file.
			 */
			writeOutput(generatePrintedOutputResult.value.getGeneratedDoc(),
					inputProps, "generatePrintedOutput.output");

		} catch (OutputException e) {
			System.err.println("Server has returned an OutputException.");
			e.printStackTrace();
		} catch (RemoteException e) {
			System.err.println("A communication exception has occured.");
			e.printStackTrace();
		}
	}

	/**
	 * A convenience method to load the input XML, if given.
	 * 
	 * @param inputProps
	 * @return
	 */
	private BLOB readInputDataXML(InputProps inputProps) {
		BLOB blob = new BLOB();
		byte[] bytes = getURLorFileBytes(inputProps.getInputDataXML());
		if (bytes == null) {
			System.err
					.println("Could not read input data XML file. Will continue without it.");
			return null;
		} else {
			blob.setBinaryData(bytes);
			return blob;
		}
	}
	
	/**
	 * A convenience method to load the input form document, if given.
	 * 
	 * @param inputProps
	 * @return
	 */
	private BLOB readFormDocument(InputProps inputProps) {
		BLOB blob = new BLOB();
		byte[] bytes = getURLorFileBytes(inputProps.
					getContentRootURI() + File.separator + inputProps.getFormsQuery());
		if (bytes == null) {
			System.err
					.println("Could not read input data XML file. Will continue without it.");
			return null;
		} else {
			blob.setBinaryData(bytes);
			return blob;
		}
	}

	/**
	 * A convenience method to load a URL or a file as a byte array.
	 * 
	 * @param urlStr
	 *            Either a URL or a file path
	 * @return
	 */
	private byte[] getURLorFileBytes(String urlStr) {
		try {
			URL url = new URL(urlStr);
			InputStream inputStream = url.openStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int b;
			while ((b = inputStream.read()) != -1) {
				outputStream.write(b);
			}
			inputStream.close();
			return outputStream.toByteArray();
		} catch (MalformedURLException e) {
			// hmmm...probably this is a file.
			File file = new File(urlStr);
			if (!file.exists() && !file.isFile()) {
				System.err.println("Could not resolve URL:" + urlStr);
				e.printStackTrace();
				return null;
			} else {
				return getFileBytes(file);
			}
		} catch (IOException e) {
			System.err.println("Couldn't read from URL:" + urlStr);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * A convenience method to load a file as a byte array.
	 * 
	 * @param file
	 * @return
	 */
	private byte[] getFileBytes(File file) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int b;
			while ((b = inputStream.read()) != -1) {
				outputStream.write(b);
			}
			inputStream.close();
			return outputStream.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Couldn't read from file:" + file.getPath());
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method create a new {@link InputProps} object. If {@link InputProps}
	 * cannot load input.props, built-in defaults are used.
	 * 
	 * @return
	 */
	private InputProps getInputProps() {
		// Load input.props file
		InputProps inputProps = null;
		try {
			inputProps = new InputProps();
		} catch (IOException e) {
			System.out
					.println("Could load the file input.props. Running with built-in defaults.");
			inputProps = new InputProps(new Properties());
		}
		return inputProps;
	}

	/**
	 * Get OutputService object created by Apache AXIS. It also sets the
	 * authentication username and password.
	 * 
	 * @param inputProps
	 * @return
	 */
	private OutputService getOutputService(InputProps inputProps) {
		OutputServiceServiceLocator locator = new OutputServiceServiceLocator();
		OutputService outputService = null;
		try {
			outputService = locator.getOutputService(new URL(inputProps
					.getSoapEndpointURL()));

			// LiveCycle ES needs authentication.
			((Stub) outputService)._setProperty(Stub.USERNAME_PROPERTY,
					inputProps.getUserName());
			((Stub) outputService)._setProperty(Stub.PASSWORD_PROPERTY,
					inputProps.getPassword());
		} catch (ServiceException e) {
			System.err
					.println("An axis exception has been caught."
							+ " Please verify your WSDL, SOAP endpoint URL and rerun the sample.");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.err
					.println("Please verify your WSDL and SOAP endpoint URL. Rerun the sample.");
			e.printStackTrace();
		}
		return outputService;
	}

	/**
	 * A convenience method to write content of an {@link BLOB} object.
	 * 
	 * @param blob
	 * @param inputProps
	 * @param fileName
	 *            the file to write
	 */
	private void writeOutput(BLOB blob, InputProps inputProps, String fileName) {
		byte[] bytes = blob.getBinaryData();

		if (bytes == null) {
			bytes = getURLorFileBytes(blob.getRemoteURL());
		}

		if (bytes == null || bytes.length == 0) {
			System.err.println("Not going to write a zero length document.");
		}

		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(inputProps.getOutputDir() + "/"
					+ fileName);
			outputStream.write(bytes);
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("Please check that " + inputProps.getOutputDir()
					+ " can be created or is not a directory");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not write to "
					+ inputProps.getOutputDir());
			e.printStackTrace();
		}
	}

	/**
	 * A convenience method to parse a {@link String} as a
	 * {@link TransformationFormat}
	 * 
	 * @param inputProps
	 * @return
	 */
	private TransformationFormat getTransformationFormat(InputProps inputProps) {
		TransformationFormat transformationFormat = TransformationFormat.PDF;
		try {
			transformationFormat = TransformationFormat.fromString(inputProps
					.getTransformationFormat());
		} catch (IllegalArgumentException e) {
			System.err.println("The value of transformation format: "
					+ inputProps.getTransformationFormat()
					+ " is not correct. Continuing assuming a value of PDF");
		}
		return transformationFormat;
	}

	/**
	 * A convenience method to parse a {@link String} as a {@link PrintFormat}
	 * 
	 * @param inputProps
	 * @return
	 */
	private PrintFormat getPrintFormat(InputProps inputProps) {
		PrintFormat printFormat = PrintFormat.PostScript;
		try {
			printFormat = PrintFormat.fromString(inputProps.getPrintFormat());
		} catch (IllegalArgumentException e) {
			System.err
					.println("The value of print format: "
							+ inputProps.getPrintFormat()
							+ " is not correct. Continuing assuming a value of PostScript");
		}
		return printFormat;
	}

}
