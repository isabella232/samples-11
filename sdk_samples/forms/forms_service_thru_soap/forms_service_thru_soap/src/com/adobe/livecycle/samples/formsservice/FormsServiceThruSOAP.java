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
 
package com.adobe.livecycle.samples.formsservice;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import javax.xml.rpc.holders.LongHolder;
import javax.xml.rpc.holders.StringHolder;

import com.adobe.idp.services.BLOB;
import com.adobe.idp.services.FormsService;
import com.adobe.idp.services.FormsServiceServiceLocator;
import com.adobe.idp.services.HTMLRenderSpec;
import com.adobe.idp.services.PDFFormRenderSpec;
import com.adobe.idp.services.RenderFormException;
import com.adobe.idp.services.TransformTo;
import com.adobe.idp.services.URLSpec;
import com.adobe.idp.services.holders.BLOBHolder;
import com.adobe.idp.services.holders.FormsResultHolder;

/**
 * A sample that uses Apache AXIS generated SOAP client to render forms using
 * Forms Service. For its usage and directory structure, please read
 * Readme_FormsServiceThruSOAP.doc
 * 
 * The code here is very similar to the code used in the java client SDK of
 * Forms. Do refer to <a
 * href="http://livedocs.adobe.com/livecycle/es/sdkHelp/programmer/javadoc/com/adobe/livecycle/formsservice/client/package-detail.html">
 * Forms java client Javadocs</a> and <i>Rendering forms topic</i> in <a
 * href="http://livedocs.adobe.com/livecycle/es/sdkHelp/programmer/sdkHelp/wwhelp/wwhimpl/js/html/wwhelp.htm">
 * Adobe® LiveCycle® ES (Enterprise Suite) SDK Help</a>
 * 
 * @author adarsh
 * 
 */
public class FormsServiceThruSOAP {

