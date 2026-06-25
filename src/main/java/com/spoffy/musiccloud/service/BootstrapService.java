package com.spoffy.musiccloud.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BootstrapService {

    private final StorageService storageService;

    @Bean
    ApplicationRunner storageBootstrapRunner() {
        return args -> storageService.ensureBucketExists();
    }
}