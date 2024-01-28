package com.neov.epassi.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class FrequencyServiceTest {
  
  @Mock
  private FileService fileService;
  @InjectMocks
  private FrequencyServiceImpl frequencyService;
  
  @Test
  void testGetTopWords() {
    // Given:
    int k = 2;
    Mockito.when(fileService.readWordsFromFile(Mockito.any(), Mockito.any()))
           .thenReturn(Map.of("a", 1, "s", 2, "d", 3, "f", 4, "g", 5));
    // When:
    var result = frequencyService.getTopWords("testfile.txt", k, true);
    // Then:
    Assertions.assertNotNull(result);
    Assertions.assertEquals(k, result.getWords().size());
    Assertions.assertEquals(5, result.getWords().get(0).getFrequency());
    Assertions.assertEquals("g", result.getWords().get(0).getWord());
    Assertions.assertEquals(4, result.getWords().get(1).getFrequency());
    Assertions.assertEquals("f", result.getWords().get(1).getWord());
  }
}