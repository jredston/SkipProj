import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * SSTests
 *
 * @author Shashank Singh
 * @version 1.6
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SSTests {

    // region #VARIABLES
    private CoinFlipper coinFlipper;
    private SkipListInterface<Integer> list;
    private int[] rows;
    private int[] cols;
    private int[] vals;
    private int[] prevs;
    private int[] nexts;
    private int[] ups;
    private int[] downs;
    private int[] levels;

    private static final int TIMEOUT = 200;
    private static final int LONG_TIMEOUT = 8000;
    // endregion

    // region [A] #CONSTRUCTORS [COMPLETE COVERAGE]
    @Test(timeout = TIMEOUT)
    public void tAConstructor() {
        initList("");
        assertListEmpty();
    }
    // endregion

    // region [B] #EXCEPTIONS [COMPLETE COVERAGE]
    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void tB01ExceptionFirst() {
        initList("");
        list.first();
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void tB02ExceptionLast() {
        initList("");
        list.last();
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void tB03ExceptionPut() {
        initList("HT");
        list.put(null);
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void tB04ExceptionRemoveNull() {
        initList("HT");
        list.remove(null);
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void tB05ExceptionRemoveNotFound() {
        initList("HT");
        list.put(2);
        list.remove(4);
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void tB06ExceptionContains() {
        initList("");
        list.contains(null);
    }

    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void tB07ExceptionGetNull() {
        initList("");
        list.get(null);
    }

    @Test(timeout = TIMEOUT, expected = NoSuchElementException.class)
    public void tB08ExceptionGetNotFound() {
        initList("HT");
        list.put(2);
        list.get(4);
    }
    // endregion

    // region [C] #PUT [COMPLETE COVERAGE]
    @Test(timeout = TIMEOUT)
    public void tC01Put() {
        // pre-determined results of coin flips
        // uses a subclass of CoinFlipper that allows you to force desired
        // outcomes
        initList("HTTHT");

        // helper method for putting multiple items into the Skip List
        //putItems(1, 2, 3);
        list.put(1);
        list.put(2);
        list.put(3);

        /*
         * setting up expected values for all properties of the SkipListNodes
         *
         * row is 0 indexed and starts from the top (look below for example)
         *     - the indexing excludes the empty level
         *
         * col is 0 indexed and starts from the left (look below for example)
         *     - the indexing excludes the first column of phantom nodes
         *
         * vals are the expected values at the corresponding row and col
         *
         * prevs are the expected values of the previous node
         *     - put -1 for null data
         *
         * nexts are the expected values of the next node
         *     - put -1 for a null node
         *
         * ups are the expected values of the up node
         *     - put -1 for a null node
         *
         * downs are the expected values of the down node
         *     - put -1 for a null node
         *
         * levels are the expected levels of the node
         */
        rows   = new int[]  {0,  0,  1,  1,  1};
        cols   = new int[]  {0,  1,  0,  1,  2};
        vals   = new int[]  {1,  3,  1,  2,  3};
        prevs  = new int[] {-1,  1, -1,  1,  2};
        nexts  = new int[]  {3, -1,  2,  3, -1};
        ups    = new int[] {-1, -1,  1, -1,  3};
        downs  = new int[]  {1,  3, -1, -1, -1};
        levels = new int[]  {2,  2,  1,  1,  1};
        /*
         *  null                                          level: 3
         *   |
         *  null -- 1 (0, 0) -------------- 3 (0, 1)      level: 2
         *   |      |                       |
         *  null -- 1 (1, 0) -- 2 (1, 1) -- 3 (1, 2)      level: 1
         */
        assertListEquals(3);
    }

    @Test(timeout = TIMEOUT)
    public void tC02PutManyLevels() {
        // tests multiple heads in succession
        initList("HHHHHHHHHHT");

        putItems(9);

        rows   = new int[]  {0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10};
        cols   = new int[]  {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0};
        vals   = new int[]  {9,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9};
        prevs  = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        nexts  = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        ups    = new int[] {-1,  9,  9,  9,  9,  9,  9,  9,  9,  9,  9};
        downs  = new int[]  {9,  9,  9,  9,  9,  9,  9,  9,  9,  9, -1};
        levels = new int[] {11, 10,  9,  8,  7,  6,  5,  4,  3,  2,  1};

        assertListEquals(1);
    }

    @Test(timeout = TIMEOUT)
    public void tC03PutStepUp() {
        // tests adding increasing upwards-duplication of items
        initList("THTHHTHHHTHHHHTHHHHHT");

        putItems(1, 2, 3, 4, 5, 6);

        rows   = new int[]  {0,  1,  1,  2,  2,  2,  3,  3,  3,  3,
                             4,  4,  4,  4,  4,  5,  5,  5,  5,  5,  5};
        cols   = new int[]  {0,  0,  1,  0,  1,  2,  0,  1,  2,  3,
                             0,  1,  2,  3,  4,  0,  1,  2,  3,  4,  5};
        vals   = new int[]  {6,  5,  6,  4,  5,  6,  3,  4,  5,  6,
                             2,  3,  4,  5,  6,  1,  2,  3,  4,  5,  6};
        prevs  = new int[] {-1, -1,  5, -1,  4,  5, -1,  3,  4,  5,
                            -1,  2,  3,  4,  5, -1,  1,  2,  3,  4,  5};
        nexts  = new int[] {-1,  6, -1,  5,  6, -1,  4,  5,  6, -1,
                            3,   4,  5,  6, -1,  2,  3,  4,  5,  6, -1};
        ups    = new int[] {-1, -1,  6, -1,  5,  6, -1,  4,  5,  6,
                            -1,  3,  4,  5,  6, -1,  2,  3,  4,  5,  6};
        downs  = new int[]  {6,  5,  6,  4,  5,  6,  3,  4,  5,  6,
                             2,  3,  4,  5,  6, -1, -1, -1, -1, -1, -1};
        levels = new int[]  {6,  5,  5,  4,  4,  4,  3,  3,  3,  3,
                             2,  2,  2,  2,  2,  1,  1,  1,  1,  1,  1};

        assertListEquals(6);
    }

    @Test(timeout = TIMEOUT)
    public void tC04PutStepDownReversed() {
        // tests adding decreasing upwards-duplication of items in reverse order
        initList("THTHHTHHHTHHHHTHHHHHT");

        putItems(6, 5, 4, 3);

        rows   = new int[]  {0,  1,  1,  2,  2,  2,  3,  3,  3,  3};
        cols   = new int[]  {0,  0,  1,  0,  1,  2,  0,  1,  2,  3};
        vals   = new int[]  {3,  3,  4,  3,  4,  5,  3,  4,  5,  6};
        prevs  = new int[] {-1, -1,  3, -1,  3,  4, -1,  3,  4,  5};
        nexts  = new int[] {-1,  4, -1,  4,  5, -1,  4,  5,  6, -1};
        ups    = new int[] {-1,  3, -1,  3,  4, -1,  3,  4,  5, -1};
        downs  = new int[]  {3,  3,  4,  3,  4,  5, -1, -1, -1, -1};
        levels = new int[]  {4,  3,  3,  2,  2,  2,  1,  1,  1,  1};

        assertListEquals(4);
    }

    @Test(timeout = TIMEOUT)
    public void tC05PutStaggered() {
        // tests adding BST like structure with random order of numbers
        initList("TTHTTHHTHTT");

        putItems(3, 7, 6, 1, 4, 2, 5);

        rows   = new int[]  {0,  1,  1,  1,  2,  2,  2,  2,  2,  2,  2};
        cols   = new int[]  {0,  0,  1,  2,  0,  1,  2,  3,  4,  5,  6};
        vals   = new int[]  {4,  2,  4,  6,  1,  2,  3,  4,  5,  6,  7};
        prevs  = new int[] {-1, -1,  2,  4, -1,  1,  2,  3,  4,  5,  6};
        nexts  = new int[] {-1,  4,  6, -1,  2,  3,  4,  5,  6,  7, -1};
        ups    = new int[] {-1, -1,  4, -1, -1,  2, -1,  4, -1,  6, -1};
        downs  = new int[]  {4,  2,  4,  6, -1, -1, -1, -1, -1, -1, -1};
        levels = new int[]  {3,  2,  2,  2,  1,  1,  1,  1,  1,  1,  1};

        assertListEquals(7);
    }

    @Test(timeout = LONG_TIMEOUT)
    public void tC06PutRandomized() {
        // random catch-all
        // if you encounter an error here that is not caught by any of the
        // cases above, please post a comment on the HW08: Shared JUnits test
        Random gen = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; ++i) {
            coinFlipper = new CoinFlipper(gen);
            list = new SkipList<>(coinFlipper);
            List<Integer> values = IntStream.range(0, gen.nextInt(1000)).boxed()
                    .collect(Collectors.toList());
            Collections.shuffle(values, gen);
            values.forEach(list::put);
            assertListProperties(list);
        }
    }
    // endregion

    // region [D] #REMOVE [COMPLETE COVERAGE]
    // Author: Hayden Flinner
    @Test(timeout = TIMEOUT)
    public void tD01RemoveDeleteEmptyLevels() {
        // tests that remove deletes a level if the last element on that level
        // was deleted
        initList("HHHHTHWG!");
        list.put(2);
        list.remove(2);
        assertListEmpty();
    }

    @Test(timeout = TIMEOUT)
    public void tD02RemoveSingle() {
        // remove an element that occurs only once
        initList("TTHTTHHTHTT");

        putItems(3, 7, 6, 1, 4, 2, 5);
        rows   = new int[]  {0,  1,  1,  1,  2,  2,  2,  2,  2,  2,  2};
        cols   = new int[]  {0,  0,  1,  2,  0,  1,  2,  3,  4,  5,  6};
        vals   = new int[]  {4,  2,  4,  6,  1,  2,  3,  4,  5,  6,  7};
        prevs  = new int[] {-1, -1,  2,  4, -1,  1,  2,  3,  4,  5,  6};
        nexts  = new int[] {-1,  4,  6, -1,  2,  3,  4,  5,  6,  7, -1};
        ups    = new int[] {-1, -1,  4, -1, -1,  2, -1,  4, -1,  6, -1};
        downs  = new int[]  {4,  2,  4,  6, -1, -1, -1, -1, -1, -1, -1};
        levels = new int[]  {3,  2,  2,  2,  1,  1,  1,  1,  1,  1,  1};
        assertListEquals(7);

        assertEquals(5, (int) list.remove(5));
        rows   = new int[]  {0,  1,  1,  1,  2,  2,  2,  2,  2,  2};
        cols   = new int[]  {0,  0,  1,  2,  0,  1,  2,  3,  4,  5};
        vals   = new int[]  {4,  2,  4,  6,  1,  2,  3,  4,  6,  7};
        prevs  = new int[] {-1, -1,  2,  4, -1,  1,  2,  3,  4,  6};
        nexts  = new int[] {-1,  4,  6, -1,  2,  3,  4,  6,  7, -1};
        ups    = new int[] {-1, -1,  4, -1, -1,  2, -1,  4,  6, -1};
        downs  = new int[]  {4,  2,  4,  6, -1, -1, -1, -1, -1, -1};
        levels = new int[]  {3,  2,  2,  2,  1,  1,  1,  1,  1,  1};
        assertListEquals(6);
    }

    @Test(timeout = TIMEOUT)
    public void tD03RemoveMultipleLevels() {
        // remove an element that was duplicated multiple times
        // also tests for deleting a level if the level's last element
        // is removed
        initList("TTHTTHHTHTT");

        putItems(3, 7, 6, 1, 4, 2, 5);
        rows   = new int[]  {0,  1,  1,  1,  2,  2,  2,  2,  2,  2,  2};
        cols   = new int[]  {0,  0,  1,  2,  0,  1,  2,  3,  4,  5,  6};
        vals   = new int[]  {4,  2,  4,  6,  1,  2,  3,  4,  5,  6,  7};
        prevs  = new int[] {-1, -1,  2,  4, -1,  1,  2,  3,  4,  5,  6};
        nexts  = new int[] {-1,  4,  6, -1,  2,  3,  4,  5,  6,  7, -1};
        ups    = new int[] {-1, -1,  4, -1, -1,  2, -1,  4, -1,  6, -1};
        downs  = new int[]  {4,  2,  4,  6, -1, -1, -1, -1, -1, -1, -1};
        levels = new int[]  {3,  2,  2,  2,  1,  1,  1,  1,  1,  1,  1};
        assertListEquals(7);

        assertEquals(4, (int) list.remove(4));
        rows   = new int[]  {0,  0,  1,  1,  1,  1,  1,  1};
        cols   = new int[]  {0,  1,  0,  1,  2,  3,  4,  5};
        vals   = new int[]  {2,  6,  1,  2,  3,  5,  6,  7};
        prevs  = new int[] {-1,  2, -1,  1,  2,  3,  5,  6};
        nexts  = new int[]  {6, -1,  2,  3,  5,  6,  7, -1};
        ups    = new int[] {-1, -1, -1,  2, -1, -1,  6, -1};
        downs  = new int[]  {2,  6, -1, -1, -1, -1, -1, -1};
        levels = new int[]  {2,  2,  1,  1,  1,  1,  1,  1};
        assertListEquals(6);
    }

    @Test(timeout = TIMEOUT)
    public void tD04RemoveAll() {
        // remove all elements that were added
        // list should revert back to original (empty) state
        initList("TTHTTHHTHTT");
        putItems(3, 7, 6, 1, 4, 2, 5);
        list.remove(3);
        list.remove(7);
        list.remove(6);
        list.remove(1);
        list.remove(4);
        list.remove(2);
        list.remove(5);
        assertListEmpty();
    }

    @Test(timeout = LONG_TIMEOUT)
    public void tD05RemoveRandomized() {
        // random catch-all
        // if you encounter an error here that is not caught by any of the
        // cases above, please post a comment on the HW08: Shared JUnits test
        Random gen = new Random(System.currentTimeMillis());
        for (int i = 0; i < 500; ++i) {
            coinFlipper = new CoinFlipper(gen);
            list = new SkipList<>(coinFlipper);
            List<Integer> values = IntStream.range(0, gen.nextInt(500)).boxed()
                    .collect(Collectors.toList());
            Collections.shuffle(values, gen);
            values.forEach(list::put);
            int expectedSize = values.size();
            for (Integer val : values) {
                assertEquals(val, list.remove(val));
                assertListProperties(list);
                assertEquals(--expectedSize, list.size());
            }
            assertListEmpty();
        }
    }
    // endregion

    // PLEASE MAKE SURE EVERYTHING ABOVE THIS COMMENT WORKS BEFORE TESTING
    // ANYTHING BELOW THIS COMMENT SINCE IT IS ALL DEPENDENT ON THE ABOVE
    // METHODS WORKING CORRECTLY

    // region [E] #FIRST [COMPLETE COVERAGE]
    // Author: Akhilesh Aji
    @Test(timeout = TIMEOUT)
    public void tE01First() {
        initList("HHTTHT");
        list.put(5);
        assertEquals(new Integer(5), list.first());
        list.put(3);
        assertEquals(new Integer(3), list.first());
        list.remove(3);
        assertEquals(new Integer(5), list.first());
        list.put(7);
        assertEquals(new Integer(5), list.first());
    }

    @Test(timeout = LONG_TIMEOUT)
    public void tE02FirstRandomized() {
        // random catch-all
        // if you encounter an error here that is not caught by any of the
        // cases above, please post a comment on the HW08: Shared JUnits test
        Random gen = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; ++i) {
            coinFlipper = new CoinFlipper(gen);
            list = new SkipList<>(coinFlipper);
            List<Integer> values = IntStream.range(gen.nextInt(20),
                    21 + gen.nextInt(100)).boxed().collect(Collectors.toList());
            Collections.shuffle(values, gen);
            values.forEach(list::put);
            assertEquals(Collections.min(values), list.first());
        }
    }
    // endregion

    // region [F] #LAST [COMPLETE COVERAGE]
    // Author: Akhilesh Aji
    @Test(timeout = TIMEOUT)
    public void tF01Last() {
        initList("HHTTHT");
        list.put(5);
        assertEquals(new Integer(5), list.last());
        list.put(7);
        assertEquals(new Integer(7), list.last());
        list.remove(7);
        assertEquals(new Integer(5), list.last());
        list.put(3);
        assertEquals(new Integer(5), list.last());
    }

    @Test(timeout = TIMEOUT)
    public void tF02LastEfficiencyCheck() {
        initList("THT");
        putItems(1, 2);
        list.getHead().getDown().getDown().getNext().setNext(null);
        assertEquals("You shouldn't be traversing the whole bottom level Linked"
                + " List to find the last element", 2, (int) list.last());
    }

    @Test(timeout = LONG_TIMEOUT)
    public void tF03LastRandomized() {
        // random catch-all
        // if you encounter an error here that is not caught by any of the
        // cases above, please post a comment on the HW08: Shared JUnits test
        Random gen = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; ++i) {
            coinFlipper = new CoinFlipper(gen);
            list = new SkipList<>(coinFlipper);
            List<Integer> values = IntStream.range(20, 21 + gen.nextInt(1000))
                    .boxed().collect(Collectors.toList());
            Collections.shuffle(values, gen);
            values.forEach(list::put);
            assertEquals(Collections.max(values), list.last());
        }
    }
    // endregion

    // region [G] #CONTAINS [COMPLETE COVERAGE]
    // Author: Taylor Hearn
    @Test(timeout = TIMEOUT)
    public void tG01Contains() {
        initList("THTHHT");

        list.put(42);
        assertTrue(list.contains(42));

        list.put(-2);
        assertTrue(list.contains(-2));

        list.put(8);
        assertTrue(list.contains(8));

        assertFalse(list.contains(50));
        assertFalse(list.contains(30));
        assertFalse(list.contains(-100));
    }

    @Test(timeout = TIMEOUT)
    public void tG02ContainsEfficiencyCheck() {
        initList("TTHT");
        putItems(1, 2, 3);
        list.getHead().getDown().getDown().getNext().getNext().setNext(null);
        assertTrue("You shouldn't be traversing the whole bottom level Linked"
                + " List to check for presence of an item", list.contains(3));
    }

    @Test(timeout = LONG_TIMEOUT)
    public void tG03ContainsRandomized() {
        // random catch-all
        // if you encounter an error here that is not caught by any of the
        // cases above, please post a comment on the HW08: Shared JUnits test
        Random gen = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; ++i) {
            coinFlipper = new CoinFlipper(gen);
            list = new SkipList<>(coinFlipper);
            List<Integer> values = IntStream.range(0, 500 + gen.nextInt(500))
                    .boxed().collect(Collectors.toList());
            Collections.shuffle(values, gen);
            values.forEach(list::put);
            for (int j = 0; j < 200; ++j) {
                int num = 500 + gen.nextInt(1000);
                assertEquals(values.contains(num), list.contains(num));
            }
        }
    }
    // endregion

    // region [H] #GET [COMPLETE COVERAGE]
    // Author: Taylor Hearn
    @Test(timeout = TIMEOUT)
    public void tH01Get() {
        initList("HHHHTHHTT");

        list.put(0);
        assertEquals((Integer) 0, list.get(0));

        list.put(477);
        assertEquals((Integer) 0, list.get(0));
        assertEquals((Integer) 477, list.get(477));

        list.put(9);
        assertEquals((Integer) 0, list.get(0));
        assertEquals((Integer) 9, list.get(9));
        assertEquals((Integer) 477, list.get(477));
    }

    @Test(timeout = LONG_TIMEOUT)
    public void tH02GetRandomized() {
        // random catch-all
        // if you encounter an error here that is not caught by any of the
        // cases above, please post a comment on the HW08: Shared JUnits test
        Random gen = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; ++i) {
            coinFlipper = new CoinFlipper(gen);
            list = new SkipList<>(coinFlipper);
            List<Integer> values = IntStream.range(0, 500 + gen.nextInt(500))
                    .boxed().collect(Collectors.toList());
            Collections.shuffle(values, gen);
            values.forEach(list::put);
            values.forEach(j -> assertEquals(j, list.get(j)));
        }
    }
    // endregion

    // region [I] #DATASET [COMPLETE COVERAGE]
    // Author: Hayden Flinner
    @Test(timeout = TIMEOUT)
    public void tI01DataSetEmptyList() {
        initList("");
        assertEquals(new HashSet<>(), list.dataSet());
        assertFalse(list.contains(392));
    }

    @Test(timeout = LONG_TIMEOUT)
    public void tI02DataSetRandomized() {
        // random catch-all
        // if you encounter an error here that is not caught by any of the
        // cases above, please post a comment on the HW08: Shared JUnits test
        Random gen = new Random(System.currentTimeMillis());
        for (int i = 0; i < 1000; ++i) {
            coinFlipper = new CoinFlipper(gen);
            list = new SkipList<>(coinFlipper);
            List<Integer> values = IntStream.range(0, 500 + gen.nextInt(500))
                    .boxed().collect(Collectors.toList());
            Collections.shuffle(values, gen);
            values.forEach(list::put);
            Collections.sort(values);
            assertEquals(values, new ArrayList<>(list.dataSet()));
        }
    }
    // endregion

    // region [J] #CLEAR [COMPLETE COVERAGE]
    // Author: Taylor Hearn
    @Test(timeout = TIMEOUT)
    public void tJ01Clear() {
        initList("TTTHHTHTHT");
        assertListEmpty();

        putItems(1, 2, 3);
        list.clear();
        assertListEmpty();

        putItems(-1, -2, 0);
        list.clear();
        assertListEmpty();
    }
    // endregion

    // region [K] #TOSTRING [COMPLETE COVERAGE]
    @Test(timeout = TIMEOUT)
    public void tK02ToString() {
        //\u000AinitList("TTTTTTTTT");
        //\u000AputItems(70, 85, 67, 75, 32, 84, 72, 73, 83);
        //\u000ASystem.out.println(list);
    }
    // endregion

    // region #HELPER CONSTRUCTS
    /**
     * Helper method for initializing the Skip List
     *
     * @param outcomes predetermined flip outcomes
     */
    private void initList(String outcomes) {
        coinFlipper = new RiggedCoinFlipper(outcomes);
        list = new SkipList<>(coinFlipper);
    }

    /**
     * Helper method for adding multiple items to the Skip List
     *
     * @param items array of items to add
     */
    private void putItems(int... items) {
        for (int item : items) {
            list.put(item);
        }
    }

    /**
     * Helper method for checking Skip List's correctness
     *
     * @param size expected size of the Skip List
     */
    private void assertListEquals(int size) {
        assertEquals("Unexpected size", size, list.size());

        // make sure that head is on its own level
        SkipListNode<Integer> node = list.getHead();
        assertNull(node.getNext());
        assertNull(node.getPrev());
        assertNull(node.getUp());

        // check phantom nodes column is properly linked both ways (up and down)
        for (int j = 0; j <= rows[rows.length - 1] + 1; ++j) {
            assertEquals("Phantom node has unexpected level value",
                    rows[rows.length - 1] + 2 - j, node.getLevel());
            assertNull(node.getData());
            if (node.getDown() != null) {
                node = node.getDown();
            }
        }
        for (int j = rows[rows.length - 1] + 1; j >= 0; --j) {
            assertEquals("Phantom node has unexpected level value",
                    rows[rows.length - 1] + 2 - j, node.getLevel());
            node = node.getUp();
        }

        // check each node's properties against their expected values
        for (int i = 0; i < rows.length; ++i) {
            // move to current node
            node = list.getHead();
            for (int j = 0; j <= rows[i]; ++j) {
                node = node.getDown();
            }
            for (int j = 0; j <= cols[i]; ++j) {
                node = node.getNext();
            }

            // check to see if node's data is as expected
            assertEquals(String.format("Unexpected data in row %d index %d",
                    rows[i], cols[i]), vals[i], (int) node.getData());

            // check to see if node's previous is as expected
            if (prevs[i] == -1) {
                assertNull(String.format("Expected phantom prev for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        node.getPrev().getData());
            } else {
                assertEquals(String.format("Unexpected prev value for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        prevs[i], (int) node.getPrev().getData());
            }
            assertEquals(node.getLevel(), node.getPrev().getLevel());

            // check to see if node's next is as expected
            if (nexts[i] == -1) {
                assertNull(String.format("Expected null next value for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        node.getNext());
            } else {
                assertEquals(String.format("Unexpected next value for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        nexts[i], (int) node.getNext().getData());
                assertEquals(node.getLevel(), node.getNext().getLevel());
            }

            // check to see if node's up is as expected
            if (ups[i] == -1) {
                assertNull(String.format("Expected null up value for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        node.getUp());
            } else {
                assertEquals(String.format("Unexpected up value for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        ups[i], (int) node.getUp().getData());
                assertEquals(node.getLevel() + 1, node.getUp().getLevel());
            }

            // check to see if node's down is as expected
            if (downs[i] == -1) {
                assertNull(String.format("Expected null down value for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        node.getDown());
            } else {
                assertEquals(String.format("Unexpected down value for data in"
                                + " row %d index %d", rows[i], cols[i]),
                        downs[i], (int) node.getDown().getData());
                assertEquals(node.getLevel() - 1, node.getDown().getLevel());
            }

            // check to see if node's level is as expected
            assertEquals(String.format("Unexpected level for data in"
                            + " row %d index %d", rows[i], cols[i]),
                    levels[i], node.getLevel());
        }
    }

    /**
     * Method for asserting that the Skip List is empty
     */
    private void assertListEmpty() {
        assertNotNull(list.getHead());
        assertEquals(1, list.getHead().getLevel());
        assertNull(list.getHead().getPrev());
        assertNull(list.getHead().getNext());
        assertNull(list.getHead().getUp());
        assertNull(list.getHead().getDown());
        assertEquals(0, list.size());
    }

    /**
     * Helper method for verifying correctness of the Skip List as much as
     * possible. Use this version only for the randomized tests. Otherwise,
     * use the assertListEquals(int) method with specific expected outputs
     *
     * @param lst Skip List to verify
     */
    private void assertListProperties(SkipListInterface<Integer> lst) {
        SkipListNode<Integer> head = lst.getHead();
        assertNull(head.getNext());
        assertNull(head.getPrev());
        assertNull(head.getUp());
        while ((head = head.getDown()) != null) {
            SkipListNode<Integer> node = head;
            Integer prevValue = null;
            while ((node = node.getNext()) != null) {
                if (prevValue != null) {
                    assertTrue(node.getData() > prevValue);
                }
                assertEquals(prevValue, node.getPrev().getData());
                if (node.getUp() != null) {
                    assertEquals(node.getData(), node.getUp().getData());
                    assertEquals(node.getLevel(), node.getUp().getLevel() - 1);
                }
                if (node.getDown() != null) {
                    assertEquals(node.getData(), node.getDown().getData());
                    assertEquals(node.getLevel(),
                                 node.getDown().getLevel() + 1);
                } else {
                    assertEquals(1, node.getLevel());
                }
                prevValue = node.getData();
            }
        }
    }

    private class RiggedCoinFlipper extends CoinFlipper {
        private int numFlips;
        private String outcomes;

        /**
         *
         * Construct a coin flipper object with given outcomes
         * @param outcomes string of pre-determined outcomes
         */
        public RiggedCoinFlipper(String outcomes) {
            this.outcomes = outcomes;
        }

        @Override
        public Coin flipCoin() {
            if (numFlips >= outcomes.length()) {
                throw new IllegalArgumentException("RiggedCoinFlipper has run"
                        + " out of flips to give");
            }
            return outcomes.charAt(numFlips++) == 'H' ? Coin.HEADS : Coin.TAILS;
        }

        @Override
        public int getNumFlips() {
            return numFlips;
        }
    }
    // endregion
}
