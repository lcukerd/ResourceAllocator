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

	// Convert Json List to String
	private static String getJSONString(ArrayList<Result> results) {
		JSONArray outputArr = new JSONArray();
		
		// Sort list by cost
		results.sort(new Comparator<Result>() {

			@Override
			public int compare(Result o1, Result o2) {
				if (o1.cost < o2.cost)
					return -1;
				else if (o1.cost > o2.cost)
					return 1;
				else
					return 0;
			}
			
		});

		for (Result result : results) {
			JSONObject output = new JSONObject();
			output.put("region", result.region);
			output.put("total_cost", result.cost);
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

	// Parse Json String to Objects
	private static ArrayList<Region> parseInstances() {
		ArrayList<Region> regions = new ArrayList<>();
		try {
			JSONObject top = (JSONObject) new JSONParser().parse(instances);
			Iterator<String> locations = top.keySet().iterator();

			while (locations.hasNext()) {
				String location = locations.next();

				JSONObject servers = (JSONObject) top.get(location);
				double large = -1;
				double xlarge = -1;
				double _2xlarge = -1;
				double _4xlarge = -1;
				double _8xlarge = -1;
				double _10xlarge = -1;

				if (servers.containsKey("large"))
					large = Double.valueOf(servers.get("large").toString());
				if (servers.containsKey("xlarge"))
					xlarge = Double.valueOf(servers.get("xlarge").toString());
				if (servers.containsKey("2xlarge"))
					_2xlarge = Double.valueOf(servers.get("2xlarge").toString());
				if (servers.containsKey("4xlarge"))
					_4xlarge = Double.valueOf(servers.get("4xlarge").toString());
				if (servers.containsKey("8xlarge"))
					_8xlarge = Double.valueOf(servers.get("8xlarge").toString());
				if (servers.containsKey("10xlarge"))
					_10xlarge = Double.valueOf(servers.get("10xlarge").toString());

				Region region = new Region(location, large, xlarge, _2xlarge, _4xlarge, _8xlarge, _10xlarge);
				regions.add(region);
			}

			return regions;

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	// Allocator when max cpu count is not given by user
	private static Result getCPU(Region region, int hours, double price) {
		Result r = new Result();
		Server s = new Server();
		price /= hours;
		double originalPrice = price;

		while (price >= region.getLarge()) {
			if (region.get10xlarge() > 0 && price >= region.get10xlarge()) {

				s._10xlarge = (int) (price / region.get10xlarge());
				price = price % region.get10xlarge();

			} else if (region.get8xlarge() > 0 && price >= region.get8xlarge()) {

				s._8xlarge = (int) (price / region.get8xlarge());
				price = price % region.get8xlarge();

			} else if (region.get4xlarge() > 0 && price >= region.get4xlarge()) {

				s._4xlarge = (int) (price / region.get4xlarge());
				price = price % region.get4xlarge();

			} else if (region.get2xlarge() > 0 && price >= region.get2xlarge()) {

				s._2xlarge = (int) (price / region.get2xlarge());
				price = price % region.get2xlarge();

			} else if (region.getXlarge() > 0 && price >= region.get2xlarge()) {

				s.xlarge = (int) (price / region.getXlarge());
				price = price % region.getXlarge();

			} else if (region.getLarge() > 0) {

				s.large = (int) (price / region.getLarge());
				price = price % region.getLarge();
			} else {
				break;
			}
		}

		r.region = region.getRegion();
		r.cost = (float) (originalPrice - price) * hours;
		r.server = s;
		return r;
	}

	// Allocator when cpu count is given by user
	private static Result getPrice(Region region, int hours, int cpus) {
		Result r = new Result();
		Server s = new Server();
		double price = 0;

		while (cpus > 0) {
			if (region.get10xlarge() > 0 && cpus >= 32) {

				s._10xlarge = (int) (cpus / 32);
				cpus = cpus % 32;
				price += s._10xlarge * region.get10xlarge();

			} else if (region.get8xlarge() > 0 && cpus >= 16) {

				s._8xlarge = (int) (cpus / 16);
				cpus = cpus % 16;
				price += s._8xlarge * region.get8xlarge();

			} else if (region.get4xlarge() > 0 && cpus >= 8) {

				s._4xlarge = (int) (cpus / 8);
				cpus = cpus % 8;
				price += s._4xlarge * region.get4xlarge();

			} else if (region.get2xlarge() > 0 && cpus >= 4) {

				s._2xlarge = (int) (cpus / 4);
				cpus = cpus % 4;
				price += s._2xlarge * region.get2xlarge();

			} else if (region.getXlarge() > 0 && cpus >= 2) {

				s.xlarge = (int) (cpus / 2);
				cpus = cpus % 2;
				price += s.xlarge * region.getXlarge();

			} else if (region.getLarge() > 0){

				s.large = cpus;
				cpus = 0;
				price += s.large * region.getLarge();

			} else {
				break;
			}
		}
		
		if (cpus > 0)
			return null;

		r.region = region.getRegion();
		r.cost = (float) (price * hours);
		r.server = s;
		return r;
	}

	// Main Allocator fn
	private static String get_costs(int hours, int cpus, double price) {
		ArrayList<Region> regions = parseInstances();
		ArrayList<Result> results = new ArrayList<>();
		if (cpus == -1) {
			for (Region r : regions) {
				results.add(getCPU(r, hours, price));
			}
		} else if (price == -1) {
			for (Region r : regions) {
				Result result = getPrice(r, hours, cpus);
				
				// When CPU can't be met
				if (result != null)
					results.add(result);
			}
		} else {
			for (Region r : regions) {
				Result result = getPrice(r, hours, cpus);
				
				// Don't add if price is more than what was asked by user
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
		System.out.println(get_costs(7, 214, 205));
	}
}
