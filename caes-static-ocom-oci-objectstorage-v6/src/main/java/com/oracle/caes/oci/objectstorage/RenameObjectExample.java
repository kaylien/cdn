package com.oracle.caes.oci.objectstorage;

/** This is an automatically generated code sample. 
To make this code sample work in your Oracle Cloud tenancy, 
please replace the values for any parameters whose current values do not fit
your use case (such as resource IDs, strings containing ‘EXAMPLE’ or ‘unique_id’, and 
boolean, number, and enum parameters with values not fitting your use case).
*/

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.*;
import com.oracle.bmc.objectstorage.requests.*;
import com.oracle.bmc.objectstorage.responses.*;
import com.oracle.caes.oci.objectstorage.utils.PropertiesManager;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class RenameObjectExample {
	private static final Logger LOG = LogManager.getLogger(RenameObjectExample.class);
	
	 public static void processRenamePrefix(String namespaceName, String bucketName, String oldName, String newName, String profile,  ObjectStorage client) {
		 try {
			 if (oldName != null && newName != null) {
				 RenameObjectDetails renameObjectDetails = RenameObjectDetails.builder()
							.sourceName(oldName)
							.newName(newName)
							//.srcObjIfMatchETag("EXAMPLE-srcObjIfMatchETag-Value")
							//.newObjIfMatchETag("EXAMPLE-newObjIfMatchETag-Value")
							//.newObjIfNoneMatchETag("EXAMPLE-newObjIfNoneMatchETag-Value")
							.build();
					
				RenameObjectRequest renameObjectRequest = RenameObjectRequest.builder()
					.namespaceName(namespaceName)
					.bucketName(bucketName)
					.renameObjectDetails(renameObjectDetails)
					//.opcClientRequestId("ocid1.test.oc1..<unique_ID>EXAMPLE-opcClientRequestId-Value")
					.build();

		        /* Send request to the Client */
		        RenameObjectResponse response = client.renameObject(renameObjectRequest);
		        if(response != null) {
		        	// use the response's function to print the fetched object's metadata
		            System.out.println("response :::"+response.toString() + ":::status::" + response.get__httpStatusCode__());
		        }
		        
		        response = null;
		        renameObjectRequest = null;
		        renameObjectDetails = null;
			 }
			 
		 }
		 catch (Exception ex) {
			 LOG.error("Failed to rename the old file:"+oldName+" - new file:"+newName, ex);
			 System.out.println("Exception renaming files -"+oldName+" - new file:"+newName);
		 }
		 
		 
	 }
    public static void main(String[] args) throws Exception {

        /**
         * Create a default authentication provider that uses the DEFAULT
         * profile in the configuration file.
         * Refer to <see href="https://docs.cloud.oracle.com/en-us/iaas/Content/API/Concepts/sdkconfig.htm#SDK_and_CLI_Configuration_File>the public documentation</see> on how to prepare a configuration file.
         */
       
    	if(args != null && args.length == 3) {
    		String prefix = args[1];
    		String newPrefix = args[2];
    		String configPath = args[0];
    		try {
    			PropertiesManager pm = new PropertiesManager("config.properties");
				String namespaceName = pm.getProperty("oci.namespace");
				String bucketName = pm.getProperty("oci.bucketname");
				String profile = pm.getProperty("oci.profile");
				// Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI
	            // config file
	            // "~/.oci/config", and a profile in that config with the name "DEFAULT". Make changes to
	            // the following
	            // line if needed and use ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
	
	            final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configPath);
	            final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
	            ObjectStorage client = new ObjectStorageClient(provider);
	            client.setRegion(Region.US_ASHBURN_1);
	            long t1 = System.currentTimeMillis();
	            List<String> fileNames = ListObjectsExample.listObjects(namespaceName, bucketName, prefix, profile, client);
	            if(fileNames != null && fileNames.size() > 0) {
	            	for(String oldName : fileNames) {
	            		String newName = newPrefix + oldName.substring(prefix.length());
	            		System.out.println("oldName::"+oldName+"  -----  newName::"+newName);
	            		processRenamePrefix(namespaceName, bucketName, oldName, newName, profile, client);
	            	}
	            }
				long t2 = System.currentTimeMillis();
				long timeDiff = (t2 - t1)/1000;
				System.out.println("Time taken to process files : "+timeDiff);
    			
    		}
    		catch (Exception ex) {
				LOG.error("Exception processing files", ex);
				System.out.println("Exception processing renaming files");
			}
	    } else {
			System.out.println("Invalid arguments, only input file path is allowed");
		}
    }

    
}