package com.neov.epassi.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
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
    int k = 3;
    Mockito.when(fileService.readWordsFromFile(Mockito.any(), Mockito.eq(true)))
           .thenReturn(Map.of("A", 1, "S", 2, "D", 3, "F", 4, "G", 5));
    // When:
    var result = frequencyService.getTopWords("testfile.txt", k, true);
    // Then:
    Assertions.assertNotNull(result);
    Assertions.assertEquals(k, result.getWords().size());
    Assertions.assertEquals(5, result.getWords().get(0).getFrequency());
    Assertions.assertEquals("G", result.getWords().get(0).getWord());
    Assertions.assertEquals(4, result.getWords().get(1).getFrequency());
    Assertions.assertEquals("F", result.getWords().get(1).getWord());
    Assertions.assertEquals(3, result.getWords().get(2).getFrequency());
    Assertions.assertEquals("D", result.getWords().get(2).getWord());
  }
  
  @Test
  void testGetTopWordsCase() {
    // Given:
    int k = 2;
    Mockito.when(fileService.readWordsFromFile(Mockito.any(), Mockito.eq(false)))
           .thenReturn(Map.of("a", 1, "s", 2, "d", 3));
    // When:
    var result = frequencyService.getTopWords("testfile.txt", k, false);
    // Then:
    Assertions.assertNotNull(result);
    Assertions.assertEquals(k, result.getWords().size());
    Assertions.assertEquals(3, result.getWords().get(0).getFrequency());
    Assertions.assertEquals("d", result.getWords().get(0).getWord());
    Assertions.assertEquals(2, result.getWords().get(1).getFrequency());
    Assertions.assertEquals("s", result.getWords().get(1).getWord());
    
  }
}