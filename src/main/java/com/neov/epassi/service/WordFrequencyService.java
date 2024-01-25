package com.neov.epassi.service;

import com.neov.epassi.model.WordFrequencyResponse;

public interface WordFrequencyService {
  WordFrequencyResponse getWordFrequency(String filePath, String kWord, Boolean ignoreCase);
  WordFrequencyResponse getWordFrequencyInUpload(String filePath, String kWord, Boolean ignoreCase);
}
