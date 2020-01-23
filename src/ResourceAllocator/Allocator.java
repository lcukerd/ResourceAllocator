package ResourceAllocator;

import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ResourceAllocator.Model.Region;
import ResourceAllocator.Model.Result;
import ResourceAllocator.Model.Server;

public class Allocator {
	private static String instances;

	private static String getJSONString(ArrayList<Result> results) {
		JSONArray outputArr = new JSONArray();
		
		for (Result result: results) {
			JSONObject output = new JSONObject();
			output.put("region", result.region);
			output.put("total_cost",result.cost);
			JSONObject servers = new JSONObject();
			
			if (result.server._10xlarge != 0) {
				servers.put("10xlarge", result.server._10xlarge);
			}
			if (result.server._8xlarge != 0) {
				servers.put("8xlarge", result.server._8xlarge);
			}
			if (result.server._4xlarge != 0) {
				servers.put("4xlarge", result.server._4xlarge);
			}
			if (result.server._2xlarge != 0) {
				servers.put("2xlarge", result.server._2xlarge);
			}
			if (result.server.xlarge != 0) {
				servers.put("xlarge", result.server.xlarge);
			}
			if (result.server.large != 0) {
				servers.put("large", result.server.large);
			}
			output.put("servers", servers);
			
			outputArr.add(output);
		}
		

		return outputArr.toJSONString();

	}

	private static ArrayList<Region> parseInstances() {
		ArrayList<Region> regions = new ArrayList<>();
		try {
			JSONObject top = (JSONObject) new JSONParser().parse(instances);
			Iterator<String> locations = top.keySet().iterator();

			while (locations.hasNext()) {
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
			}

			return regions;

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	private static Result getCPU(Region region, int hours, double price) {
		Result r = new Result();
		Server s = new Server();
		price /= hours;
		double originalPrice = price; 

		while (price >= region.getLarge()) {
			if (price >= region.get10xlarge()) {

				s._10xlarge = (int) (price / region.get10xlarge());
				price = price % region.get10xlarge();

			} else if (price >= region.get8xlarge()) {

				s._8xlarge = (int) (price / region.get8xlarge());
				price = price % region.get8xlarge();

			} else if (price >= region.get4xlarge()) {

				s._4xlarge = (int) (price / region.get4xlarge());
				price = price % region.get4xlarge();

			} else if (price >= region.get2xlarge()) {

				s._2xlarge = (int) (price / region.get2xlarge());
				price = price % region.get2xlarge();

			} else if (price >= region.get2xlarge()) {

				s.xlarge = (int) (price / region.getXlarge());
				price = price % region.getXlarge();

			} else {
				
				s.large = (int) (price / region.getLarge());
				price = price % region.getLarge();
			}
		}
		
		r.region = region.getRegion();
		r.cost = (originalPrice - price) * hours;
		r.server = s;
		return r;
	}

	private static Result getPrice(Region region, int hours, int cpus) {
		Result r = new Result();
		Server s = new Server();
		double price = 0;

		while (cpus > 0) {
			if (cpus >= 32) {

				s._10xlarge = (int) (cpus / 32);
				cpus = cpus % 32;
				price += s._10xlarge * region.get10xlarge();

			} else if (cpus >= 16) {

				s._8xlarge = (int) (cpus / 16);
				cpus = cpus % 16;
				price += s._10xlarge * region.get8xlarge();

			} else if (cpus >= 8) {

				s._4xlarge = (int) (cpus / 8);
				cpus = cpus % 8;
				price += s._10xlarge * region.get4xlarge();

			} else if (cpus >= 4) {

				s._2xlarge = (int) (cpus / 4);
				cpus = cpus % 4;
				price += s._10xlarge * region.get2xlarge();

			} else if (cpus >= 2) {

				s.xlarge = (int) (cpus / 2);
				cpus = cpus % 2;
				price += s._10xlarge * region.getXlarge();

			} else {

				s.large = cpus;
				cpus = 0;
				price += s._10xlarge * region.getLarge();

			}
		}
		
		r.region = region.getRegion();
		r.cost = price * hours;
		r.server = s;
		return r;
	}

	private static String get_costs(int hours, int cpus, double price) {
		ArrayList<Region> regions = parseInstances();
		ArrayList<Result> results = new ArrayList<>();
		if (cpus == -1) {
			for (Region r : regions) {
				results.add(getCPU(r, hours, price));
			}
		} else if (price == -1) {
			for (Region r : regions) {
				results.add(getPrice(r, hours, cpus));
			}
		} else {
			for (Region r : regions) {
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
		System.out.println(get_costs(24, 115, -1));
		System.out.println(get_costs(8, -1, 29));
		System.out.println(get_costs(7, 214, 95));
	}
}
