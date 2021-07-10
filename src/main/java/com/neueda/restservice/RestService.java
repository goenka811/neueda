package com.neueda.restservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;

@Service
public class RestService {

    private final Logger log = LoggerFactory.getLogger(RestService.class);
    private HashMap<String, String> map = new HashMap<String, String>();

    @Cacheable(value = "userValueCache", key = "#url.hashCode()", condition="#url != null")
    public String generateShortUrl(String url) {
        if (url != null) {
            //check for spaces
            if (url.trim().contains(" ")){
                return "Url contains spaces !!";
            }
            //compute short url string
            int leftLimit = 48;
            int rightLimit = 122;
            int targetStringLength = 7;
            Random random = new Random();
            String generatedString = random.ints(leftLimit, rightLimit+1).filter(i -> (i<=57 || i>=65)
                    && (i<=90 || i>=97)).limit(targetStringLength).collect(StringBuilder::new,
                    StringBuilder::appendCodePoint, StringBuilder::append).toString();

            generatedString = "http://san.ri/"+generatedString;
            log.info("generated {} for long URL {}", generatedString, url);
            final String shortUrl = generatedString;
            map.put(shortUrl, url);
            //non-palindrome values written to file as they maybe more in number
            ExecutorService executor = Executors.newSingleThreadExecutor();
           //write to file/cache as new task
                Future<Void> future = executor.submit((Callable<Void>) () -> writeToFile(shortUrl+":"+url));
                log.info("url {} is written to storage with shorturl {}", url, shortUrl);
            return "The shorturl is::" + shortUrl;
        } else {
            return "Bad URL - parameter is not supplied correctly !!";
          }
    }
    @Cacheable(value = "userValueCache", key = "#url.hashCode()", condition="#url != null")
    public String getLongUrlFromShort(String url) {
    if(url != null){
        log.info("current map contents::"+map+"::url"+url+"::value"+map.get(url));
          return map.get(url);
    } else {
        return "Bad URL - parameter is not supplied correctly !!";
    }
    }

    public Void writeToFile(String str){
        try {

            String path = "";
            if(System.getProperty("os.name").startsWith("Windows")){
                path = "C:\\temp\\out.txt";
            }else{
                path = "/tmp/out.txt";
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(new File(path), true));
            out.write(str);
            out.newLine();
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
