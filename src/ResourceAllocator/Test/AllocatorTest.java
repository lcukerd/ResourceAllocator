package ResourceAllocator.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ResourceAllocator.Allocator;
import ResourceAllocator.Model.Result;
import ResourceAllocator.Model.Server;

class AllocatorTest {

	@Test
	void testgetJSONString() {
		Server s = new Server();
		s.large = 2;
		
		Result r1 = new Result();
		r1.region = "R1";
		r1.cost = (float) 35.5;
		r1.server = s;
		
		Result r2 = new Result();
		r2.region = "R2";
		r2.cost = (float) 32;
		r2.server = s;
		
		ArrayList<Result> results = new ArrayList<>();
		results.add(r1);
		results.add(r2);
		
		String output = Allocator.getJSONString(results);
		String expected = "[{\"servers\":{\"large\":2},\"total_cost\":32.0,\"region\":\"R2\"},{\"servers\":{\"large\":2},\"total_cost\":35.5,\"region\":\"R1\"}]";
		
		assertEquals(expected, output);
		
	}
	
	@Test
	void testgetCosts() {
		Allocator.instances = "{\"us-east\": {\"large\": 0.12,\"xlarge\": 0.23,\"2xlarge\": 0.45,\"4xlarge\": 0.774,\"8xlarge\": 1.4,\"10xlarge\": 0}}";
		String result = Allocator.get_costs(24, 115, -1);
		String expected = "[{\"servers\":{\"large\":1,\"xlarge\":1,\"8xlarge\":7},\"total_cost\":243.6,\"region\":\"us-east\"}]";
		
		assertEquals(expected, result);
	}

}
