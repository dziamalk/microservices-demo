package com.microservices.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:twitter.properties")
public class Twitter4jConfig {

//    @Value(value = "${oauth.consumerKey}")
    private String consumerKey = "";

//    @Value("${oauth.consumerSecret}")
    private String consumerSecret = "";

//    @Value("${oauth.accessToken}")
    private String accessToken = "";

//    @Value("${oauth.accessTokenSecret}")
    private String accessTokenSecret = "";

}
