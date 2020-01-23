package ResourceAllocator;

import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ResourceAllocator.Model.Region;
import ResourceAllocator.Model.Result;

public class Allocator {
  private static String instances;

  private static String getJSONString(ArrayList<Result> results){
	  // TODO
	  
	  return "";

  }

  private static ArrayList<Region> parseInstances(){
	  ArrayList<Region> regions = new ArrayList<>();
	  try {
		JSONObject top = (JSONObject) new JSONParser().parse(instances);
		Iterator<String> locations = top.keySet().iterator();
				
		
		while(locations.hasNext()) {
			String location = locations.next();
			
			JSONObject servers = (JSONObject) top.get(location);
			double large = (double) servers.get("large");
			double xlarge = (double) servers.get("xlarge");
			double _2xlarge = (double) servers.get("2xlarge");
			double _4xlarge = (double) servers.get("4xlarge");
			double _8xlarge = (double) servers.get("8xlarge");
			double _10xlarge = (double) servers.get("10xlarge");
			
			
			Region region = new Region(location, large, xlarge, _2xlarge, _4xlarge, _8xlarge, _10xlarge);
			regions.add(region);
			System.out.println(location + large + xlarge + _2xlarge + _4xlarge + _8xlarge + _10xlarge);
		}
		
		return regions;
		
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}

  }

  private static Result getCPU(Region region, int hours, double price){
	  Result r = new Result();
	  // TODO
	  
	  return r;

  }

  private static Result getPrice(Region region, int hours, double cpus){
	  Result r = new Result();
	  // TODO
	  
	  return r;
  }

  private static String get_costs(int hours, int cpus, double price) {
    ArrayList<Region> regions = parseInstances();
    ArrayList<Result> results = new ArrayList<>();
    if (cpus == -1){
      for (Region r: regions){
        results.add(getCPU(r, hours, price));
      }
    }
    else if (price == -1) {
      for (Region r: regions){
        results.add(getPrice(r, hours, cpus));
      }
    }
    else{
      for (Region r: regions){
        Result result = getPrice(r, hours, cpus);
        if (result.cost <= price)
          results.add(result);
      }
    }

    return getJSONString(results);
  }

  public static void main(String args[]) {
    Scanner sc = new Scanner(System.in);
    instances = sc.nextLine();
    get_costs(24, 115, -1);
    get_costs(8, -1, 29);
    get_costs(7, 214, 95);
  }
}
