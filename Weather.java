import java.io.*;
import java.net.*;
import com.google.gson.*;

public class Weather 
{
    final public String API_KEY = "af9ee88f4d630d1ffbadab9b3a4a916f";
    public String city;
    final public String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    public static Boolean metric = false; // true for metric, false for imperial
    
    // Weather values
    public double temperature;
    public double highTemp;
    public double lowTemp;

    public double getTemperature() // Returns temperature 
    {
    	return temperature;
    }


    public String getTempUnit() 
    {
    	// Metric or customary (imperial)
    	return metric ? "°C" : "°F";
    }

    public double getHighTemp() // Return highest temperature
    {
    	return highTemp;
    }
    
    public double getLowTemp() // Return lowest temperature 
    {
    	return lowTemp;
    }
    // Constructor receives name of city 
    public Weather(String city)throws IOException, BadAPIKeyException, InvalidCityException
    {
    	this.city = city;
    	setData();
    }
    
    // Receive API data
    public String GetAPIData(String url) throws IOException, BadAPIKeyException, InvalidCityException 
    {
    	URL apiUrl = new URL(url);
    	HttpURLConnection con = (HttpURLConnection) apiUrl.openConnection();
    	int code = con.getResponseCode();
    	if (code == 404) // Invalid city 
    	{
    		throw new InvalidCityException();
    	}	 
    	else if (code == 401 || code == 429) // bad API key
    	{
    		throw new BadAPIKeyException();
    	}
    	
    	// If everything works well
    	// take in information
    	BufferedReader line = new BufferedReader(new InputStreamReader(con.getInputStream()));
    	String inputLine;
    	StringBuffer content = new StringBuffer();
    	while ((inputLine = line.readLine()) != null) 
    	{
    		content.append(inputLine);
    	}
    	return content.toString();
    }
    
    public String unitFormat() 
    {
    	return metric ? "metric" : "imperial";
    }
    public void setData() throws IOException, BadAPIKeyException, InvalidCityException 
    {
    	// Format URL
    	StringBuffer url = new StringBuffer("");    	
    	try 
    	{
    		// Is number, assumes non alphanumeric zip code
    		url.append(BASE_URL + "?zip=" + Integer.parseInt(city));
    	} 
    	catch (NumberFormatException | NullPointerException nfe) 
    	{
    		// Is string
    		url.append(BASE_URL + "?q=" + URLEncoder.encode(city, "UTF-8"));
    	} 
    	finally 
    	{
    		// add remaining part of url
    		url.append("&appid=" + API_KEY + "&units=" + unitFormat());
    	}
    	String apiData = GetAPIData(url.toString());
    	// Create parser
    	JsonParser parser = new JsonParser();
    	JsonElement tree = parser.parse(apiData);
    	JsonObject root = tree.getAsJsonObject();
    	if (root.has("main")) // Return information of Json
    	{
    		JsonObject mainObject = root.get("main").getAsJsonObject();
    		temperature = mainObject.get("temp").getAsDouble();
    		highTemp = mainObject.get("temp_max").getAsDouble();
    		lowTemp = mainObject.get("temp_min").getAsDouble();
    	}
    	return;
    }
}
