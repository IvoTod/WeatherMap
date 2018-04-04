package project;

import java.sql.*;
import java.lang.Math;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/Get")
public class Main {
	
	static Connection conn = init();
	
	public static Connection init(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://87.120.33.83:3306/WeatherMap?autoReconnect=true&useSSL=false", "ipproekt", "aesihoim");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String GetCoordinates() {
		
		
		JSONArray jsonarray = new JSONArray();
		//String ret = "";
		
		try {
			Statement stmt = conn.createStatement();
			ResultSet result_set = stmt.executeQuery("SELECT * FROM stations");
			
			
			while (result_set.next()) {
				
				Integer id = result_set.getInt("id");
				String name = result_set.getString("name");
				Double latitude = result_set.getDouble("latitude");
				Double longitude = result_set.getDouble("longitude");
				
				JSONObject  nest = new JSONObject ();
				
				nest.put("id", id);
				nest.put("name", name);
				nest.put("latitude", latitude);
				nest.put("longitude", longitude);
				
				jsonarray.add(nest);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return jsonarray.toJSONString();
	}
	
	@Path("/station")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String GetStationData( @QueryParam("id") int id) {
	
		JSONObject  data = new JSONObject ();
	
		try {
			PreparedStatement stmt = null;
			String query = "SELECT s.name, r.temperature, r.humidity, r.apm, r.airPressure FROM stations s " +
							"JOIN readings r ON r.stationID = ? AND r.stationID = s.ID;";
			
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			
			ResultSet result_set = stmt.executeQuery();
			result_set.next();
			String name = result_set.getString("name");
			Float humidity = result_set.getFloat("humidity");
			Float temperature = result_set.getFloat("temperature");
			Float apm = result_set.getFloat("apm");
			Float airPressure = result_set.getFloat("airPressure");
			
			data.put("name", name);
			data.put("humidity", humidity);
			data.put("temperature", temperature);
			data.put("apm", apm);
			data.put("airPressure", airPressure);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return data.toJSONString();
	}
	
	@Path("/average")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String GetAverageData( @QueryParam("id") int id) {
		
		JSONObject  avrdata = new JSONObject ();
		try {
			PreparedStatement stmt = null;
			String query = "SELECT * FROM readings WHERE stationID = ? " +
							"UNION SELECT * FROM (SELECT * FROM readingsArchive WHERE stationID = ? ORDER BY time DESC) as a;";
			
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			stmt.setInt(2, id);
			
			ResultSet result_set = stmt.executeQuery();
			
			Float humidity = 0.0f, temperature = 0.0f, apm = 0.0f, airPressure = 0.0f; 
			Integer count = 0;
			Float twohour_hum = 0.0f, twohour_temp = 0.0f, twohour_apm = 0.0f, twohour_air = 0.0f;
			Float fivehour_hum = 0.0f, fivehour_temp = 0.0f, fivehour_apm = 0.0f, fivehour_air = 0.0f;
			Float twelvehour_hum = 0.0f, twelvehour_temp = 0.0f, twelvehour_apm = 0.0f, twelvehour_air = 0.0f;

			while(result_set.next()){
				humidity = result_set.getFloat("humidity");
				temperature = result_set.getFloat("temperature");
				apm = result_set.getFloat("apm");
				airPressure = result_set.getFloat("airPressure");
				
				if(count < 2){
					twohour_hum += humidity;
					twohour_temp += temperature;
					twohour_apm += apm;
					twohour_air += airPressure;
				}
				
				if(count < 5){
					fivehour_hum += humidity;
					fivehour_temp += temperature;
					fivehour_apm += apm;
					fivehour_air += airPressure;
				}
				
				if(count < 12){
					twelvehour_hum += humidity;
					twelvehour_temp += temperature;
					twelvehour_apm += apm;
					twelvehour_air += airPressure;
				}

				count++;
			}
			
			if(count >= 2){
				twohour_hum = twohour_hum / 2;	
				twohour_temp = twohour_temp / 2;
				twohour_apm = twohour_apm / 2;
				twohour_air = twohour_air / 2;
				
				JSONObject  twohour_data = new JSONObject ();
				
				twohour_data.put("humidity", twohour_hum);
				twohour_data.put("temperature", twohour_temp);
				twohour_data.put("apm", twohour_apm);
				twohour_data.put("airPressure",twohour_air);
				
				avrdata.put("2hr", twohour_data);
			}
			
			if(count >= 5){
				fivehour_hum = fivehour_hum / 5;	
				fivehour_temp = fivehour_temp / 5;	
				fivehour_apm = fivehour_apm / 5;	
				fivehour_air = fivehour_air / 5;	
	
				JSONObject  fivehour_data = new JSONObject ();
				
				fivehour_data.put("humidity", fivehour_hum);
				fivehour_data.put("temperature", fivehour_temp);
				fivehour_data.put("apm", fivehour_apm);
				fivehour_data.put("airPressure", fivehour_air);
				
				avrdata.put("5hr", fivehour_data);
			}
			
			if(count >= 12){
				twelvehour_hum = twelvehour_hum / 12;	
				twelvehour_temp = twelvehour_temp / 12;	
				twelvehour_apm = twelvehour_apm / 12;	
				twelvehour_air = twelvehour_air / 12;	
	
				JSONObject  twelvehour_data = new JSONObject ();
				
				twelvehour_data.put("humidity", twelvehour_hum);
				twelvehour_data.put("temperature", twelvehour_temp);
				twelvehour_data.put("apm", twelvehour_apm);
				twelvehour_data.put("airPressure", twelvehour_air);
				
				avrdata.put("12hr", twelvehour_data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return avrdata.toJSONString();
	}
	
	
}
