package com.erapulus.server.configuration;

import com.azure.spring.autoconfigure.storage.StorageProperties;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.String.format;

@Configuration
public class AzureStorageConfiguration {

    private static final String CONNECTION_STRING = """
            DefaultEndpointsProtocol=https;
            EndpointSuffix=core.windows.net;
            AccountName=%s;
            AccountKey=%s
            """;

    @Bean
    public BlobServiceAsyncClient blobServiceClient(StorageProperties storageProperties) {
        return new BlobServiceClientBuilder()
                .connectionString(format(CONNECTION_STRING, storageProperties.getAccountName(), storageProperties.getAccountKey()))
                .buildAsyncClient();
    }

    @Bean
    public BlobContainerAsyncClient blobContainerAsyncClient(BlobServiceAsyncClient blobServiceClient,
                                                             @Value("${azure.storage.container-name}") String containerName) {
        return blobServiceClient.getBlobContainerAsyncClient(containerName);
    }
}
