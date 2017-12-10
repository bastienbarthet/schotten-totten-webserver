package server;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ServerTests {

	@Test
	public void TestGetLocal() throws ClientProtocolException, IOException {
		TestGet("http://localhost:8080/");
	}
	@Test
	public void TestGetRemote() throws ClientProtocolException, IOException {
		TestGet("https://schotten-totten.herokuapp.com/");
	}
	
	private void TestGet(final String url) throws ClientProtocolException, IOException {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-2" + System.currentTimeMillis();
		final Boolean result = rest.getForObject(url + "createGame?gamename=" + gamename, Boolean.class);
		//		System.out.println(result);
		Assert.assertTrue(result);
		
		final CloseableHttpClient httpclient = HttpClients.createDefault();
		final HttpGet httpGet = new HttpGet(url + "getMilestones?gamename=" + gamename);
		final CloseableHttpResponse response = httpclient.execute(httpGet);
		try {
		    System.out.println("status: " + response.getStatusLine());
		    HttpEntity entity = response.getEntity();
		    System.out.println("json: " + IOUtils.toString(entity.getContent()));
		    EntityUtils.consume(entity);
		} finally {
		    response.close();
		}
	}
	
}
