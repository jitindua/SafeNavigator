package com.app.nwhacks.safenavigator;

import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.*;


/*int RECOVERED = 1;
int FRAUD = 1;
int EMBEZZLEMENT = 1;
int VANDALISM = 1;
int SECONDARY = 1;
int OTHER = 1;
int LIQUOR = 1;
int SEX = 1;
int DRUG = 1;
int BAD = 1;
int FORGERY = 1;
int ASSAULT = 1;
int WEAPON = 1;
int ARSON = 1;
int SUICIDE = 1;
int VEHICLE = 1;
int ROBBERY = 1;
int MISSING = 1;
int PROSTITUTION = 1;
int EXTORTION = 1;
int TRESPASS = 1;
int RUNAWAY = 1;
int SUSPICIOUS = 1;
int DRIVING = 1;
int BRIBERY = 1;
int LARCENY = 1;
int STOLEN = 1;
int DISORDERLY = 1;
int FAMILY = 1;
int GAMBLING = 1;
int NON = 1;
int LOITERING = 1;
int PORNOGRAPHY = 1;
int WARRANTS = 1;
int BURGLARY = 1;
int KIDNAPPING = 1;
int TREA = 1;
int DRUNKENNESS = 1;*/

public class DataExtractor{
	
	Map<String, Map<String, Integer>> maps = new HashMap<String, Map<String, Integer>>();
	Map<String, Integer> crimeSeverity = new HashMap<String, Integer>();
	ArrayList<Double> latitude = new ArrayList<Double>();
	ArrayList<Double> longitude = new ArrayList<Double>();
	
 /* public static void main(String[] args) {

	DataExtractor obj = new DataExtractor();
	obj.run();

  }*/

	//private InputStream is;
	public DataExtractor(InputStream is){
		//this.is = is;
		run(is);
	}


  
  public double getScore(double lon, double lat, int time, double radius)
  {
	  /*try
	  {
		  InputStream fis = is;
		  if(is == null) {
			  System.out.println("NULL NULL");
		  }

		  ObjectInputStream ois = new ObjectInputStream((FileInputStream)fis);
		  maps = (HashMap) ois.readObject();
		  ois.close();
		  fis.close();
	  }catch(IOException ioe)
	  {
		  ioe.printStackTrace();
	  }catch(ClassNotFoundException e){
		  e.printStackTrace();
	  }

*/
	  //pick radius 0.0005
	  double score = 0;
	  double lonLowRange = lon + radius;
	  double latLowRange = lat - radius;
	  
	  double lonHighRange = lon - radius;
	  double latHighRange = lat + radius;
	  Map<String, Integer> visitedMap = new HashMap<String, Integer>();
	  
	  int dataLen = latitude.size();
	  
	  for(int i=0; i<dataLen; i++)
	  {
		  double longi, lati;
		  longi = longitude.get(i);
		  lati = latitude.get(i);
		  String tempKey = String.valueOf(longi).concat("|").concat(String.valueOf(lati));
		  
		  if(visitedMap.containsKey(tempKey))
		  {
			  // Do nothing;
		  }
		  else
		  {
		  if(longi < lonLowRange && longi > lonHighRange && lati < latHighRange && lati > latLowRange)
		  {
			  String key = String.valueOf(longi).concat("|").concat(String.valueOf(lati)).concat("|").concat(String.valueOf(time));
			  
			  if(maps.containsKey(key))
				{
					Map<String, Integer> crime = maps.get(key);
					for (Map.Entry<String, Integer> crimeEntry : crime.entrySet()) 
					{
						System.out.println("key: " + key);
						int sev = crimeSeverity.get(crimeEntry.getKey());
						int num = crimeEntry.getValue();
						score = score + sev*num;
					}
				}
		  }
		  visitedMap.put(tempKey, new Integer(1));
		  }
	  }
	  
	  return score;
  }

