package com.erapulus.server.service;

import com.azure.storage.blob.BlobAsyncClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.models.BlockBlobItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AzureStorageServiceTest {

    @Mock
    private BlobContainerAsyncClient blobContainerAsyncClient;

    @Mock
    private BlobAsyncClient blobAsyncClient;

    @Mock
    private FilePart filePart;

    private AzureStorageService azureStorageService;

    @BeforeEach
    void setUp() {
        azureStorageService = new AzureStorageService(blobContainerAsyncClient);
    }

    @Test
    void uploadFile() {
        // given
        String path = "app/path";
        String resultPath = "https://erapulus.blob.core.windows.net/erapulus/app/path";
        when(blobContainerAsyncClient.getBlobAsyncClient(path)).thenReturn(blobAsyncClient);
        when(filePart.content()).thenReturn(Flux.just(new DefaultDataBufferFactory().wrap(new byte[]{1,2,3,4})));
        when(blobAsyncClient.upload(any(), any(), eq(true))).thenReturn(Mono.just(new BlockBlobItem(null, null, null, false, null)));

        // when
        Mono<String> result = azureStorageService.uploadFile(filePart, path);

        // then
        StepVerifier.create(result)
                .expectSubscription()
                .assertNext(pathFromMono -> assertEquals(path, pathFromMono))
                .verifyComplete();
        verify(blobContainerAsyncClient).getBlobAsyncClient(path);
        verify(filePart).content();
        verify(blobAsyncClient).upload(any(), any(), eq(true));
    }
}
