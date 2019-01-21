package code;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.net.*;

public class TestUsersAPI {

	@Test
	public void TestAPI() throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
     
		final String baseUrl = "http://my-json-server.typicode.com/skalsi316/restapi/users";
		URI uri = new URI(baseUrl);
 
		ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
     
		//Verify request succeed
		assertEquals(200, result.getStatusCodeValue());
		assertEquals(true, result.getBody().contains("msisdn"));
	}
}