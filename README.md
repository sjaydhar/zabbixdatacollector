# zabbixdatacollectorservice w/ Kafka
Open Source Zabbix API data collector service with Kafka Producer Integration

The purpose of this Data Collector is to templatize data collection as a service that leverages open source monitoring tools such as zabbix, Nagios etc. The initial code base provides zabbix integration for collecting various zabbix collected metrics, KPI's etc. The Data Collection Service is designed to run as a distributed service in a client/customer environment running Zabbix and sends the data over Kafka for consummption by different back end services. 

The data collector service can support multiple use cases in DevOps and Enterprise IT such as:
  - Operational intelligence
  - Security Operations
  - Diagnostics
  - Visualization of Application Services etc.

What is required to run the Data Collection Service:

1) You will need to have Zabbix Server and Zabbix agents as applicable on the servers that you are going to collect data from in the deployment environment. If you need to install zabbix or zabbix agents, please follow the instructions for zabbix installation from zabbix web site along with instructions for creating database for zabbix. 
  - Start zabbix java gateway
3) Kafka Installation on the Server. Follow Kafka installation instructions for deployment and topic creation.
  - start kafka topics using zookeper
  - start kafka broker
3) Apache Tomcat Web Server. Follow Tomcat server installation from the web  
  - Start Data Collection Service