	/**
	 * Usage: java com.adobe.livecycle.samples.formsservice.FormsServiceThruSOAP
	 * [renderPDFForm2|renderHTMLForm2] ...
	 * 
	 * Settings can be modified in file input.props
	 * 
	 * @param args
	 *            The command line arguments.
	 */
	public static void main(String[] args) {

		// We sanitize command line arguments here. It is not needed when no
		// command line arguments are passed. Both renderPDFForm and
		// renderHTMLForm shall be invoked then.
		Set<String> methods = new HashSet<String>();
		for (String method : args) {
			if (method.equalsIgnoreCase("renderPDFForm2")
					|| method.equalsIgnoreCase("renderHTMLForm2")) {
				methods.add(method.toLowerCase());
			} else {
				printUsage();
				return;
			}
		}
		// end of sanitizing command line arguments.

		FormsServiceThruSOAP formsServiceSample = new FormsServiceThruSOAP();
		switch (methods.size()) {
		case 0:
			formsServiceSample.runSample();
			break;
		case 1:
		case 2:
			formsServiceSample.runSample(methods);
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
		callRenderPDFForm(inputProps);
		callRenderHTMLForm(inputProps);
	}

	/**
	 * Method to invoke the sample when command line arguments are given.
	 * 
	 * @param operations
	 */
	public void runSample(Set<String> operations) {
		InputProps inputProps = getInputProps();
		for (String method : operations) {
			if ("renderPDFForm2".equalsIgnoreCase(method)) {
				callRenderPDFForm(inputProps);
			}
			if ("renderHTMLForm2".equalsIgnoreCase(method)) {
				callRenderHTMLForm(inputProps);
			}
		}
	}

	private static void printUsage() {
		System.out
				.println("Usage: java com.adobe.livecycle.samples.formsservice.FormsServiceThruSOAP "
						+ "[renderPDFForm|renderHTMLForm] ...");
		System.out.println("Settings can be modified in file input.props");
	}

	/**
	 * Render a Form design as PDF. What form design to render and how, can be set in
	 * input.props file. This method prepares the input arguments and calls
	 * {@link FormsService#renderPDFForm2(BLOB, BLOB, PDFFormRenderSpec, URLSpec, HashMap, BLOBHolder, LongHolder, StringHolder, FormsResultHolder)}
	 * 
	 * @param inputProps
	 *            The object created using input.props file. see
	 *            {@link InputProps}
	 */
	public void callRenderPDFForm(InputProps inputProps) {
		try {
			// Get the forms service object.
			FormsService formsService = getFormsService(inputProps);

			// prepare the input arguments
			/**
			 * The inputData object contains the ../resources/input.xml file by default.
			 */
			BLOB inputData = readInputDataXML(inputProps);
			/**
			 * Here we use the default empty PDFFormRenderSpec
			 */
			PDFFormRenderSpec renderSpec = new PDFFormRenderSpec();
			/**
			 * URLSpec is used here to give the content URI to renderPDFForm
			 */
			URLSpec urlSpec = new URLSpec();
			urlSpec.setContentRootURI(inputProps.getContentRootURI());

			// the output arguments. The first three output arguments below are
			// simply components of FormsResultHolder provided as a convenience.
			BLOBHolder outRenderPDFFormResultDoc = new BLOBHolder();
			LongHolder pageCount = new LongHolder();
			StringHolder locale = new StringHolder();
			/**
			 * @{link FormsResultHolder} holds all the output of renderPDFForm
			 *        method.
			 */
			FormsResultHolder renderPDFFormResult = new FormsResultHolder();

			/**
			 * BLOB which holds the InputForm
			 */
			BLOB formDocument = readFormDocument(inputProps);
			
			/**
			 * invoke the actual method of FormsService.
			 */
			formsService.renderPDFForm2(formDocument, inputData,
					renderSpec, urlSpec, new HashMap(),
					outRenderPDFFormResultDoc, pageCount, locale,
					renderPDFFormResult);

			/**
			 * Write the output file.
			 */
			writeOutput(renderPDFFormResult.value.getOutputContent(),
					inputProps, "renderPDFForm.pdf");

		} catch (RenderFormException e) {
			System.err.println("Server has returned a RenderFormException.");
			e.printStackTrace();
		} catch (RemoteException e) {
			System.err.println("A communication exception has occured.");
			e.printStackTrace();
		}

	}

	/**
	 * Render a Form as HTML. What form to render and how can be set in
	 * input.props file. This method prepares the input arguments and calls
	 * {@link FormsService#renderHTMLForm2(BLOB, TransformTo, BLOB, HTMLRenderSpec, String, URLSpec, HashMap, BLOBHolder, BLOBHolder, LongHolder, StringHolder, StringHolder, FormsResultHolder)}
	 * 
	 * Please note that the rendered HTML form using defaults refers to
	 * stylesheets not downloaded by this sample to filesystem. To get proper
	 * look and feel of the default HTML form, it will have to hosted on the
	 * server.
	 * 
	 * @param inputProps
	 *            The object created using input.props file. see
	 *            {@link InputProps}
	 */
	public void callRenderHTMLForm(InputProps inputProps) {
		try {
			// Get the forms service object.
			FormsService formsService = getFormsService(inputProps);

			// prepare the input arguments
			TransformTo transformTo = getTransformTo(inputProps);
			/**
			 * The inputData object contains the ../resources/input.xml file by default.
			 */
			BLOB inputData = readInputDataXML(inputProps);
			/**
			 * Here we use the default empty HTMLRenderSpec
			 */
			HTMLRenderSpec renderSpec = new HTMLRenderSpec();
			String userAgent = inputProps.getUserAgent();
			/**
			 * URLSpec is used here to give the content URI to renderHTMLForm
			 */
			URLSpec urlSpec = new URLSpec();
			urlSpec.setContentRootURI(inputProps.getContentRootURI());

			// the output arguments. The first five output arguments below are
			// simply components of FormsResultHolder provided as a convenience.
			BLOBHolder outRenderHTMLFormResultDoc = new BLOBHolder();
			BLOBHolder outputXML = new BLOBHolder();
			LongHolder pageCount = new LongHolder();
			StringHolder locale = new StringHolder();
			StringHolder actualRendering = new StringHolder();

			/**
			 * @{link FormsResultHolder} holds all the output of renderHTMLForm
			 *        method.
			 */
			FormsResultHolder renderHTMLFormResult = new FormsResultHolder();

			/**
			 * BLOB which holds the InputForm
			 */
			BLOB formDocument = readFormDocument(inputProps);
			
			/**
			 * invoke the actual method of FormsService.
			 */
			formsService.renderHTMLForm2(formDocument, transformTo, 
					inputData, renderSpec, userAgent, urlSpec,
					new HashMap(), outRenderHTMLFormResultDoc, outputXML,
					pageCount, locale, actualRendering, renderHTMLFormResult);

			/**
			 * Write the output file.
			 */
			writeOutput(renderHTMLFormResult.value.getOutputContent(),
					inputProps, "renderHTMLForm.html");

		} catch (RenderFormException e) {
			System.err.println("Server has returned a RenderFormException.");
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
	 * Get FormsService object created by Apache AXIS. It also sets the
	 * authentication username and password.
	 * 
	 * @param inputProps
	 * @return
	 */
	private FormsService getFormsService(InputProps inputProps) {
		FormsServiceServiceLocator locator = new FormsServiceServiceLocator();
		FormsService formsService = null;
		try {
			formsService = locator.getFormsService(new URL(inputProps
					.getSoapEndpointURL()));

			// LiveCycle ES needs authentication.
			((Stub) formsService)._setProperty(Stub.USERNAME_PROPERTY,
					inputProps.getUserName());
			((Stub) formsService)._setProperty(Stub.PASSWORD_PROPERTY,
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
		return formsService;
	}

	/**
	 * A convenience method to write content of an {@link BLOB} object.
	 * 
	 * @param blob
	 * @param inputProps
	 * @param fileName the file to write
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
	 * A convenience method to parse a {@link String} as a {@link TransformTo}
	 * 
	 * @param inputProps
	 * @return
	 */
	private TransformTo getTransformTo(InputProps inputProps) {
		TransformTo transformTo = TransformTo.AUTO;
		try {
			transformTo = TransformTo.fromString(inputProps.getTransformTo());
		} catch (IllegalArgumentException e) {
			System.err.println("The value of transform to: "
					+ inputProps.getTransformTo()
					+ " is not correct. Continuing assuming a value of AUTO");
		}
		return transformTo;
	}
}
