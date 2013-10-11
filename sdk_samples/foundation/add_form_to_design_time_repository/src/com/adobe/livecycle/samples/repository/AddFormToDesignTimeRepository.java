/* *****************************************************************************
 *
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2004-2007 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Adobe Systems Incorporated and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to Adobe Systems
 * Incorporated and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or copyright
 * law. Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained from
 * Adobe Systems Incorporated.
 *
 */
package com.adobe.livecycle.samples.repository;

import com.adobe.idp.dsc.clientsdk.ServiceClientFactory;
import com.adobe.idp.Document;
import com.adobe.repository.bindings.dsc.client.ResourceRepositoryClient;
import com.adobe.repository.RepositoryException;
import com.adobe.repository.infomodel.RepositoryInfomodelFactory;
import com.adobe.repository.infomodel.Id;
import com.adobe.repository.infomodel.Lid;
import com.adobe.repository.infomodel.bean.ResourceCollection;
import com.adobe.repository.infomodel.bean.Resource;
import com.adobe.repository.infomodel.bean.ResourceContent;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

/**
 * <p>AddFormToDesignTimeRepository is a sample which demonstrates adding a
 * form to the design time repository using the Repository API.</p>
 *
 */
public class AddFormToDesignTimeRepository {

  /**
   * <p>Position of the argument containing the path to the form to upload.</p>
   */
  private static final int FORM_TO_UPLOAD_ARGUMENT = 1;

  /**
   * <p>Position of the username argument.</p>
   */
  private static final int PROPERTIES_FILE_ARGUMENT = 0;

  /**
   * <p>Folder to upload files into.</p>
   */
  private static final String PARENT_FOLDER_NAME = "AddFormToDesignTimeRepository-Sample";

  /**
   * <p>String path to the form to upload.</p>
   */
  private String formToUpload = null;

  /**
   * <p>Path to the properties file containing all the information required to
   * connect to the repository.</p>
   */
  private String propertiesFile = null;

  /**
   * <p>Creates a new instance of AddFormToDesignTimeRepository.</p>
   * @param formToUpload String path to the form to upload
   * @param propertiesFile String path to the properties file containing information
   * required to connect to repository
   */
  public AddFormToDesignTimeRepository(String formToUpload, String propertiesFile) {
    super();
    this.formToUpload = formToUpload;
    this.propertiesFile = propertiesFile;
  }

  /**
   * <p>Execute this AddFormToDesignTimeRepository sample.</p>
   * @throws RepositoryException An error occured during a call to the repository
   */
  public void execute() throws RepositoryException {
    final String PARENT_FOLDER_PATH = "/"+AddFormToDesignTimeRepository.PARENT_FOLDER_NAME;
    final String FORM_PATH = PARENT_FOLDER_PATH + "/" + this.formToUpload;

    ServiceClientFactory serviceClientFactory =
            this.createServiceClientFactory(this.propertiesFile);
    ResourceRepositoryClient repositoryClient =
            new ResourceRepositoryClient(serviceClientFactory);
    RepositoryInfomodelFactory repositoryInfomodelFactory =
            RepositoryInfomodelFactory.newFactory();

    this.initialize(repositoryClient, repositoryInfomodelFactory);

    Resource form = repositoryInfomodelFactory.newResource(new Id(), new Lid(), this.formToUpload);
    File fileToUpload = new File(this.formToUpload);
    Document document = new Document(fileToUpload, false);
    ResourceContent content = repositoryInfomodelFactory.newResourceContent();
    content.setDataDocument(document);
    content.setMimeType("application/vnd.adobe.xdp+xml");
    form.setContent(content);

    System.out.println("-- Initialization Complete\n\n");

    // upload the form
    System.out.print("Adding form to design time repository at ["+FORM_PATH+"]... ");
    form = repositoryClient.writeResource(PARENT_FOLDER_PATH, form);
    System.out.println("Success");

    // read back the form metadata
    System.out.print("Reading form metadata from design time repository... ");
    Resource readForm = repositoryClient.readResource(FORM_PATH);
    System.out.println("Success");

    // read back the form content
    System.out.print("Reading form content from design time repository... ");
    Document readContent = repositoryClient.readResourceContent(FORM_PATH);
    System.out.println("Success");

  }