  public void assignSeverity()
  {
		crimeSeverity.put("RECOVERED VEHICLE", new Integer(1));
		crimeSeverity.put("FRAUD", new Integer(3));
		crimeSeverity.put("EMBEZZLEMENT", new Integer(1));
		crimeSeverity.put("VANDALISM", new Integer(5));
		crimeSeverity.put("SECONDARY CODES", new Integer(1));
		crimeSeverity.put("OTHER OFFENSES", new Integer(1));
		crimeSeverity.put("LIQUOR LAWS", new Integer(3));
		crimeSeverity.put("SEX OFFENSES NON FORCIBLE", new Integer(3));
		crimeSeverity.put("DRUG/NARCOTIC", new Integer(8));
		crimeSeverity.put("SEX OFFENSES FORCIBLE", new Integer(5));
		crimeSeverity.put("BAD CHECKS", new Integer(1));
		crimeSeverity.put("FORGERY/COUNTERFEITING", new Integer(3));
		crimeSeverity.put("ASSAULT", new Integer(8));
		crimeSeverity.put("WEAPON LAWS", new Integer(8));
		crimeSeverity.put("ARSON", new Integer(5));
		crimeSeverity.put("SUICIDE", new Integer(3));
		crimeSeverity.put("VEHICLE THEFT", new Integer(10));
		crimeSeverity.put("ROBBERY", new Integer(8));
		crimeSeverity.put("MISSING PERSON", new Integer(5));
		crimeSeverity.put("PROSTITUTION", new Integer(3));
		crimeSeverity.put("EXTORTION", new Integer(8));
		crimeSeverity.put("TRESPASS", new Integer(3));
		crimeSeverity.put("RUNAWAY", new Integer(3));
		crimeSeverity.put("SUSPICIOUS OCC", new Integer(3));
		crimeSeverity.put("DRIVING UNDER THE INFLUENCE", new Integer(10));
		crimeSeverity.put("BRIBERY", new Integer(3));
		crimeSeverity.put("LARCENY/THEFT", new Integer(5));
		crimeSeverity.put("STOLEN PROPERTY", new Integer(3));
		crimeSeverity.put("DISORDERLY CONDUCT", new Integer(3));
		crimeSeverity.put("FAMILY OFFENSES", new Integer(1));
		crimeSeverity.put("GAMBLING", new Integer(1));
		crimeSeverity.put("NON-CRIMINAL", new Integer(1));
		crimeSeverity.put("LOITERING", new Integer(1));
		crimeSeverity.put("PORNOGRAPHY/OBSCENE MAT", new Integer(1));
		crimeSeverity.put("WARRANTS", new Integer(3));
		crimeSeverity.put("BURGLARY", new Integer(7));
		crimeSeverity.put("KIDNAPPING", new Integer(10));
		crimeSeverity.put("TREA", new Integer(7));
		crimeSeverity.put("DRUNKENNESS", new Integer(5));
		
  }
  
  private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	//JITIN
	private ArrayList<ArrayList<LatLng>> points = null;
	public ArrayList<ArrayList<LatLng>> GetLatLangList()
	{
		return points;
	}


	public Map<Integer, Double> calcScore(JSONObject jObject)
	{
		Map<Integer, Double> scoreMap = new HashMap<Integer, Double>();
		List<List<HashMap<String, Double>>> routes;

		//TODO: UPDATE TIME
		int hour = new Date(System.currentTimeMillis()).getHours();
		routes = parse(jObject);

		for(int i=0; i<routes.size(); i++)
		{
			double scoring = 0;
			List<HashMap<String, Double>> tmpRoute = routes.get(i);
			for(int j=0; j<tmpRoute.size(); j++)
			{
				HashMap<String, Double> cor = tmpRoute.get(j);
				scoring += getScore(cor.get("lng"),cor.get("lat"), hour, 0.0005);
			}
			//first route represented by '0'. Considering maintaining order
			scoreMap.put(i, scoring);
		}

		return scoreMap;
	}

