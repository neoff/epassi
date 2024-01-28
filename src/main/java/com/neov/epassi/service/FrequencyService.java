package com.neov.epassi.service;

import java.util.Map;
import java.util.Optional;
import com.neov.epassi.model.FrequencyResponse;

public interface FrequencyService {
  FrequencyResponse getTopWords(String filePath, Integer k, Boolean ignoreCase);
}
