package com.example.TelegramBot.AmazonServices;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonServicesConfig {



    @Bean
    public AmazonS3 getAmazonS3client(){
        AWSCredentials credentials = new BasicAWSCredentials(
                "AKIA6QLW2QADBYQ2I4N6",
          "Syzo8qbqFt6TMZBkBxC8iwPFVowST/0jAzWNShLJ"
        );
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_WEST_2)
                .build();
        return s3client;
    }
}
