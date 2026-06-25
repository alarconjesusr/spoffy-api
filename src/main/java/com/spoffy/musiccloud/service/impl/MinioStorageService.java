package com.spoffy.musiccloud.service.impl;

import com.spoffy.musiccloud.config.StorageProperties;
import com.spoffy.musiccloud.service.StorageService;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
    private final StorageProperties storageProperties;

    @Override
    public void upload(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(storageProperties.bucketName())
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to upload file to storage", ex);
        }
    }

    @Override
    public InputStream download(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(storageProperties.bucketName())
                    .object(objectName)
                    .build());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to download file from storage", ex);
        }
    }

    @Override
    public InputStream downloadRange(String objectName, long offset, long length) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(storageProperties.bucketName())
                    .object(objectName)
                    .offset(offset)
                    .length(length)
                    .build());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to stream file from storage", ex);
        }
    }

    @Override
    public void delete(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(storageProperties.bucketName())
                    .object(objectName)
                    .build());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to delete file from storage", ex);
        }
    }

    @Override
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(storageProperties.bucketName()).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(storageProperties.bucketName()).build());
            }
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize MinIO bucket", ex);
        }
    }
}