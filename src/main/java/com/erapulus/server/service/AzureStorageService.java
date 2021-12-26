package com.erapulus.server.service;

import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.ParallelTransferOptions;
import com.erapulus.server.configuration.AzureStorageProperties;
import com.erapulus.server.database.model.DocumentEntity;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class AzureStorageService {

    private static final String URL_COMMON_PART = "https://%s.blob.core.windows.net/%s/";
    private final BlobContainerAsyncClient blobContainerAsyncClient;
    private final String storageContainerUrl;

    public AzureStorageService(BlobContainerAsyncClient blobContainerAsyncClient,
                               AzureStorageProperties storageProperties) {
        this.blobContainerAsyncClient = blobContainerAsyncClient;
        storageContainerUrl = URL_COMMON_PART.formatted(storageProperties.getAccountName(), storageProperties.getContainerName());
    }

    public Mono<String> uploadFile(FilePart resource, String path) {
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(path);
        ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions();
        Flux<ByteBuffer> data = DataBufferUtils.join(resource.content())
                                               .map(this::toByteBuffer)
                                               .flatMapMany(Flux::just);
        return blobAsyncClient.upload(data, parallelTransferOptions, true)
                              .thenReturn(storageContainerUrl + path);
    }

    public Mono<Boolean> deleteFile(DocumentEntity document) {
        String path = document.path().replace(storageContainerUrl, "");
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(path);
        return blobAsyncClient.delete()
                              .thenReturn(true);
    }

    private ByteBuffer toByteBuffer(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return ByteBuffer.wrap(bytes);
    }
}
