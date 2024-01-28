package com.neov.epassi.controller;

import java.nio.file.Path;
import java.util.List;
import com.neov.epassi.model.FrequencyResponse;
import com.neov.epassi.model.WordFrequency;
import com.neov.epassi.service.FileService;
import com.neov.epassi.service.FileServiceImpl;
import com.neov.epassi.service.FrequencyService;
import com.neov.epassi.service.FrequencyServiceImpl;
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

@WebFluxTest(controllers = FrequencyController.class)
@Import({FrequencyController.class, FileServiceImpl.class, FrequencyServiceImpl.class})
class FrequencyControllerTest {
  
  @Mock
  private FrequencyService frequencyService;
  
  @Mock
  private FileService fileService;
  
  @InjectMocks
  private FrequencyController frequencyController;
  
  @Test
  void testGetTopFrequency() {
    String word = "test";
    String fileName = "/path/to/uploaded/file.txt";
    int frequency = 10;
    int limit = 1;
    FrequencyResponse mockResponse = FrequencyResponse.builder()
                                                      .fileName(fileName)
                                                      .words(List.of(WordFrequency.builder()
                                                                                  .word(word)
                                                                                  .frequency(frequency)
                                                                                  .build()))
                                                      .build();
    Mockito.when(frequencyService.getTopWords(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenReturn(mockResponse);
    
    MockServerHttpRequest request = MockServerHttpRequest.get("/api/frequencys")
                                                         .queryParam("word", word)
                                                         .queryParam("file", fileName)
                                                         .queryParam("ignoreCase", true)
                                                         .build();
    // When:
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<FrequencyResponse>> resultMono =
      frequencyController.getTopFrequency(fileName, limit, true, exchange);
    
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.OK &&
                                       responseEntity.getBody() != null &&
                                       responseEntity.getBody().equals(mockResponse))
                .expectComplete()
                .verify();
    var result = resultMono.mapNotNull(ResponseEntity::getBody).block();
    Assertions.assertNotNull(result);
    Assertions.assertEquals(fileName, result.getFileName());
    Assertions.assertEquals(1, result.getWords().size());
    Assertions.assertEquals(frequency, result.getWords().get(0).getFrequency());
    Assertions.assertEquals(word, result.getWords().get(0).getWord());
  }
  
  @Test
  void testGetTopFrequency_exception() {
    Mockito.when(frequencyService.getTopWords(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenThrow(new RuntimeException("Test exception"));
    MockServerHttpRequest request = MockServerHttpRequest.get("/api/count")
                                                         .queryParam("word", "word")
                                                         .queryParam("file", "fileName")
                                                         .queryParam("ignoreCase", true)
                                                         .build();
    
    // When:
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<FrequencyResponse>> resultMono =
      frequencyController.getTopFrequency("fileName", 1, true, exchange);
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR &&
                                       responseEntity.getBody() != null)
                .expectComplete()
                .verify();
  }
  
  @Test
  void testTopFrequency() {
    // Given:
    String word = "test";
    String fileName = "/path/to/uploaded/file.txt";
    Flux<Part> fileFlux = Flux.empty();
    int frequency = 10;
    int limit = 1;
    Boolean ignoreCase = true;
    
    // Mock FileUpload service behavior
    Path uploadedFilePath = Path.of(fileName);
    Mockito.when(fileService.uploadFile(fileFlux)).thenReturn(Mono.just(uploadedFilePath));
    
    // Mock WordFrequencyService behavior
    FrequencyResponse mockResponse = FrequencyResponse.builder()
                                                      .fileName(uploadedFilePath.toString())
                                                      .words(List.of(WordFrequency.builder()
                                                                                  .word(word)
                                                                                  .frequency(frequency)
                                                                                  .build()))
                                                      .build();
    Mockito.when(frequencyService.getTopWords(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenReturn(mockResponse);
    
    
    
    // Create a mock ServerWebExchange
    Flux<DataBuffer> dataBufferFlux = fileFlux.flatMap(Part::content);
    MockServerHttpRequest request = MockServerHttpRequest.post("/api/frequencys")
                                                         .header("Content-Type", "multipart/form-data")
                                                         .body(dataBufferFlux);
    
    // When: Call the controller method
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<FrequencyResponse>> resultMono =
      frequencyController.topFrequency(limit, fileFlux, ignoreCase, exchange);
    
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.OK &&
                                       responseEntity.getBody() != null &&
                                       responseEntity.getBody().equals(mockResponse))
                .expectComplete()
                .verify();
    var result = resultMono.mapNotNull(ResponseEntity::getBody).block();
    Assertions.assertNotNull(result);
    Assertions.assertEquals(fileName, result.getFileName());
    Assertions.assertEquals(1, result.getWords().size());
    Assertions.assertEquals(frequency, result.getWords().get(0).getFrequency());
    Assertions.assertEquals(word, result.getWords().get(0).getWord());
  }
  
  
  @Test
  void testFrequency_exception() {
    // Given:
    String fileName = "/path/to/uploaded/file.txt";
    Flux<Part> fileFlux = Flux.empty();
    
    Path uploadedFilePath = Path.of(fileName);
    Mockito.when(fileService.uploadFile(fileFlux)).thenReturn(Mono.just(uploadedFilePath));
    
    Mockito.when(frequencyService.getTopWords(Mockito.any(), Mockito.any(), Mockito.any()))
           .thenThrow(new RuntimeException("Test exception"));
    
    Flux<DataBuffer> dataBufferFlux = fileFlux.flatMap(Part::content);
    MockServerHttpRequest request = MockServerHttpRequest.post("/api/frequencys")
                                                         .header("Content-Type", "multipart/form-data")
                                                         .body(dataBufferFlux);
    
    // When:
    ServerWebExchange exchange = MockServerWebExchange.from(request);
    Mono<ResponseEntity<FrequencyResponse>> resultMono =
      frequencyController.topFrequency(1, fileFlux, false, exchange);
    
    // Then:
    StepVerifier.create(resultMono)
                .expectNextMatches(responseEntity ->
                                     responseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR &&
                                       responseEntity.getBody() != null )
                .expectComplete()
                .verify();
  }
}