  /**
   * <p>Adds required folder for this sample if it does not already exist. Also checks for conflicts created by previous
   * runs of this sample and removes them.</p>
   * @param resourceRepositoryClient Client to make calls against the repository
   * @param repositoryInfomodelFactory Factory to create repository information model objects.
   * @throws RepositoryException an error occured during a call to the repository.
   */
  public void initialize(ResourceRepositoryClient resourceRepositoryClient,
                         RepositoryInfomodelFactory repositoryInfomodelFactory) throws RepositoryException {
    final String PARENT_FOLDER_PATH = "/"+AddFormToDesignTimeRepository.PARENT_FOLDER_NAME;
    final String FORM_PATH = PARENT_FOLDER_PATH + "/" + this.formToUpload;

    System.out.println("-- Initializing");
    System.out.print("    Testing if parent folder ["+PARENT_FOLDER_PATH+"] exists... ");

    // make sure the folder to upload into exists
    boolean folderExists = resourceRepositoryClient.resourceExists(PARENT_FOLDER_PATH);
    if (!folderExists) {
      System.out.println(" parent folder not found");
      System.out.print("    Creating parent folder ["+PARENT_FOLDER_PATH+"]... ");
      ResourceCollection folder =
               repositoryInfomodelFactory.newResourceCollection(
                       new Id(),
                       new Lid(),
                       AddFormToDesignTimeRepository.PARENT_FOLDER_NAME
               );
      folder = (ResourceCollection) resourceRepositoryClient.writeResource("/", folder);
      System.out.println("Success");
    } else {
      System.out.println(" parent folder found");
    }

    System.out.print("    Testing if a resource already exists at ["+FORM_PATH+"]... ");
    boolean fileAlreadyExists = resourceRepositoryClient.resourceExists(FORM_PATH);
    if (fileAlreadyExists) {
      System.out.println("resource found");
      System.out.println("    Cleaning up previous sample data. Removing resource found at ["+FORM_PATH+"]");
      resourceRepositoryClient.deleteResource(FORM_PATH);
    } else {
      System.out.println("resource not found");
    }
  }

  /**
   * <p>Creates a ServiceClientFactory from the properties file at the given location.</p>
   * @param propertiesFile String path to a properties file containing the information required
   * to connect to the repository.
   * @return ServiceClientFactory capable of connecting to the repository.
   */
  private ServiceClientFactory createServiceClientFactory(String propertiesFile) {
    FileInputStream fileInputStream = null;
    try {
      fileInputStream = new FileInputStream(propertiesFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Error creating FileInputStream from ["+propertiesFile+"]", e);
    }
    Properties properties = new Properties();
    try {
      properties.load(fileInputStream);
    } catch (IOException e) {
      throw new RuntimeException("Error loading properties from ["+propertiesFile+"]", e);
    }
    return ServiceClientFactory.createInstance(properties);
  }


  /**
   * <p>Allows this class to execute from the command line.</p>
   * @param args String array of command line arguments.
   * @throws RepositoryException an error occured during a call to the repository.
   */
  public static void main(String[] args) throws RepositoryException {
    final String DEFAULT_FORM_TO_UPLOAD = "SampleForm.xdp";
    final String DEFAULT_PROPERTIES_FILE = "JBoss.properties";

    System.out.println();
    if (args.length == 0) {
      System.out.println("Usage: java com.adobe.livecycle.samples.repository.AddformToDesignTimeRepository [properties-file] [form-to-upload]\n");
      System.out.println("   - properties-file: Properties file containing configuration required to connect to the LiveCycle Server");
      System.out.println("   - form-to-upload: (Optional) form to upload (must be a form in the executing directory)\n");
    } else {
      // get the properties-file
      String propertiesFile = DEFAULT_PROPERTIES_FILE;
      if (AddFormToDesignTimeRepository.PROPERTIES_FILE_ARGUMENT < args.length) {
        propertiesFile = args[AddFormToDesignTimeRepository.PROPERTIES_FILE_ARGUMENT];
      }
      File file = new File(propertiesFile);
      if (!file.exists()) {
        System.out.println("Error: properties-file not found ["+file.getPath()+"]");
        return;
      }

      // get the form-to-upload
      String formToUpload = DEFAULT_FORM_TO_UPLOAD;
      if (AddFormToDesignTimeRepository.FORM_TO_UPLOAD_ARGUMENT < args.length) {
        formToUpload = args[AddFormToDesignTimeRepository.FORM_TO_UPLOAD_ARGUMENT];
        // this sample requires that the form to upload be in the same directory and be referenced only by name
        if (-1 != formToUpload.indexOf('/') || -1 != formToUpload.indexOf('/')) {
          System.out.println("Error: form-to-upload may not contain '\\' or '/' it must be uploaded from the executing directory.");
          return;
        }
      } else {
        System.out.println("form-to-upload not specified... using default ["+DEFAULT_FORM_TO_UPLOAD+"]\n\n");
      }
      // check the form-to-upload file exists
      file = new File(formToUpload);
      if (!file.exists()) {
        System.out.println("Error: form-to-upload not found ["+file.getPath()+"]");
        return;
      }

      AddFormToDesignTimeRepository addFormToDesignTimeRepository =
              new AddFormToDesignTimeRepository(
                      formToUpload,
                      propertiesFile);
      addFormToDesignTimeRepository.execute();
    }
  }
}
