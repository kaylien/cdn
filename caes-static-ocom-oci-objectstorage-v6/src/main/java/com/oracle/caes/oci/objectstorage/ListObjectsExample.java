package com.oracle.caes.oci.objectstorage;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.ListObjects;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.caes.oci.objectstorage.utils.PropertiesManager;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;

public class ListObjectsExample {
	private static final Logger LOG = LogManager.getLogger(ListObjectsExample.class);
	
	public static List<String> listObjects(String namespaceName, String bucketName, String prefix, String profile,  ObjectStorage client) {
		List<String> fileNames = null;
		if (prefix != null) {
			try {
				System.out.println("Creating list Objects bucket request");
		        ListObjectsRequest request = ListObjectsRequest.builder()
		        							.namespaceName(namespaceName)
		        							.bucketName(bucketName)
		        							.prefix(prefix).build();
		        ListObjectsResponse response = client.listObjects(request);
		        if(response != null ) {
		        	System.out.println("Status:"+response.get__httpStatusCode__());
		        	System.out.println("Object details:"+response.toString());
		        	ListObjects objects = response.getListObjects();
		        	if(objects != null) {
		        		List<ObjectSummary>	objSummList = objects.getObjects();
		        		fileNames = new ArrayList<>();
		        		for(ObjectSummary ObjSumm : objSummList) {
		        			String objName = ObjSumm.getName();
		        			if(objName != null && objName.endsWith("/")) {
		        				System.out.println("It is prefix/folder::: "+objName);
		        			} else {
		        				fileNames.add(objName);
		        				System.out.println("Object name::: "+objName);
		        			}
		        			
		        		}
		        		
		        	}
		        }

			}
			catch (Exception ex) {
				LOG.error("Failed listing the objects from bucket");
			}
		} else {
			System.out.println("invalid prefix to read from bucket");
		}
		
		return fileNames;
		
	}
	
	public static void main(String[] args) {
		if(args != null && args.length == 2) {
    		String prefix = args[1];
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
	            listObjects(namespaceName, bucketName, prefix, profile, client);
				long t2 = System.currentTimeMillis();
				long timeDiff = (t2 - t1)/1000;
				System.out.println("Time taken to listing files : "+timeDiff);
    		}
    		catch (Exception ex) {
				LOG.error("Exception listing files", ex);
				System.out.println("Exception listing  files");
			}
	    } else {
			System.out.println("Invalid arguments");
		}
	}

}
