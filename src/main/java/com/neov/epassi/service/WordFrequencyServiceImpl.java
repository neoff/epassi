package com.neov.epassi.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.neov.epassi.model.FrequencyResponse;
import com.neov.epassi.model.WordFrequency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordFrequencyServiceImpl implements WordFrequencyService {
  private final FileService fileService;
  @Override
  public FrequencyResponse getWordFrequency(String filePath, String kWord, Boolean ignoreCase) {
    var freq = wordFrequency(filePath, kWord, ignoreCase);
    return FrequencyResponse.builder()
                            .fileName(filePath)
                            .words(List.of(WordFrequency.builder().frequency(freq).word(kWord).build()))
                            .build();
  }
  
  private Integer wordFrequency(String filePath, String word, boolean ignoreCase) {
    Map<String, Integer> wordFrequencyMap = fileService.readWordsFromFile(filePath, ignoreCase);
    return wordFrequencyMap.get(ignoreCase?word.toUpperCase(Locale.ROOT):word);
  }
}
