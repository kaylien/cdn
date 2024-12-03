/**
 * 
 */
package com.oracle.caes.oci.objectstorage;

/**
 * @author svongolu
 *
 */
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Realm;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import com.oracle.bmc.util.internal.StringUtils;
import com.oracle.caes.oci.objectstorage.utils.FileUtils;
import com.oracle.caes.oci.objectstorage.utils.PropertiesManager;

/**
 * Example of using the simplified UploadManager to upload objects.
 *
 * <p>UploadManager can be configured to control how/when it does multi-part uploads, and manages
 * the underlying upload method. Clients construct a PutObjectRequest similar to what they normally
 * would.
 */
public class UploadObjectExample {

	private static final Logger LOG = LogManager.getLogger(UploadObjectExample.class);
	
    public static void processFile(Path path, String filePath, PropertiesManager pm, ConfigFileAuthenticationDetailsProvider provider, String bName) {
    	if(path != null && filePath != null) {
    		try {
    			String pathValue = path.toString().replace("\\", "/");
    			if(filePath != null && !filePath.endsWith("/") ) {
    				filePath = filePath + "/";
    			}
	    		String objectName = pathValue.replace(filePath.replace("\\", "/"), "");
	    		System.out.println("obejct name::"+objectName);
	            Map<String, String> metadata = null;
	            String contentType = "text/plain";
	            String contentEncoding = null;
	            String contentLanguage = null;
	           // Path path = Paths.get("D:\\iaas-storage\\test\\sree-test1.txt");
	            File body = path.toFile();
	            
	            String namespaceName = pm.getProperty("oci.namespace");
				String bucketName = (bName != null && StringUtils.isNotEmpty(bName)) ? bName : pm.getProperty("oci.bucketname");
	            ObjectStorage client = new ObjectStorageClient(provider);
	            client.setRegion(Region.register("us-ashburn-1", Realm.OC1, "iad"));
	
	            // configure upload settings as desired
	            UploadConfiguration uploadConfiguration =
	                    UploadConfiguration.builder()
	                            .allowMultipartUploads(true)
	                            .allowParallelUploads(true)
	                            .build();
	
	            UploadManager uploadManager = new UploadManager(client, uploadConfiguration);
	
	            PutObjectRequest request =
	                    PutObjectRequest.builder()
	                            .bucketName(bucketName)
	                            .namespaceName(namespaceName)
	                            .objectName(objectName)
	                            .contentType(contentType)
	                            .contentLanguage(contentLanguage)
	                            .contentEncoding(contentEncoding)
	                            .opcMeta(metadata)
	                            .build();
	
	            UploadRequest uploadDetails =
	                    UploadRequest.builder(body).allowOverwrite(true).build(request);
	
	            // upload request and print result
	            // if multi-part is used, and any part fails, the entire upload fails and will throw
	            // BmcException
	            UploadResponse response = uploadManager.upload(uploadDetails);
	            System.out.println(response);
	
	            // fetch the object just uploaded
	            GetObjectResponse getResponse =
	                    client.getObject(
	                            GetObjectRequest.builder()
	                                    .namespaceName(namespaceName)
	                                    .bucketName(bucketName)
	                                    .objectName(objectName)
	                                    .build());
	
	            // use the response's function to print the fetched object's metadata
	            System.out.println("response :::"+getResponse.getOpcMeta() + ":::status::" + getResponse.get__httpStatusCode__());
	            response = null;
	            getResponse = null;
	            uploadDetails = null;
	            request = null;
	            uploadManager = null;
	            client.close();
            
            }
    		catch(Exception ex) {
    			LOG.error("Exception processing file", ex);
    		}
    	}
    	
    }
	
	public static void main(String[] args) throws Exception {
    	if(args != null && args.length >= 2) {
    		String configPath = args[0];
    		String filePath = args[1];
    		String bucketName = "";
    		if( args.length > 2) {
    			bucketName = args[2];
    		}
    		
    		
    		try {
				List<Path> pathList = FileUtils.getFolderWalkTree(filePath);
				if(pathList != null && pathList.size() > 0) {
					PropertiesManager pm = new PropertiesManager("config.properties");
					
					// Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI
		            // config file
		            // "~/.oci/config", and a profile in that config with the name "DEFAULT". Make changes to
		            // the following
		            // line if needed and use ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
		
					 final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configPath);
			         final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
		            
					long t1 = System.currentTimeMillis();
					for(Path path : pathList) {
						System.out.println("Start processing file : "+path.toString());
						long t3 = System.currentTimeMillis();
						processFile(path, filePath, pm, provider, bucketName);
						long t4 = System.currentTimeMillis();
						long timeDiff1 = (t4 - t3)/1000;
						System.out.println("Time taken upload file : "+timeDiff1);
					}
					
					long t2 = System.currentTimeMillis();
					long timeDiff = (t2 - t1)/1000;
					System.out.println("Time taken to process files : "+timeDiff);
				}
			} catch (Exception ex) {
				LOG.error("Exception processing files", ex);
				System.out.println("Exception processing files");
			}
	    } else {
			System.out.println("Invalid arguments, only input file path is allowed");
		}
    }
}

