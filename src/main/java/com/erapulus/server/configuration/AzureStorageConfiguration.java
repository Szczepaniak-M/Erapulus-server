package com.erapulus.server.configuration;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobServiceAsyncClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
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
    public BlobServiceAsyncClient blobServiceClient(AzureStorageProperties storageProperties) {
        return new BlobServiceClientBuilder()
                .connectionString(format(CONNECTION_STRING, storageProperties.getAccountName(), storageProperties.getAccountKey()))
                .buildAsyncClient();
    }

    @Bean
    public BlobContainerAsyncClient blobContainerAsyncClient(BlobServiceAsyncClient blobServiceClient,
                                                             AzureStorageProperties storageProperties) {
        return blobServiceClient.getBlobContainerAsyncClient(storageProperties.getContainerName());
    }
}
