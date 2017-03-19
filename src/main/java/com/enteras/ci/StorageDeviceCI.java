package com.enteras.ci;

import com.enteras.metrics.KPIStorageMetricsCI;

public class StorageDeviceCI extends HardwareCI{
	
	public String vendor;
	public String storageType;
	public String processor;
	public int totalDiskDrives;
	
	public KPIStorageMetricsCI storageMetrics = new KPIStorageMetricsCI();
	
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getStorageType() {
		return storageType;
	}
	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}
	public String getProcessor() {
		return processor;
	}
	public void setProcessor(String processor) {
		this.processor = processor;
	}
	public int getTotalDiskDrives() {
		return totalDiskDrives;
	}
	public void setTotalDiskDrives(int totalDiskDrives) {
		this.totalDiskDrives = totalDiskDrives;
	}
	

}
