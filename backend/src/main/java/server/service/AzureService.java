package server.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.azure.storage.blob.*;
import com.azure.storage.blob.sas.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.OffsetDateTime;
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
}
