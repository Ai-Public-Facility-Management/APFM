package server.service;


import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AzureService {
    @Value("${azure.account_key}")
    private String accountKey;
    @Value("${azure.account_name}")
    private String accountName;
    @Value("${azure.connection_string}")
    private String cnnectionString;
    @Value("${azure.container_name}")
    private String containerName;

    public String azureBlobSas(String url){
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(url)
                .credential(new com.azure.storage.common.StorageSharedKeyCredential(
                        accountName, accountKey))
                .buildClient();

        // SAS 권한 및 만료 설정
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusHours(1),
                permissions
        );

        // SAS 토큰 생성
        String sasToken = blobClient.generateSas(sasValues);

        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    public String azureBlobUpload(MultipartFile file,String type) throws IOException {
        String blobName = UUID.randomUUID() + type;
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(cnnectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        return blobClient.getBlobUrl();
    }

    public String azureSaveFile(String file_base64,Long id,String type) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(file_base64);
        String blobName = switch (type) {
            case "camera" -> "camera/image/camera_" + id.toString() + ".png";
            case "facility" -> "camera/facility/facility_" + id.toString() + ".png";
            case "report" -> "inspection/report_" + id.toString() + "_" + LocalDate.now() + ".pdf";
            default -> "fail";
        };
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(cnnectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        try(InputStream dataStream = new ByteArrayInputStream(imageBytes)){
            blobClient.upload(dataStream, imageBytes.length, true);
        }

        return blobClient.getBlobUrl();
    }

    public List<Long> getVideos (List<Long> ids){
        List<Long> videos = new ArrayList<>();
        String currentDir = System.getProperty("user.dir");
        Path targetDirPath = Paths.get(currentDir, "../Ai/videos").normalize();
        File targetDir = targetDirPath.toFile();
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(cnnectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        ids.forEach(id->{
            String blobName = "camera_"+id.toString()+".mp4";
            Path downloadFilePath = targetDirPath.resolve(blobName);
            BlobClient blobClient = containerClient.getBlobClient("camera/video/"+blobName);
            if (blobClient.exists()) {
                blobClient.downloadToFile(downloadFilePath.toString(), true);
                videos.add(id);
            }
        });

        return videos;
    }
}
