package com.neueda.restservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class RestServiceControllerTests {

    @Test
    void contextLoads() {
    }
    @Test
    void testGetLongUrlFromShort(){
        RestService service = new RestService();
        assertNull(service.getLongUrlFromShort("www.testingurl.com"));
    }
    @Test
    void testShrinkURL(){
        RestServiceController restServiceController = new RestServiceController(new RestService());
        assertNotNull(restServiceController.shrinkUrl("www.testingurl.com"));
    }
    @Test
    void testGenerateShortUrl(){
        RestService service = new RestService();
        assertNotNull(service.generateShortUrl("www.testingurl.com"));
    }
    @Test
    void redirectURL(){
        RestServiceController restServiceController = new RestServiceController(new RestService());
        assertNotNull(restServiceController.redirectUrl("www.testingurl.com"));
    }
}
