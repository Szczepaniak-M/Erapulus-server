package com.erapulus.server.service;

import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.ParallelTransferOptions;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
@AllArgsConstructor
public class AzureStorageService {

    private final BlobContainerAsyncClient blobContainerAsyncClient;

    public Mono<String> uploadFile(FilePart resource, String path) {
        BlobAsyncClient blobAsyncClient = blobContainerAsyncClient.getBlobAsyncClient(path);
        ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions();
        Flux<ByteBuffer> data = DataBufferUtils.join(resource.content())
                                               .map(this::toByteBuffer)
                                               .flatMapMany(Flux::just);
        return blobAsyncClient.upload(data, parallelTransferOptions, true)
                              .thenReturn(path);
    }

    private ByteBuffer toByteBuffer(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return ByteBuffer.wrap(bytes);
    }
}
