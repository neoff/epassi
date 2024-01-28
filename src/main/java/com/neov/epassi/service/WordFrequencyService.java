package com.neov.epassi.service;

import com.neov.epassi.model.FrequencyResponse;

public interface WordFrequencyService {
  FrequencyResponse getWordFrequency(String filePath, String kWord, Boolean ignoreCase);
}
