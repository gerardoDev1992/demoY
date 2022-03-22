package com.example.demo.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.stereotype.Service;

import com.example.demo.entities.Suggestion;
/** Represents a Suggestion Service.
 * @author Gerardo Leyva 
 * @version 1.0
*/

@Service
public class SuggestionService {
	
	 /**
     * This method convert a .tsv file to stream to manipulate the info 
     * with this method get all necessary info
     * @return  ArrayList posible  Suggestion
     */	
	public ArrayList<Suggestion> getCities() {		
		ArrayList<Suggestion> cities=new ArrayList<Suggestion>();
		try {
				    
		      
		      BufferedReader reader = new BufferedReader(new FileReader(
						"cities_canada-usa.tsv"));
		      Scanner myReader = new Scanner(reader);
		      int i=0;
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        if(i>0) {
		        String[] tokens = data.split("\t");		        		       
		        Suggestion city=new Suggestion();
		        city.setName(tokens[1]+", "+tokens[10]+", "+tokens[8]);		        
		        city.setLatitude(Double.parseDouble(tokens[4]));
		        city.setLongitude(Double.parseDouble(tokens[5]));		        
		        cities.add(city);
		        }
		        i++;
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		return cities;
	}
	 /**
     * This method is for calculate the score between two points     
     * @param oriLatitude  Double value represent a point
     * @param oriLongitude  Double value represent a point  
     * @param des Suggestion this object contains a posible Suggestion 
     * @return score Double the calculated value for the Suggestion
     */	
	public Double score( double oriLatitude,double oriLongitude,Suggestion des) {
		DecimalFormat df = new DecimalFormat("#.#");
		double latitud = Math.abs(des.getLatitude() - oriLatitude);
		double longi= Math.abs(des.getLongitude() - oriLongitude);
		double score = 10 - (latitud + longi) / 2;
		
		score=score/10;
	    if(score>0 && score<=1) {
	    	score = Double.valueOf(df.format(score));		    	
	    }else {
	    	score=0;
	    }		    		 
	    return score;
	}

	
	 /**
     * This method get suggestions and sort based on his score
     * @param name String name of the posible Suggestion     
     * @param lat  Double value represent a point
     * @param lon  Double value represent a point   
     * @return ass  String JSONOBject of suggestions
     */	
	public String scoredSuggestedMatches(String name,Double lat,Double lon) {			
		
		List<Suggestion> filtList = getCities().stream().
		         filter(value -> value.getName().toUpperCase().
		         contains(name.toUpperCase())).collect(Collectors.toList());
		
		filtList.forEach(x->x.setScore(score(lat,lon ,x)));
		
		
		List<Suggestion> sortedList = filtList.stream()
		        .sorted(Comparator.comparingDouble(Suggestion::getScore).reversed())
		        .collect(Collectors.toList());
		
		JSONArray arr=new JSONArray();
		JSONObject as=null;
		for(Suggestion suggestion : sortedList) {
			as=new JSONObject();
								
			as.put("latitude", suggestion.getLatitude());
			as.put("longitud", suggestion.getLongitude());
			as.put("score", suggestion.getScore());
			as.put("name", suggestion.getName());	
			
			arr.put(as);
			
			
		}
	
		
		JSONObject ass=new JSONObject();
		
		ass.put("suggestions", arr);
		//
		
		
		
		return ass.toString();
		
		
	}

}
