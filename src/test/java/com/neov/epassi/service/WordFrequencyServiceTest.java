package com.neov.epassi.service;

import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@SpringBootTest
class WordFrequencyServiceTest {
  @Autowired
  private WordFrequencyService wordFrequencyService;
  
  @Test
  void testGetWordFrequency() throws IOException {
    // Given:
    var filePath = "book.txt";
    var word = "Romeo";
    
    Resource resource = new ClassPathResource(filePath);
    Path path = resource.getFile().toPath();
    
    // When: ignore Case
    var result = wordFrequencyService.getWordFrequency(path.toString(), word, true);
    // Then:
    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getWords().size());
    Assertions.assertEquals(200, result.getWords().get(0).getFrequency());
    Assertions.assertEquals(word, result.getWords().get(0).getWord());
    var bits = result.getFileName().split("/");
    Assertions.assertEquals(filePath, bits[bits.length -1]);
    
    // When: case sensitive w/o cache
    result = wordFrequencyService.getWordFrequency(path.toString(), word, false);
    // Then:
    Assertions.assertNotNull(result);
    Assertions.assertEquals(71, result.getWords().get(0).getFrequency());
  }
  
  @Test
  void testGetWordFrequency_file_not_exist() throws IOException {
    // Given:
    var word = "Romeo";
    String nonExistentFilePath = "/path/to/nonexistent/file.txt";
    // Then:
    Assertions.assertThrows(RuntimeException.class, () -> wordFrequencyService.getWordFrequency(nonExistentFilePath, word, true));
  }
}