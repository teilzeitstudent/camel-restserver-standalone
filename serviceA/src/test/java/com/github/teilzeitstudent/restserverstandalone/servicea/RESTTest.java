package com.github.teilzeitstudent.restserverstandalone.servicea;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.camel.StringSource;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.teilzeitstudent.restserverstandalone.model.GreetingRequest;
import com.github.teilzeitstudent.restserverstandalone.model.GreetingResponse;

public class RESTTest extends CamelBlueprintTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTTest.class);
    
    @Override
    protected String getBlueprintDescriptor() {
        return "OSGI-INF/blueprint/blueprint.xml";
    }
    
    private HttpGet buildGet(String accept) {
        HttpGet getRequest = new HttpGet("http://localhost:8080/greeting/hello/bob");
        getRequest.setHeader("Accept", accept);
        
        return getRequest;
    }
    
    private String exec(HttpRequestBase request) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(request);

        String responseBody = EntityUtils.toString(response.getEntity());
        
        return responseBody;
    }
    
    @Test
    public void getAsXML() throws ClientProtocolException, IOException, JAXBException {
        HttpGet getRequest = buildGet("application/xml");
        String responseBody = exec(getRequest);
        
        JAXBContext context = JAXBContext.newInstance("com.github.teilzeitstudent.restserverstandalone.model");
        GreetingResponse responseObject = (GreetingResponse) context.createUnmarshaller().unmarshal(new StringSource(responseBody));
        Assert.assertEquals("Hello, bob", responseObject.getAnswer());
    }
    
    @Test
    public void getAsJSON() throws ClientProtocolException, IOException, JAXBException {
        HttpGet getRequest = buildGet("application/json");
        
        String responseBody = exec(getRequest);
        
        LOGGER.debug("Recieved response {}", responseBody);
        ObjectMapper mapper = new ObjectMapper();
        
        GreetingResponse responseObject = mapper.readValue(responseBody, GreetingResponse.class);
        Assert.assertEquals("Hello, bob", responseObject.getAnswer());
    }
    
    public HttpPost buildPost(String accept, String payload) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost("http://localhost:8080/greeting/hello");
        post.setEntity(new StringEntity(payload));
        post.setHeader("Content-Type", accept);
        post.setHeader("Accept", accept);
        return post;
    }
    
    @Test
    public void postAsXML() throws JAXBException, ClientProtocolException, IOException {
        GreetingRequest request = new GreetingRequest("Bob");
        JAXBContext context = JAXBContext.newInstance("com.github.teilzeitstudent.restserverstandalone.model");
        StringWriter sw = new StringWriter();
        context.createMarshaller().marshal(request, sw);
        HttpPost post = buildPost("application/xml", sw.toString());
        
        String responseBody = exec(post);
        
        GreetingResponse responseObject = (GreetingResponse) context.createUnmarshaller().unmarshal(new StringSource(responseBody));
        Assert.assertEquals("Hello, Bob", responseObject.getAnswer());
    }
    
    @Test
    public void postAsJSON() throws JAXBException, ClientProtocolException, IOException {
        GreetingRequest request = new GreetingRequest("Bob");
        ObjectMapper mapper = new ObjectMapper();
        
        String payload = mapper.writeValueAsString(request);
        
        HttpPost post = buildPost("application/json", payload);
        
        LOGGER.debug("Will send {}", payload);
        String responseBody = exec(post);
        LOGGER.debug("Recieved {}", payload);
        
        GreetingResponse responseObject = mapper.readValue(responseBody, GreetingResponse.class);
        Assert.assertEquals("Hello, Bob", responseObject.getAnswer());
    }

    @Override
    public boolean isCreateCamelContextPerClass() {
        return true;
    }
    
    
}
