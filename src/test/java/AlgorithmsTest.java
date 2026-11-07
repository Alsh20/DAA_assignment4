import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class AlgorithmsTest {
    @Test
    void editDistanceMatchesReferenceValues() {
        assertDistance("kitten", "sitting", 3);
        assertDistance("flaw", "lawn", 2);
        assertDistance("intention", "execution", 5);
        assertDistance("", "abc", 3);
        assertDistance("abc", "", 3);
        assertDistance("abc", "abc", 0);
    }

    @Test
    void editDistanceImplementationsAgreeOnOneHundredRandomShortPairs() {
        Random random = new Random(2026);
        for (int trial = 0; trial < 100; trial++) {
            String a = randomString(random, random.nextInt(7));
            String b = randomString(random, random.nextInt(7));
            assertDistance(a, b, EditDistanceTable.distance(a, b));
        }
    }

    @Test
    void tableAndTwoRowsComputeSameCellsButUseDifferentStorage() {
        String a = "algorithm";
        String b = "altruistic";
        Metrics table = new Metrics();
        Metrics rows = new Metrics();

        int tableAnswer = EditDistanceTable.distance(a, b, table);
        int rowAnswer = EditDistanceTwoRows.distance(a, b, rows);

        assertEquals(tableAnswer, rowAnswer);
        assertEquals((long) a.length() * b.length(), table.workUnits());
        assertEquals(table.workUnits(), rows.workUnits());
        assertEquals((long) (a.length() + 1) * (b.length() + 1), table.storageCells());
        assertEquals(2L * (b.length() + 1), rows.storageCells());
        assertTrue(rows.storageCells() < table.storageCells());
    }

    @Test
    void lpsMatchesReferenceArrays() {
        assertArrayEquals(new int[] {0, 1, 2, 0, 1, 2, 3, 3},
                KmpStringSearch.lps("AAACAAAA"));
        assertArrayEquals(new int[] {0, 0, 1, 2, 0, 1, 2, 3, 4},
                KmpStringSearch.lps("ABABCABAB"));
        assertArrayEquals(new int[0], KmpStringSearch.lps(""));
    }

    @Test
    void searchMatchesReferencesAndFindsOverlaps() {
        assertEquals(List.of(10),
                NaiveStringSearch.search("ABABDABACDABABCABAB", "ABABCABAB"));
        assertEquals(List.of(0, 1, 2, 3), NaiveStringSearch.search("AAAAA", "AA"));
        assertEquals(List.of(0, 2, 4), KmpStringSearch.search("ABABABA", "ABA"));
        assertEquals(List.of(0, 1, 2, 3), KmpStringSearch.search("AAAAA", "AA"));
        assertEquals(List.of(), KmpStringSearch.search("ABC", "ABCD"));
        assertEquals(List.of(), NaiveStringSearch.search("ABC", "ABCD"));
    }

    @Test
    void emptyPatternIsRejectedByBothSearches() {
        assertThrows(IllegalArgumentException.class, () -> NaiveStringSearch.search("text", ""));
        assertThrows(IllegalArgumentException.class, () -> KmpStringSearch.search("text", ""));
    }

    @Test
    void naiveAndKmpAgreeOnOneHundredRandomTexts() {
        Random random = new Random(42);
        for (int trial = 0; trial < 100; trial++) {
            String text = randomString(random, random.nextInt(81));
            String pattern = randomString(random, 1 + random.nextInt(20));
            assertEquals(NaiveStringSearch.search(text, pattern),
                    KmpStringSearch.search(text, pattern));
        }
    }

    @Test
    void repeatedPrefixCausesMoreNaiveComparisons() {
        String text = "A".repeat(5_000);
        String pattern = "A".repeat(63) + "B";
        Metrics naive = new Metrics();
        Metrics kmp = new Metrics();

        List<Integer> naiveMatches = NaiveStringSearch.search(text, pattern, naive);
        List<Integer> kmpMatches = KmpStringSearch.search(text, pattern, kmp);

        assertEquals(naiveMatches, kmpMatches);
        assertTrue(naive.workUnits() > 20 * kmp.workUnits());
    }

    private static void assertDistance(String a, String b, int expected) {
        assertEquals(expected, EditDistanceRecursive.distance(a, b));
        assertEquals(expected, EditDistanceTable.distance(a, b));
        assertEquals(expected, EditDistanceTwoRows.distance(a, b));
    }

    private static String randomString(Random random, int length) {
        String alphabet = "ABCD";
        StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return value.toString();
    }
}
