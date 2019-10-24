package io.xpire.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.xpire.commons.core.Messages;
import io.xpire.model.item.Name;
import io.xpire.testutil.Assert;
import io.xpire.testutil.TypicalItems;


public class StringUtilTest {

    //---------------- Tests for containsWordIgnoreCase --------------------------------------

    /*
     * Invalid equivalence partitions for word: null, empty, multiple words
     * Invalid equivalence partitions for sentence: null
     * The four test cases below test one invalid input at a time.
     */

    @Test
    public void containsWordIgnoreCase_nullWord_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> StringUtil
                .containsPhraseIgnoreCase("typical sentence", null));
    }

    @Test
    public void containsWordIgnoreCase_emptyWord_throwsIllegalArgumentException() {
        Assert.assertThrows(IllegalArgumentException.class, "Phrase parameter cannot be empty", ()
            -> StringUtil.containsPhraseIgnoreCase("typical sentence", "  "));
    }

    @Test
    public void containsWordIgnoreCase_nullSentence_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> StringUtil
                .containsPhraseIgnoreCase(null, "abc"));
    }

    /*
     * Valid equivalence partitions for word:
     *   - any word
     *   - word containing symbols/numbers
     *   - word with leading/trailing spaces
     *
     * Valid equivalence partitions for sentence:
     *   - empty string
     *   - one word
     *   - multiple words
     *   - sentence with extra spaces
     *
     * Possible scenarios returning true:
     *   - matches first word in sentence
     *   - last word in sentence
     *   - middle word in sentence
     *   - matches multiple words
     *
     * Possible scenarios returning false:
     *   - query word matches part of a sentence word
     *   - sentence word matches part of the query word
     *
     * The test method below tries to verify all above with a reasonably low number of test cases.
     */

    @Test
    public void containsWordIgnoreCase_validInputs_correctResult() {

        // Empty sentence
        assertFalse(StringUtil.containsPhraseIgnoreCase("", "abc")); // Boundary case
        assertFalse(StringUtil.containsPhraseIgnoreCase("    ", "123"));

        // Matches a partial word only
        assertTrue(StringUtil.containsPhraseIgnoreCase("aaa bbb ccc", "bb")); // Sentence word bigger than query word
        assertFalse(StringUtil.containsPhraseIgnoreCase("aaa bbb ccc", "bbbb")); // Query word bigger than sentence word

        // Matches word in the sentence, different upper/lower case letters
        assertTrue(StringUtil.containsPhraseIgnoreCase("aaa bBb ccc", "Bbb")); // First word (boundary case)
        assertTrue(StringUtil.containsPhraseIgnoreCase("aaa bBb ccc@1", "CCc@1")); // Last word (boundary case)
        assertTrue(StringUtil.containsPhraseIgnoreCase("  AAA   bBb   ccc  ", "aaa")); // Sentence has extra spaces
        assertTrue(StringUtil.containsPhraseIgnoreCase("Aaa", "aaa")); // Only one word in sentence (boundary case)
        assertTrue(StringUtil.containsPhraseIgnoreCase("aaa bbb ccc", "  ccc  ")); // Leading/trailing spaces

        // Matches multiple words in sentence
        assertTrue(StringUtil.containsPhraseIgnoreCase("AAA bBb ccc  bbb", "bbB"));
    }

    //---------------- Tests for getDetails --------------------------------------

    /*
     * Equivalence Partitions: null, valid throwable object
     */

    @Test
    public void getDetails_exceptionGiven() {
        assertTrue(StringUtil.getDetails(new FileNotFoundException("file not found"))
            .contains("java.io.FileNotFoundException: file not found"));
    }

    @Test
    public void getDetails_nullGiven_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> StringUtil.getDetails(null));
    }

    //---------------- Tests for isNonZeroUnsignedInteger --------------------------------------

    @Test
    public void isNonZeroUnsignedInteger() {

        // EP: empty strings
        assertFalse(StringUtil.isNonZeroUnsignedInteger("")); // Boundary value
        assertFalse(StringUtil.isNonZeroUnsignedInteger("  "));

        // EP: not a number
        assertFalse(StringUtil.isNonZeroUnsignedInteger("a"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("aaa"));

        // EP: zero
        assertFalse(StringUtil.isNonZeroUnsignedInteger("0"));

        // EP: zero as prefix
        assertTrue(StringUtil.isNonZeroUnsignedInteger("01"));

        // EP: signed numbers
        assertFalse(StringUtil.isNonZeroUnsignedInteger("-1"));
        assertFalse(StringUtil.isNonZeroUnsignedInteger("+1"));

        // EP: numbers with white space
        assertFalse(StringUtil.isNonZeroUnsignedInteger(" 10 ")); // Leading/trailing spaces
        assertFalse(StringUtil.isNonZeroUnsignedInteger("1 0")); // Spaces in the middle

        // EP: number larger than Integer.MAX_VALUE
        assertFalse(StringUtil.isNonZeroUnsignedInteger(Long.toString(Integer.MAX_VALUE + 1)));

        // EP: valid numbers, should return true
        assertTrue(StringUtil.isNonZeroUnsignedInteger("1")); // Boundary value
        assertTrue(StringUtil.isNonZeroUnsignedInteger("10"));
    }

    //---------------- Tests for isNonNegativeInteger --------------------------------------
    @Test
    public void isNonNegativeInteger() {

        // EP: empty strings
        assertFalse(StringUtil.isNonNegativeInteger(""));
        assertFalse(StringUtil.isNonNegativeInteger(" "));

        // EP: not a number
        assertFalse(StringUtil.isNonNegativeInteger("a"));
        assertFalse(StringUtil.isNonNegativeInteger("aaa"));

        // EP: zero
        assertTrue(StringUtil.isNonNegativeInteger("0"));

        // EP: zero as prefix
        assertTrue(StringUtil.isNonNegativeInteger("01"));

        // EP: signed numbers
        assertFalse(StringUtil.isNonNegativeInteger("-1"));
        assertTrue(StringUtil.isNonNegativeInteger("1")); //"+1" is successfully parsed by Integer#parseInt(String)
    }

    //---------------- Tests for convertToSentenceCase --------------------------------------
    @Test
    public void convertToSentenceCase() {
        // EP: empty strings
        assertEquals(StringUtil.convertToSentenceCase(""), "");
        assertEquals(StringUtil.convertToSentenceCase(" "), " ");

        // EP: upper case strings
        assertEquals(StringUtil.convertToSentenceCase("FRUIT"), "Fruit");
        assertEquals(StringUtil.convertToSentenceCase("SWEET"), "Sweet");

        // EP: lower case strings
        assertEquals(StringUtil.convertToSentenceCase("fruit"), "Fruit");
        assertEquals(StringUtil.convertToSentenceCase("sweet"), "Sweet");

        // EP: mix of upper and lower case strings
        assertEquals(StringUtil.convertToSentenceCase("frUit"), "Fruit");
        assertEquals(StringUtil.convertToSentenceCase("SweEt"), "Sweet");
    }

    //---------------- Tests for computeDistance --------------------------------------
    @Test
    public void computeDistance() {

        // Equal strings
        assertEquals(StringUtil.computeDistance("banana", "banana"), 0);

        // Both empty strings
        assertEquals(StringUtil.computeDistance("", ""), 0);

        // Either as empty strings
        assertEquals(StringUtil.computeDistance("banana", ""), 6);

        // Either as empty strings
        assertEquals(StringUtil.computeDistance("", "strawberry"), 10);

        // Transpositions
        assertEquals(StringUtil.computeDistance("aplpe", "apple"), 1);
        assertEquals(StringUtil.computeDistance("friut", "fruit"), 1);

        // Substitutions
        assertEquals(StringUtil.computeDistance("apble", "apple"), 1);
        assertEquals(StringUtil.computeDistance("abble", "apple"), 2);
        assertEquals(StringUtil.computeDistance("abbee", "apple"), 3);

        // Deletions
        assertEquals(StringUtil.computeDistance("fruitt", "fruit"), 1);
        assertEquals(StringUtil.computeDistance("cllear", "clear"), 1);
    }

    //---------------- Tests for getSuggestions --------------------------------------
    @Test
    public void getSuggestions() {
        Set<Name> allItems = TypicalItems.getTypicalItems()
                                         .stream()
                                         .map(x-> x.getName())
                                         .collect(Collectors.toSet());
        Set<String> allNames = allItems.stream()
                                       .map(Name::toString)
                                       .map(x -> x.split("\\s+"))
                                       .flatMap(Arrays::stream)
                                       .collect(Collectors.toSet());
        int limit = 2;
        // Similar keywords recommended
        assertEquals(StringUtil.getSuggestions("banaa", allNames, limit), "[Banana]");
        assertEquals(StringUtil.getSuggestions("fihs", allNames, limit), "[Fish]");
        assertEquals(StringUtil.getSuggestions("mik", allNames, limit), "[Milk]");

        // Kiwi not found as it is not in typicalItems
        assertEquals(StringUtil.getSuggestions("kiww", allNames, limit), "");
    }

    //---------------- Tests for findSimilar --------------------------------------
    @Test
    public void findSimilar() {
        Set<Name> allItems = TypicalItems.getTypicalItems()
                                         .stream()
                                         .map(x-> x.getName())
                                         .collect(Collectors.toSet());
        Set<String> allNames = allItems.stream()
                                       .map(Name::toString)
                                       .map(x -> x.split("\\s+"))
                                       .flatMap(Arrays::stream)
                                       .collect(Collectors.toSet());
        int limit = 2;
        // Similar keywords recommended
        assertEquals(StringUtil.findSimilar("banaa", allNames, limit),
                String.format(Messages.MESSAGE_SUGGESTIONS, "[Banana]"));

    }

}