package com.neueda.restservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;

@RestController
public class RestServiceController {

    private final Logger log = LoggerFactory.getLogger(RestServiceController.class);
    private final RestService restService;
    private BufferedReader br;
    private ArrayList<String> arr = new ArrayList<String>();

    @Autowired
    public RestServiceController(RestService restService) {
        this.restService = restService;
    }

    @PostConstruct
    public void init(){

        //Do initial cache loading
        try {
            String path = "";
            if(System.getProperty("os.name").startsWith("Windows")){
                path = "C:\\temp\\out.txt";
            }else{
                path = "/tmp/out.txt";
            }
            File fobj = new File(path);
            if(!fobj.exists()){
                FileOutputStream fos = new FileOutputStream(fobj);
                fos.flush();
                fos.close();
            }
            br = new BufferedReader(new FileReader(fobj));
            String str;
            while((str = br.readLine()) != null){
                arr.add(str);
                log.info("added to disc cache"+str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping(path = "/shrink", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> shrinkUrl(String url){
            log.info("checking url::" + url);
            log.info("disc cache"+arr);
            //get from persistent storage
            if(arr.toString().contains(":"+url)){
                for (String str: arr) {
                    if(str.contains(":"+url))
                    return ResponseEntity.ok("Url found(from disk cache)::"+str.substring(0, str.lastIndexOf(":")));
                }
              }
            String result = restService.generateShortUrl(url);
            return ResponseEntity.ok(result);
     }

    @GetMapping(value = "/redirect")
    public ResponseEntity<Object> redirectUrl(String shortUrl){
        log.info("checking url::" + shortUrl);
        log.info("disc cache"+arr);
        String longURL = "";
        if(arr.toString().contains(shortUrl+":")){
            for (String str: arr) {
                if (str.contains(shortUrl + ":"))
                    longURL = str.substring(str.lastIndexOf(":")+1);
            }
            log.info("Got from persistent cache::" + longURL);
        }else {
        longURL =restService.getLongUrlFromShort(shortUrl);
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("redirect:"+longURL)).build();
        //return new ModelAndView("redirect:"+longURL);
    }
 }
