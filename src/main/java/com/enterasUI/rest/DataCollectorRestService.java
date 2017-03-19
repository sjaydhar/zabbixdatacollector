package com.enterasUI.rest;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enteras.Helper;
//import com.enteras.clientapi.DefaultZabbixApi;
import com.enteras.clientapi.ProtocolType;
import com.enteras.probe.EventProbe;
//import com.enteras.clientapi.ZabbixApi;
import com.enteras.probe.KPICollector;
import com.enteras.probe.KPICollectorProbe;
import com.enteras.probe.Probev1;
import com.enteras.zabbix.discovery.GeneralDiscoveryFlow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;


@Path("/service")
@Api(value = "/service", description = "Enteras REST API to handle requests")
public class DataCollectorRestService {

	static final Logger logger = LoggerFactory.getLogger(DataCollectorRestService.class);
	
	@GET
	@Path("getAllDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Sample Test for EnterasUI", httpMethod = "GET", notes = "UI Integration", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request successfully processed."),
	@ApiResponse(code = 500, message = "Internal server error."),
	@ApiResponse(code = 412, message = "Pre conditions failed.") })
	public Response getDetails() throws SQLException{

		int a = 100;
		int b = 120;

		return  response(a+b);

	}
	
	
	@GET
	@Path("getKPIDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Sample Test for EnterasUI", httpMethod = "GET", notes = "UI Integration", response = Response.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message = "Request successfully processed."),
	@ApiResponse(code = 500, message = "Internal server error."),
	@ApiResponse(code = 412, message = "Pre conditions failed.") })

	public JsonNode getKPIDetails(String jsonStr) {
		
		JsonNode jsonNode = Helper.convStrToJsonNode(jsonStr);
		
		String userName = jsonNode.get("username").asText();
		String zabbixUrl = jsonNode.get("url").asText();
		String hostId = jsonNode.get("hostIds").asText();
		String password = jsonNode.get("password").asText();
		String dcId = jsonNode.get("dataCollectorId").asText().replaceAll("\"", "");
		String selectedKPIMetrics = jsonNode.get("kpiMetrics").asText();
		
		JsonNode resultNode = KPICollector.getKPIMetrics(zabbixUrl, userName, password, hostId, dcId, selectedKPIMetrics);
		
		return  resultNode;

	}
	
		
	@GET
	@Path("autoDiscover")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Sample Test for EnterasUI", httpMethod = "GET", notes = "UI Integration", response = Response.class)
	@ApiResponses(value = { 
	@ApiResponse(code = 200, message = "Request successfully processed."),
	@ApiResponse(code = 500, message = "Internal server error."),
	@ApiResponse(code = 412, message = "Pre conditions failed.") })

	public Response autoDiscover(
			@ApiParam(required = true) @QueryParam("zabbixServerUrl") String url, 
			@ApiParam(required = true) @QueryParam("usrName") String userName,
			@ApiParam(required = true) @QueryParam("password") String password, 
			@ApiParam(required = true) @QueryParam("ipRange") String ipRange,
			@ApiParam(required = true) @QueryParam("protocol") ProtocolType protocol
			) {
		
		GeneralDiscoveryFlow generalDiscoveryFlow =  new GeneralDiscoveryFlow(url, userName, password);
		return  response (generalDiscoveryFlow.autoDiscover(ipRange, protocol) );

	}
	
	
	
	@POST
	@Path("startProbe")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "starts CI/KPI/Event probe", httpMethod = "POST", notes = "creates CI/KPI/Event probe to collect continuous data of metrics", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request successfully processed."),
	@ApiResponse(code = 500, message = "Internal server error."),
	@ApiResponse(code = 412, message = "Pre conditions failed.") })

	public Response startProbe(JsonNode jsonNode) {
		
		String url = jsonNode.get("zabbixUrl").asText();
		String userName = jsonNode.get("userName").asText();
		String password = jsonNode.get("password").asText();
		String interval = jsonNode.get("duration").asText();
		String probe = jsonNode.get("probeType").asText();
		
		if(probe.equalsIgnoreCase("CI")){
			return  response(Probev1.createProbe( url, userName,  password, interval ));
		}
		else if(probe.equalsIgnoreCase("KPI")){
			return response(KPICollectorProbe.createKpiProbe(url, userName, password, interval));
		}
		else if(probe.equalsIgnoreCase("Event")){
			return response(EventProbe.createProbe(url, userName, password, interval));
		}
		else{
			return response("Invalid parameter for probeType[CI/KPI/Event]");
		}

	}
	
	
	@POST
	@Path("stopProbe")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "stops CI/KPI/Event probe", httpMethod = "POST", notes = "stops CI/KPI/Event probe which collect continuous data of metrics", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request successfully processed."),
	@ApiResponse(code = 500, message = "Internal server error."),
	@ApiResponse(code = 412, message = "Pre conditions failed.") })	
	public Response stopProbe(JsonNode jsonNode) {
		
		String url = jsonNode.get("zabbixUrl").asText();
		String probe = jsonNode.get("probeType").asText();
		
		if(probe.equalsIgnoreCase("CI")){
			return  response(Probev1.stopProbe(url));
		}
		else if(probe.equalsIgnoreCase("KPI")){
			return  response(KPICollectorProbe.stopKPIProbe(url));
		}
		else if(probe.equalsIgnoreCase("Event")){
			return response(EventProbe.stopProbe(url));
		}
		else{
			return  response("Invalid parameter for probeType[CI/KPI/Event]");
		}

	}
	
	


	private Response response(Object entity) {

		return Response.status(Response.Status.OK).entity(convertToJSON(entity)).build();
	}

	public static String convertToJSON(Object entity) {
		
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			logger.error("Exception while converting String to JsonNode " + e);
			e.printStackTrace();
			throw new RuntimeException(e);
		} 


	}

}