	/** Receives a JSONObject and returns a list of lists containing latitude and longitude */
	private List<List<HashMap<String,Double>>> parse(JSONObject jObject){

		List<List<HashMap<String, Double>>> routes = new ArrayList<List<HashMap<String,Double>>>() ;
		//JITIN
		points = new ArrayList<ArrayList<LatLng>>();


		try{

	  /*InputStream is =
              DataExtractor.class.getResourceAsStream( "jsonFile.json");

	  String jsonTxt = getStringFromInputStream(is);
      //String jsonTxt = IOUtils.toString( is );

      jObject = (JSONObject) JSONSerializer.toJSON( jsonTxt );*/

			JSONArray jRoutes = null;
			JSONArray jLegs = null;
			JSONArray jSteps = null;

			jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			for(int i=0; i<jRoutes.length();i++){

				//JITIN
				ArrayList<LatLng> singleRoutePoints = new ArrayList<LatLng>();

				jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
				List path = new ArrayList<HashMap<String, Double>>();

				/** Traversing all legs */
				for(int j=0;j<jLegs.length();j++){
					jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

					/** Traversing all steps */
					for(int k=0;k<jSteps.length();k++){
						String polyline = "";
						polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
						List<LatLong> list = decodePoly(polyline);

						/** Traversing all points */
						for(int l=0;l<list.size();l++){
							HashMap<String, Double> hm = new HashMap<String, Double>();
							hm.put("lat", ((LatLong)list.get(l)).latitude);
							hm.put("lng", ((LatLong)list.get(l)).longitude);
							path.add(hm);

							// JITIN
							double lat = ((LatLong)list.get(l)).latitude;
							double lng = ((LatLong)list.get(l)).longitude;
							LatLng position = new LatLng(lat, lng);
							singleRoutePoints.add(position);
						}
					}
					routes.add(path);
				}
				points.add(singleRoutePoints);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e){
		}

		System.out.println("sjkdkjn" + routes.size());
		return routes;
	}

  private List<LatLong> decodePoly(String encoded) {

      List<LatLong> poly = new ArrayList<LatLong>();
      int index = 0, len = encoded.length();
      int lat = 0, lng = 0;

      while (index < len) {
          int b, shift = 0, result = 0;
          do {
              b = encoded.charAt(index++) - 63;
              result |= (b & 0x1f) << shift;
              shift += 5;
          } while (b >= 0x20);
          int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
          lat += dlat;

          shift = 0;
          result = 0;
          do {
              b = encoded.charAt(index++) - 63;
              result |= (b & 0x1f) << shift;
              shift += 5;
          } while (b >= 0x20);
          int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
          lng += dlng;

          LatLong p = new LatLong((((double) lat / 1E5)),
                  (((double) lng / 1E5)));
          poly.add(p);
      }

      return poly;
  }
  
  public void jsonParser(JSONObject js)
  {
	  
  }

  public void run(InputStream is) {
	  //Environment.getRootDirectory()
	  System.out.println("Inside Run : ");
	//String csvFile = "res/raw/crimeData.csv";
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	Map<String, Integer> crimeTypes = new HashMap<String, Integer>();
	int corCount = -1;
	
	try {
		
		assignSeverity();
		
		//br = new BufferedReader(new FileReader(csvFile));
		br = new BufferedReader(new InputStreamReader(is,"UTF-8"));

		while ((line = br.readLine()) != null) {

			// use comma as separator
			corCount++;
			String[] data = line.split(cvsSplitBy);
			longitude.add(corCount, new Double(data[2]));
			latitude.add(corCount, new Double(data[3]));
			String location = data[2].concat("|").concat(data[3]);
			
			//String[] time = data[0].split(" ");
			//String key = location.concat("|").concat(time[1].substring(0,2));
			String key = data[0];
			crimeTypes.put(data[1], new Integer(1));

			if(maps.containsKey(key))
			{
				Map<String, Integer> crime = maps.get(key);
				
				if(crime.containsKey(data[1]))
				{
					Integer count = new Integer(crime.get(data[1])) + 1;
					crime.put(data[1], count);
				}
				else
				{
					crime.put(data[1], new Integer(1));
				}
			}
			else
			{
				Map<String, Integer> crime = new HashMap<String, Integer>();
				crime.put(data[1], new Integer(1));
				maps.put(key, crime);
			}
		}

		System.out.println("Line Count : " + corCount);
		int counter = 0;
		//loop map
		/*for (Map.Entry<String, Map<String, Integer>> entry : maps.entrySet()) {
			for (Map.Entry<String, Integer> crimeEntry : entry.getValue().entrySet()) 
			{
				if(counter < 50)
				System.out.println("int " + entry.getKey() + " , crime =" + crimeEntry.getKey() + " , count=" + crimeEntry.getValue());
			}
			counter++;
		}*/
		
		/*for (Map.Entry<String, Integer> entry : crimeTypes.entrySet()) {
			System.out.println("int " + entry.getKey());
		}*/

		
		//test function for score
		//double scoring = getScore(-122.4609215,37.70791996, 22, 0.0005);
		//System.out.println("score: " + scoring);
		
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//parse(null);
	System.out.println("Done");
  }

}
