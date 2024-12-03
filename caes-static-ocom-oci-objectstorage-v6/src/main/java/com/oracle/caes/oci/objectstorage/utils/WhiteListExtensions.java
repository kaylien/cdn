package com.oracle.caes.oci.objectstorage.utils;

import java.util.*;

import com.oracle.bmc.util.internal.StringUtils;

public class WhiteListExtensions {
	
	// public List<String> extensions = new ArrayList<>();
	private PropertiesManager extensions = null;
	
	public WhiteListExtensions() {
		/*
		String str = "ai,arj,asc,au,avi,bmp,bz2,clp,crd,css,dcr,dir,doc,docm,docx,dot,dotm,dotx"
				+ ",dxr,eps,flv,gif,gtar,gz,gzip,hlp,hqx,htm,html,ics,java,jpe,jpeg,jpg,"
				+ "js,json,latex,ltx,m13,m14,m1v,m2v,m4a,man,md,mdb,mif,ml3,ml4,mny,mov,mp2,"
				+ "mp3,mpa,mpe,mpeg,mpg,mpga,mpp,mvb,oie,pdf,pgp,png,pot,potm,potx,ppa,ppam,"
				+ "pps,ppsm,ppsx,ppt,pptm,pptx,properties,ps,pub,pwz,qt,ra,ram,rar,rm,rmm,rtf,"
				+ "rtx,scd,sgm,sgml,sit,smi,smil,svg,swf,tar,tcl,tex,tgz,tif,tiff,trm,txt,uu,"
				+ "uue,vcs,vsd,vst,vsw,wav,wcp,wdb,wiz,wkb,wks,wmf,wmv,woff2,wp,wp4,wp5,wp6,wpd,"
				+ "wps,wri,wzs,xl,xla,xlam,xlb,xlc,xld,xlk,xll,xlm,xls,xlsb,xlsm,xlsx,xlt,xltm,"
				+ "xltx,xlv,xlw,xml,xsl,z,zip";
		extensions = Arrays.asList(str.split(","));
		*/
		extensions = new PropertiesManager("file-extensions.properties");
	}
	
	public boolean isWhiteListExtension(String extension) {
		if(extensions != null && extensions.prop.containsKey(extension)) {		
			return true;
		} else {
			return false;
		}
	}
	
	public String getExtension(String extension) {
		if(extensions != null && StringUtils.isNotEmpty(extension)) {
			return extensions.getProperty(extension);
		} else {
			return "";
		}
	}
	
}
