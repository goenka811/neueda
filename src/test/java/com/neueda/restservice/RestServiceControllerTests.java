package com.neueda.restservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestServiceControllerTests {

    @Test
    void contextLoads() {
    }
    @Test
    void testGetLongUrlFromShort(){
        RestService service = new RestService();
        //Negative test to check no URL returned
        assertNull(service.getLongUrlFromShort("www.testingurl.com"));
    }
    @Test
    void testShrinkURL(){
        RestServiceController restServiceController = new RestServiceController(new RestService());
        assertTrue(restServiceController.shrinkUrl("www.testingurl.com").hasBody());
    }
    @Test
    void testGenerateShortUrl(){
        RestService service = new RestService();
        assertNotNull(service.generateShortUrl("www.testingurl.com"));
        assertTrue(service.generateShortUrl("www.testingurl.com").length() >= 15);
    }
    @Test
    void redirectURL(){
        RestServiceController restServiceController = new RestServiceController(new RestService());
        assertNotNull(restServiceController.redirectUrl("http://san.ri/K6Z6bKN"));
        assertFalse(restServiceController.redirectUrl("http://san.ri/K6Z6bKNm").hasBody());
    }
}
