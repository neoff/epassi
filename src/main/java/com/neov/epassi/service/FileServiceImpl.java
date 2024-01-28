package com.neov.epassi.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FileServiceImpl implements FileService {
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
    return DataBufferUtils.join(content)
                          .flatMap(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer); // Release resources associated with the DataBuffer
                            
                            // Save the content to the target path
                            return Mono.fromCallable(() -> {
                              Files.createDirectories(targetPath.getParent());
                              Files.write(targetPath, bytes);
                              return targetPath;
                            });
                          });
  }
  
  @Override
  @Cacheable(value = "wordFrequencyMap", key = "{#filePath, #ignoreCase}")
  public Map<String, Integer> readWordsFromFile(String filePath, Boolean ignoreCase) {
    List<String> words;
    try {
      words = readFromFile(filePath, ignoreCase);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return calculateWordFrequency(words);
  }
  
  private List<String> readFromFile(String filePath, boolean ignoreCase) throws IOException {
    Path path = Paths.get(filePath);
    if (!Files.exists(path)) {
      throw new IOException("File not found");
    }
    return Files.lines(path)
                .flatMap(line -> {
                  var clean = line
                                .replace(" -", "")
                                .replaceAll("[^\\sa-zA-Z0-9]", "")
                                .replaceAll("^\\s*$", "");
                  if(ignoreCase){
                    clean = clean.toUpperCase(Locale.ROOT);
                  }
                  return Arrays.stream(clean.split("\\s+"));
                })
                .filter(word -> !word.isBlank())
                .toList();
  }
  
  
  private Map<String, Integer> calculateWordFrequency(List<String> words) {
    Map<String, Integer> wordFrequencyMap = new HashMap<>();
    
    for (String word : words) {
      wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
    }
    
    return wordFrequencyMap;
  }
}
