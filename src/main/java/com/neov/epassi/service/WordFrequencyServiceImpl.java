package com.neov.epassi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import com.neov.epassi.model.WordFrequencyResponse;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class WordFrequencyServiceImpl implements WordFrequencyService {
  
  @Cacheable(value = "wordFrequencyCache", key = "{#filePath, #kWord, #ignoreCase}")
  @Override
  public WordFrequencyResponse getWordFrequency(String filePath, String kWord, Boolean ignoreCase) {
    var freq = wordFrequency(filePath, kWord, ignoreCase);
    return WordFrequencyResponse.builder()
                                .kWord(kWord)
                                .fileName(filePath)
                                .frequency(freq)
                                .build();
  }
  
  @CachePut(value = "wordFrequencyCache", key = "{#filePath, #kWord, #ignoreCase}")
  public WordFrequencyResponse getWordFrequencyInUpload(String filePath, String kWord, Boolean ignoreCase) {
    return getWordFrequency(filePath, kWord, ignoreCase);
  }
  
  private Integer wordFrequency(String filePath, String word, boolean ignoreCase) {
    List<String> words = null;
    try {
      words = readWordsFromFile(filePath, ignoreCase);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Map<String, Integer> wordFrequencyMap = calculateWordFrequency(words);
    if(ignoreCase) {
      word = word.toUpperCase(Locale.ROOT);
    }
    return wordFrequencyMap.get(word);
  }
  
  private List<String> readWordsFromFile(String filePath, boolean ignoreCase) throws IOException {
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
                .collect(Collectors.toList());
  }
  
  
  private Map<String, Integer> calculateWordFrequency(List<String> words) {
    Map<String, Integer> wordFrequencyMap = new HashMap<>();
    
    for (String word : words) {
      wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
    }
    
    return wordFrequencyMap;
  }
}
