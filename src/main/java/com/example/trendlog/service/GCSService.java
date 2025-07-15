package com.example.trendlog.service;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.FileErrorCode;
import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class GCSService {
    private final Storage storage;
    @Value("${gcp.bucket}")
    private String bucketName;
    public GCSService(Storage storage) {
        this.storage = storage;
    }
    public String convertToRelativePath(String fullUrl) {
        return fullUrl.replace("https://storage.googleapis.com/" + bucketName + "/", "");
    }
    public void copyFile(String src, String dest) {
        try {
            BlobId source = BlobId.of(bucketName, src);
            BlobId target = BlobId.of(bucketName, dest);
            storage.copy(Storage.CopyRequest.of(source, target));
        } catch (StorageException e) {
            throw new AppException(FileErrorCode.COPY_FAIL); //FILE-003
        }
    }

    public String getBucketName() {
        return bucketName;
    }


    public String uploadFile(String fileName, String contentType, byte[] fileBytes){
        try {
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(contentType)
                    .build();
            storage.create(blobInfo, fileBytes);
            storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        } catch (StorageException e) {
            throw new AppException(FileErrorCode.UPLOAD_FAIL); // FILE-001
        }
    }
    public byte[] downloadFile(String fileName) {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, fileName));
            return blob.getContent();
        } catch (StorageException e) {
            throw new AppException(FileErrorCode.DOWNLOAD_FAIL); // FILE-004
        }
    }

    public void deleteFile(String fileName) {
        try {
            storage.delete(BlobId.of(bucketName, fileName));
        } catch (StorageException e) {
            throw new AppException(FileErrorCode.DELETE_FAIL); // FILE-002
        }
    }
    public String uploadToTemp(MultipartFile file, UUID userId) {
        try {
            String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
            String fileName = "temp/profile/" + userId + "/" + UUID.randomUUID();
            return uploadFile(fileName, contentType, file.getBytes());
        } catch (Exception e) {
            throw new AppException(FileErrorCode.UPLOAD_FAIL); //FILE-001
        }
    }
}
