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
import java.util.Optional;
import java.util.stream.Collectors;
import com.neov.epassi.model.FrequencyResponse;
import com.neov.epassi.model.WordFrequency;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FrequencyServiceImpl implements FrequencyService {
  private final FileService fileService;
  @Override
  public FrequencyResponse getTopWords(String filePath, Integer k, Boolean ignoreCase) {
    Map<String, Integer> wordFrequencyMap = fileService.readWordsFromFile(filePath, ignoreCase);
    List<Map.Entry<String, Integer>> sortedEntries = sortMapByValue(wordFrequencyMap);
    var wordFreq =  sortedEntries
               .stream()
               .limit(k)
               .map(entry -> WordFrequency.builder().word(entry.getKey()).frequency(entry.getValue()).build())
               .toList();
    return FrequencyResponse.builder()
                     .fileName(filePath)
                     .words(wordFreq)
                     .build();
  }
  
  
  
  private List<Map.Entry<String, Integer>> sortMapByValue(Map<String, Integer> map) {
    return map.entrySet().stream()
              .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
              .toList();
  }
}
