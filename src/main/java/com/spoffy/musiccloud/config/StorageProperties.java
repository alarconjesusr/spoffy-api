package com.spoffy.musiccloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(String endpoint, String accessKey, String secretKey, String bucketName, boolean secure) {
}