package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.twitter.to.kafka.service.config.Twitter4jConfig;
import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfig;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

import javax.annotation.PreDestroy;
import java.util.Arrays;

@Slf4j
@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterKafkaStreamRunner implements StreamRunner {

    private final Twitter4jConfig twitter4jConfig;
    private final TwitterToKafkaServiceConfig twitterToKafkaServiceConfig;
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;

    private TwitterStream twitterStream;

    public TwitterKafkaStreamRunner(Twitter4jConfig twitter4jConfig, TwitterToKafkaServiceConfig twitterToKafkaServiceConfig,
                                    TwitterKafkaStatusListener twitterKafkaStatusListener) {
        this.twitter4jConfig = twitter4jConfig;
        this.twitterToKafkaServiceConfig = twitterToKafkaServiceConfig;
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException {
        twitterStream = new TwitterStreamFactory().getInstance();
        setupAuth();
        twitterStream.addListener(twitterKafkaStatusListener);
        addFiler();
    }

    private void setupAuth() {
        twitterStream.setOAuthConsumer(twitter4jConfig.getConsumerKey(), twitter4jConfig.getConsumerSecret());
        AccessToken at = new AccessToken(twitter4jConfig.getAccessToken(), twitter4jConfig.getAccessTokenSecret());
        twitterStream.setOAuthAccessToken(at);
    }

    private void addFiler() {
        String[] keywords = twitterToKafkaServiceConfig.getTwitterKeywords().toArray(new String[0]);
        FilterQuery filterQuery = new FilterQuery(keywords);
        twitterStream.filter(filterQuery);
        log.info("Started filtering twitter stream for keywords {}", Arrays.toString(keywords));
    }

    @PreDestroy
    public void shutdown() {
        if (twitterStream != null) {
            log.info("Closing twitter stream");
            twitterStream.shutdown();
        }
    }
}
