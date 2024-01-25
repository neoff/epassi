package com.neov.epassi.service;

import java.nio.file.Path;
import org.springframework.http.codec.multipart.Part;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileUpload {
  Mono<Path> uploadFile(Flux<Part> fileMono);
}
