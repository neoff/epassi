package com.neov.epassi.controller;


import java.nio.file.Path;
import com.neov.epassi.model.WordFrequencyResponse;
import com.neov.epassi.service.FileUpload;
import com.neov.epassi.service.FileUploadImpl;
import com.neov.epassi.service.WordFrequencyService;
import com.neov.epassi.service.WordFrequencyServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = WordFrequencyController.class)
@Import({WordFrequencyController.class, FileUploadImpl.class, WordFrequencyServiceImpl.class})
class WordFrequencyControllerTest {
  @Mock
  private WordFrequencyService wordFrequencyService;
  
  @Mock
  private FileUpload fileUpload;
  
  @InjectMocks
  private WordFrequencyController wordFrequencyController;
  
  @Test
  void testFrequency_exception() {
    // Given:
    String fileName = "/path/to/uploaded/file.txt";
    Flux<Part> fileFlux = Flux.empty();
    
    Path uploadedFilePath = Path.of(fileName);
    Mockito.when(fileUpload.uploadFile(fileFlux)).thenReturn(Mono.just(uploadedFilePath));
    
    Mockito.when(wordFrequencyService.getWordFrequencyInUpload(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenThrow(new RuntimeException("Test exception"));
    
    Flux<DataBuffer> dataBufferFlux = fileFlux.flatMap(Part::content);
    MockServerHttpRequest request = MockServerHttpRequest.post("/api/frequencys")
                                                         .header("Content-Type", "multipart/form-data")
                                                         .body(dataBufferFlux);
    
    // When:
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<WordFrequencyResponse>> resultMono =
      wordFrequencyController.frequency("word", fileFlux, false, exchange);
    
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR &&
                                       responseEntity.getBody() != null )
                .expectComplete()
                .verify();
  }
  
  @Test
  void testFrequency() {
    // Given:
    String word = "test";
    String fileName = "/path/to/uploaded/file.txt";
    Flux<Part> fileFlux = Flux.empty();
    int frequency = 10;
    Boolean ignoreCase = true;
    
    // Mock FileUpload service behavior
    Path uploadedFilePath = Path.of(fileName);
    Mockito.when(fileUpload.uploadFile(fileFlux)).thenReturn(Mono.just(uploadedFilePath));
    
    // Mock WordFrequencyService behavior
    WordFrequencyResponse mockResponse = WordFrequencyResponse.builder()
                                                              .fileName(uploadedFilePath.toString())
                                                              .kWord(word)
                                                              .frequency(frequency)
                                                              .build();
    Mockito.when(wordFrequencyService.getWordFrequencyInUpload(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenReturn(mockResponse);
    
    
    
    // Create a mock ServerWebExchange
    Flux<DataBuffer> dataBufferFlux = fileFlux.flatMap(Part::content);
    MockServerHttpRequest request = MockServerHttpRequest.post("/api/frequencys")
                                                     .header("Content-Type", "multipart/form-data")
                                                     .body(dataBufferFlux);
    
    // When: Call the controller method
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<WordFrequencyResponse>> resultMono =
      wordFrequencyController.frequency(word, fileFlux, ignoreCase, exchange);
    
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.OK &&
                                       responseEntity.getBody() != null &&
                                       responseEntity.getBody().equals(mockResponse))
                .expectComplete()
                .verify();
    var result = resultMono.mapNotNull(ResponseEntity::getBody).block();
    Assertions.assertEquals(frequency, result.getFrequency());
    Assertions.assertEquals(fileName, result.getFileName());
    Assertions.assertEquals(word, result.getkWord());
  }
  
  @Test
  void testGetFrequency_exception() {
    Mockito.when(wordFrequencyService.getWordFrequency(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenThrow(new RuntimeException("Test exception"));
    MockServerHttpRequest request = MockServerHttpRequest.get("/api/frequencys")
                                                         .queryParam("word", "word")
                                                         .queryParam("file", "fileName")
                                                         .queryParam("ignoreCase", true)
                                                         .build();
    
    // When:
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<WordFrequencyResponse>> resultMono =
      wordFrequencyController.getFrequency("fileName", "word", true, exchange);
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR &&
                                       responseEntity.getBody() != null)
                .expectComplete()
                .verify();
  }
  @Test
  void testGetFrequency() {
    String word = "test";
    String fileName = "/path/to/uploaded/file.txt";
    int frequency = 10;
    WordFrequencyResponse mockResponse = WordFrequencyResponse.builder()
                                                              .fileName(fileName)
                                                              .kWord(word)
                                                              .frequency(frequency)
                                                              .build();
    Mockito.when(wordFrequencyService.getWordFrequency(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenReturn(mockResponse);
    
    MockServerHttpRequest request = MockServerHttpRequest.get("/api/frequencys")
                                                         .queryParam("word", word)
                                                         .queryParam("file", fileName)
                                                         .queryParam("ignoreCase", true)
                                                         .build();
    // When:
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<WordFrequencyResponse>> resultMono =
      wordFrequencyController.getFrequency(fileName, word, true, exchange);
    
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.OK &&
                                       responseEntity.getBody() != null &&
                                       responseEntity.getBody().equals(mockResponse))
                .expectComplete()
                .verify();
    var result = resultMono.mapNotNull(ResponseEntity::getBody).block();
    Assertions.assertEquals(frequency, result.getFrequency());
    Assertions.assertEquals(fileName, result.getFileName());
    Assertions.assertEquals(word, result.getkWord());
  }
}