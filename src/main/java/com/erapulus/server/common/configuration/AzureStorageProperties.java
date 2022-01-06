package com.erapulus.server.common.configuration;

import com.azure.spring.autoconfigure.storage.StorageProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;

@Primary
@ConfigurationProperties("azure.storage")
public class AzureStorageProperties extends StorageProperties {
    private String containerName;

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
}
