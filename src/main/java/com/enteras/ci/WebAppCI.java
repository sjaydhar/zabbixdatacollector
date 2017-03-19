package com.enteras.ci;

/*import java.util.ArrayList;
import java.util.List;*/

import com.enteras.Helper;

public class WebAppCI extends GeneralAppCI{


	public String typeDetails;
	public String webServerVersion;
	public String gzipCompressionStatus;

	static {

		logger.debug("Loading class WebAppCI ...");

		String className = WebAppCI.class.getSimpleName();	
		classFieldMap.put(className, Helper.loadProperties(WebAppCI.class));

		logger.debug("Field property mapping");
		logger.debug("Key:" + className + " value: " + classFieldMap.get(className));
	}

	public WebAppCI() {
		// TODO Auto-generated constructor stub
		typeDetails = "Apache Tomcat";
		//kpiMetricsList.add("Hardware_KPI");
		//kpiMetricsList.add("WebApp_KPI");
		
	}

}
