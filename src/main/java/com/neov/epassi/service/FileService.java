package com.neov.epassi.service;

import java.nio.file.Path;
import java.util.Map;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileService {
  Mono<Path> uploadFile(Flux<Part> fileMono);
  Map<String, Integer> readWordsFromFile(String filePath, Boolean ignoreCase);
}
