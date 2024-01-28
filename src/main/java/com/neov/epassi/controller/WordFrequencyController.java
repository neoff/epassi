package com.neov.epassi.controller;

import com.neov.epassi.model.FrequencyResponse;
import com.neov.epassi.service.FileService;
import com.neov.epassi.service.WordFrequencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WordFrequencyController implements WordFrequencyApi {
  
  private final WordFrequencyService wordFrequencyService;
  private final FileService fileService;
  
  @Override
  public Mono<ResponseEntity<FrequencyResponse>> frequency(String word, Flux<Part> file, Boolean ignoreCase,
                                                           ServerWebExchange exchange) {
    return fileService.uploadFile(file)
                      .map(path -> ResponseEntity
                                    .ok(wordFrequencyService.getWordFrequency(path.toString(), word, ignoreCase))
                     )
                      .onErrorResume(e -> {
                       log.error("Error while uploading file", e);
                       return Mono.just(ResponseEntity
                                     .of(ProblemDetail
                                           .forStatusAndDetail(
                                             HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()))
                                     .build());
                       }
                     );
  }
  
  @Override
  public Mono<ResponseEntity<FrequencyResponse>> getFrequency(String file,
                                                                        String word,
                                                                        Boolean ignoreCase,
                                                                        ServerWebExchange exchange) {
    return Mono.fromCallable(() -> ResponseEntity.ok(wordFrequencyService.getWordFrequency(file, word, ignoreCase)))
               .onErrorResume(e -> {
                 log.error("File not readable or not exist", e);
                 return Mono.just(ResponseEntity
                                    .of(ProblemDetail
                                          .forStatusAndDetail(
                                            HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()))
                                    .build());
               });
  }
}