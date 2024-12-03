/**
 * 
 */
package com.oracle.caes.oci.objectstorage;

/**
 * @author svongolu
 *
 */
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.oracle.bmc.Realm;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import com.oracle.bmc.util.internal.StringUtils;
import com.oracle.caes.oci.objectstorage.utils.FileUtils;
import com.oracle.caes.oci.objectstorage.utils.PropertiesManager;
import com.oracle.caes.oci.objectstorage.utils.WhiteListExtensions;

/**
 * Example of using the simplified UploadManager to upload objects.
 *
 * <p>UploadManager can be configured to control how/when it does multi-part uploads, and manages
 * the underlying upload method. Clients construct a PutObjectRequest similar to what they normally
 * would.
 */
public class UploadObjects {

	private static final Logger LOG = LogManager.getLogger(UploadObjectExample.class);
	
    public static void processFile(Path path, String filePath, PropertiesManager pm, AuthenticationDetailsProvider provider, String contentType) {
    	if(path != null && filePath != null) {
    		try {
    			String pathValue = path.toString().replace("\\", "/");
    			if(filePath != null && !filePath.endsWith("/") ) {
    				filePath = filePath + "/";
    			}
	    		String objectName = pathValue.replace(filePath.replace("\\", "/"), "");
	    		System.out.println("obejct name::"+objectName);
	            File body = path.toFile();
	            
	            // String contentType = fileNameMap.getContentTypeFor(body.getName());
	            String namespaceName = pm.getProperty("oci.namespace");
				String bucketName =  pm.getProperty("oci.bucketname");
	            ObjectStorage client = new ObjectStorageClient(provider);
	            client.setRegion(Region.register(pm.getProperty("oci.region"), Realm.OC1, "iad"));
	
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
	                            //.contentLanguage(contentLanguage)
	                            //.contentEncoding(contentEncoding)
	                            //.opcMeta(metadata)
	                            .build();
	
	            UploadRequest uploadDetails =
	                    UploadRequest.builder(body).allowOverwrite(true).build(request);
	
	            // upload request and print result
	            // if multi-part is used, and any part fails, the entire upload fails and will throw
	            // BmcException
	            UploadResponse response = uploadManager.upload(uploadDetails);
	            // use the response's function to print the fetched object's metadata
	            System.out.println("response :::"+response.toString());
	            response = null;
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
    		String keyPath = args[0];
    		String filePath = args[1];
    		String configPath = null;
    		if( args.length > 2) {
    			configPath = args[2];
    		}
    		
    		try {
				List<Path> pathList = FileUtils.getFolderWalkTree(filePath);
				if(pathList != null && pathList.size() > 0) {
					PropertiesManager pm = null;
					if(configPath != null) {
						File cFile = new File(configPath);
						pm = new PropertiesManager(cFile);
					} else {
						pm = new PropertiesManager("config.properties");
					}
					
					String ociUser = pm.getProperty("oci.user");
					String ociTenancy = pm.getProperty("oci.tenancy");
					String ociFingerPrint = pm.getProperty("oci.fingerprint");
					// String ociRegion = pm.getProperty("oci.region");
					
					// Supplier<InputStream> privateKeySupplier = new SimplePrivateKeySupplier("D:/iaas-storage/.oci/keys/corp_login_idcs_sreenadha.pem");
					Supplier<InputStream> privateKeySupplier = new SimplePrivateKeySupplier(keyPath);
					
					AuthenticationDetailsProvider provider 
				    = SimpleAuthenticationDetailsProvider.builder()
				        .tenantId(ociTenancy)
				        .userId(ociUser)
				        .fingerprint(ociFingerPrint)
				        .privateKeySupplier(privateKeySupplier)
				        .build();
					
					// Configuring the AuthenticationDetailsProvider. It's assuming there is a default OCI
		            // config file
		            // "~/.oci/config", and a profile in that config with the name "DEFAULT". Make changes to
		            // the following
		            // line if needed and use ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
		
					// final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(configPath);
			         // final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
		            
					long t1 = System.currentTimeMillis();
					WhiteListExtensions wle = new WhiteListExtensions();
					for(Path path : pathList) {
						System.out.println("Start processing file : "+path.toString());
						long t3 = System.currentTimeMillis();
						String fileExternsion = Files.getFileExtension(path.getFileName().toString());
			            if(fileExternsion == null || StringUtils.isEmpty(fileExternsion) || wle.isWhiteListExtension(fileExternsion.toLowerCase())) {
			            	String contentType = wle.getExtension(fileExternsion.toLowerCase());
			            	if(contentType == null || StringUtils.isEmpty(contentType)) {
			            		contentType = "text/plain";
			            	}
			            	processFile(path, filePath, pm, provider, contentType);
			            } else {
			            	System.out.println("Ignoring the file name as Extension is not whitelisted:"+path.getFileName().toString());
			            }
						
						long t4 = System.currentTimeMillis();
						long timeDiff1 = (t4 - t3)/1000;
						System.out.println("Time taken upload file : "+timeDiff1);
					}
					
					System.out.println("total number of files : "+pathList.size());
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

