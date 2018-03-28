package project;

import java.sql.*;

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
		
		JSONObject  nest = new JSONObject ();
		try {
			PreparedStatement stmt = null;
			String query = "SELECT r.temperature, r.humidity, r.apm, r.airPressure FROM stations s " +
							"JOIN readings r ON r.stationID = ? AND r.stationID = s.ID;";
			
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			
			ResultSet result_set = stmt.executeQuery();
			result_set.next();
			Integer humidity = result_set.getInt("humidity");
			Integer temperature = result_set.getInt("temperature");
			Integer apm = result_set.getInt("apm");
			Integer airPressure = result_set.getInt("airPressure");
				
			nest.put("humidity", humidity);
			nest.put("temperature", temperature);
			nest.put("apm", apm);
			nest.put("airPressure", airPressure);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return nest.toJSONString();
	}
	
	
}
