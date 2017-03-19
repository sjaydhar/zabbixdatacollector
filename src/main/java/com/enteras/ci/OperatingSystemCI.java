package com.enteras.ci;

import com.enteras.metrics.KPIOperatingSystemMetricsCI;

public class OperatingSystemCI extends SoftwareCI {
	
	public String osType;
	public String osName;
	public String hostName;
	public String ipAddress;
	
	public KPIOperatingSystemMetricsCI operatingSystemMetrics = new KPIOperatingSystemMetricsCI();
	
	public String getosType() {
		return osType;
	}
	public void setosType(String oSType) {
		osType = oSType;
	}
	public String getosName() {
		return osName;
	}
	public void setOSName(String oSName) {
		osName = oSName;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
}