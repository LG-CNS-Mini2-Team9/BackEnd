package com.lgcns.backend.user.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.ObjectCodec;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 파일 업로드 메소드
    public String upload(MultipartFile file) throws IOException {

        String fileName = getFileName(file);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        return amazonS3Client.getUrl(bucket, fileName).toString(); // 이미지 URL 반환
    }
    
    // 중북 안나게 파일이름 유니크하게 생성해줌
    private String getFileName(MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename();
    }

}
