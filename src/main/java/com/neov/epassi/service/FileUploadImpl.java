package com.neov.epassi.service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FileUploadImpl implements FileUpload {
  @Override
  public Mono<Path> uploadFile(Flux<Part> upload) {
    return upload
             .filter(file -> file instanceof FilePart)
             .cast(FilePart.class)
             .flatMap(filePart -> {
               Path targetPath = Path.of("./target", filePart.filename());
               return saveFile(filePart.content(), targetPath);
             }).next();
  }
  
  private Mono<Path> saveFile(Flux<DataBuffer> content, Path targetPath) {
    return Mono.fromCallable(() -> {
      Files.createDirectories(targetPath.getParent());
      OutputStream outputStream = new FileOutputStream(targetPath.toFile());
      DataBufferUtils.write(content, outputStream);
      return targetPath;
    });
  }
}
