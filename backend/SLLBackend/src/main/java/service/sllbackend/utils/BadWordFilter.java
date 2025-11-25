package service.sllbackend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to filter bad words from text content.
 * Loads bad words from badwords.txt file in resources directory.
 */
@Component
@Slf4j
public class BadWordFilter {
    
    private final Set<String> badWords = new HashSet<>();
    
    @PostConstruct
    public void init() {
        loadBadWords();
    }
    
    /**
     * Load bad words from the badwords.txt file
     */
    private void loadBadWords() {
        try {
            ClassPathResource resource = new ClassPathResource("badwords.txt");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toLowerCase();
                    // Skip empty lines and comments
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        badWords.add(line);
                    }
                }
            }
            log.info("Loaded {} bad words from badwords.txt", badWords.size());
        } catch (IOException e) {
            log.error("Failed to load bad words file", e);
        }
    }
    
    /**
     * Check if the given text contains any bad words
     * @param text Text to check
     * @return true if bad word is found, false otherwise
     */
    public boolean containsBadWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        // Convert to lowercase and split into words
        String lowerText = text.toLowerCase();
        
        // Check if any bad word appears in the text
        for (String badWord : badWords) {
            // Use word boundaries to match whole words
            if (lowerText.matches(".*\\b" + badWord + "\\b.*")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get all bad words found in the text
     * @param text Text to check
     * @return Set of bad words found
     */
    public Set<String> findBadWords(String text) {
        Set<String> found = new HashSet<>();
        if (text == null || text.isEmpty()) {
            return found;
        }
        
        String lowerText = text.toLowerCase();
        for (String badWord : badWords) {
            if (lowerText.matches(".*\\b" + badWord + "\\b.*")) {
                found.add(badWord);
            }
        }
        
        return found;
    }
}
