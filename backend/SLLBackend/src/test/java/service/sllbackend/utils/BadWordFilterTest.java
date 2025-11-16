package service.sllbackend.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BadWordFilterTest {
    
    private BadWordFilter badWordFilter;
    
    @BeforeEach
    void setUp() {
        badWordFilter = new BadWordFilter();
        badWordFilter.init();
    }
    
    @Test
    void testContainsBadWord_WithBadWord() {
        assertTrue(badWordFilter.containsBadWord("This is a damn product"));
        assertTrue(badWordFilter.containsBadWord("What the hell is this"));
        assertTrue(badWordFilter.containsBadWord("This is stupid"));
    }
    
    @Test
    void testContainsBadWord_WithoutBadWord() {
        assertFalse(badWordFilter.containsBadWord("This is a great product"));
        assertFalse(badWordFilter.containsBadWord("I love this item"));
        assertFalse(badWordFilter.containsBadWord("Excellent quality"));
    }
    
    @Test
    void testContainsBadWord_WithNull() {
        assertFalse(badWordFilter.containsBadWord(null));
    }
    
    @Test
    void testContainsBadWord_WithEmpty() {
        assertFalse(badWordFilter.containsBadWord(""));
    }
    
    @Test
    void testContainsBadWord_CaseInsensitive() {
        assertTrue(badWordFilter.containsBadWord("This is DAMN good"));
        assertTrue(badWordFilter.containsBadWord("This is Stupid"));
        assertTrue(badWordFilter.containsBadWord("HELL no"));
    }
    
    @Test
    void testFindBadWords() {
        Set<String> found = badWordFilter.findBadWords("This damn product is stupid");
        assertEquals(2, found.size());
        assertTrue(found.contains("damn"));
        assertTrue(found.contains("stupid"));
    }
    
    @Test
    void testFindBadWords_NoBadWords() {
        Set<String> found = badWordFilter.findBadWords("This is a great product");
        assertTrue(found.isEmpty());
    }
    
    @Test
    void testContainsBadWord_WordBoundaries() {
        // Should match "hell" as a word
        assertTrue(badWordFilter.containsBadWord("what the hell"));
        
        // Should not match "hell" within "hello"
        assertFalse(badWordFilter.containsBadWord("hello there"));
        
        // Should not match "ass" within "class"
        assertFalse(badWordFilter.containsBadWord("this is a great class"));
    }
}
