package com.neov.epassi.service;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FileUploadTest {
  @Mock
  private FilePart filePart;
  
  @InjectMocks
  private FileUploadImpl fileUploadService;
  
  @Test
  void uploadFiles() {
    // Given:
    var filePath = "testfile.txt";
    
    Mockito.when(filePart.filename()).thenReturn(filePath);
    byte[] contentBytes = "File content".getBytes();
    DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(contentBytes);
    Mockito.when(filePart.content()).thenReturn(Flux.just(dataBuffer));
    
    // When:
    Mono<Path> resultMono = fileUploadService.uploadFile(Flux.just(filePart));
    
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(path -> path.toString().endsWith(filePath)) // Adjust the predicate as needed
                .expectComplete()
                .verify();
    Mockito.verify(filePart, Mockito.times(1)).filename();
    Mockito.verify(filePart, Mockito.times(1)).content();
  }
  
}