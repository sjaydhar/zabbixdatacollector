package com.enteras.clientapi;

public enum KPIMetricType {
	
	Hardware(){
		
	},	
	Network(){
		
	},
	Storage(){
		
	},
	EventProbe(){
		
	},
	WebApp(){
		
	};
	
	public String check = "hello";
	
	public static void main(String[] args){
		KPIMetricType hwd = KPIMetricType.valueOf("EventProbe");
		System.out.println(hwd.check);
	}

}
