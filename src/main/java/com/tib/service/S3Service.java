package com.tib.service;

import com.tib.dto.ShortsUploadRequest;
import com.tib.dto.ShortsUploadResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Presigner s3Presigner;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public ShortsUploadResponse getPreSignedUrl(ShortsUploadRequest request) {
    String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
    String uuid = UUID.randomUUID().toString().substring(0, 8); // Short UUID for cleaner URLs

    String videoKey = String.format("shorts/%s/%s.mp4", datePath, uuid);
    String thumbnailKey = String.format("thumbs/%s/%s.jpg", datePath, uuid);

    PresignedPutObjectRequest videoPresignedRequest = createPresignedUrl(videoKey, request.getVideoContentType(),
        request.getVideoFileSize());
    PresignedPutObjectRequest thumbnailPresignedRequest = createPresignedUrl(thumbnailKey,
        request.getThumbnailContentType(), request.getThumbnailFileSize());

    return ShortsUploadResponse.builder()
        .videoUploadUrl(videoPresignedRequest.url().toString())
        .videoKey(videoKey)
        .thumbnailUploadUrl(thumbnailPresignedRequest.url().toString())
        .thumbnailKey(thumbnailKey)
        .expiresIn(3600)
        .build();
  }

  private PresignedPutObjectRequest createPresignedUrl(String key, String contentType, long fileSize) {
    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(contentType)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(60))
        .putObjectRequest(objectRequest)
        .build();

    return s3Presigner.presignPutObject(presignRequest);
  }

  public String getBucket() {
    return bucket;
  }
}
