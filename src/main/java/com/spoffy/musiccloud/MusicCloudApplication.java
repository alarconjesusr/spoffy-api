package com.spoffy.musiccloud;

import com.spoffy.musiccloud.config.JwtProperties;
import com.spoffy.musiccloud.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, StorageProperties.class})
public class MusicCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicCloudApplication.class, args);
    }
}