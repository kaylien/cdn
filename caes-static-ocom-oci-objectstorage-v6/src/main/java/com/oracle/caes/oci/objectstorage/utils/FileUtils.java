package com.oracle.caes.oci.objectstorage.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtils {
	private static final Logger LOG = LogManager.getLogger(FileUtils.class);
	
	public static List<Path> getFolderWalkTree(String folder){
		List<Path> pathList = new ArrayList<>();
		try (Stream<Path> stream = Files.walk(Paths.get(folder))) {
			  pathList = stream.map(Path::normalize)
			        .filter(Files::isRegularFile)
			        .collect(Collectors.toList());
		}
		catch (Exception ex) {
			LOG.error("Exception reading File walk tree:"+folder, ex);
		}
			
		return pathList;
	}
	
	public static void main(String args[]) {
		List<Path> pathList = getFolderWalkTree("D:/iaas-storage/test");
		// pathList.forEach(System.out::println);
		for(Path path : pathList) {
			System.out.println("filename:"+path.getFileName());
			System.out.println("uri:"+path.toUri());
			// System.out.println("splitter:"+path.);
		}
	}
}

