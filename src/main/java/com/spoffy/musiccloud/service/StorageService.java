package com.spoffy.musiccloud.service;

import java.io.InputStream;

public interface StorageService {
    void upload(String objectName, InputStream inputStream, long size, String contentType);

    InputStream download(String objectName);

    String getPresignedGetUrl(String objectName, int expirySeconds);

    InputStream downloadRange(String objectName, long offset, long length);

    void delete(String objectName);

    void ensureBucketExists();
}