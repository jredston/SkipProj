import org.junit.Before;
import org.junit.Test;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

/**
 * Student tests for SkipList.
 *
 * Redistribution in any manner is strictly forbidden.
 *
 * @author CS 1332 TAs
 * @version 1.0
 */
public class SkipListStudentTests {
    private CoinFlipper coinFlipper;
    private SkipListInterface<Integer> list;

    private static final int TIMEOUT = 200;

    @Before
    public void setup() {
        coinFlipper = new CoinFlipper(new Random(568));
        list = new SkipList<>(coinFlipper);
    }

    @Test(timeout = TIMEOUT)
    public void testAdd() {
        list.put(1);
        list.put(2);
        list.put(3);

        assertEquals(3, list.size());

        assertEquals(new Integer(1), list.first());

        assertEquals(new Integer(3), list.last());

        // Expected structure:
        // (null)
        // (null), (1), (3)
        // (null), (1), (2), (3)
        SkipListNode<Integer> head = list.getHead();
        assertNotNull(head);
        assertNull(head.getData());
        assertNull(head.getNext());
        assertEquals(3, head.getLevel());

        head = head.getDown();
        assertEquals(2, head.getLevel());
        assertEquals(new Integer(1), head.getNext().getData());
        assertEquals(new Integer(3), head.getNext().getNext().getData());
        assertNull(head.getNext().getNext().getNext());

        head = head.getDown();
        assertEquals(1, head.getLevel());
        assertEquals(new Integer(1), head.getNext().getData());
        assertEquals(new Integer(2), head.getNext().getNext().getData());
        assertEquals(new Integer(3),
            head.getNext().getNext().getNext().getData());
        assertNull(head.getNext().getNext().getNext().getNext());

        assertEquals(5, coinFlipper.getNumFlips());
    }

    @Test(timeout = TIMEOUT)
    public void testRemove() {
        list.put(1);
        list.put(2);
        list.put(3);

        list.remove(3);
        assertEquals(2, list.size());

        assertEquals(new Integer(2), list.last());

        // Expected structure:
        // (null)
        // (null), (1)
        // (null), (1), (2)
        SkipListNode<Integer> head = list.getHead();
        assertNotNull(head);
        assertNull(head.getData());
        assertNull(head.getNext());
        assertEquals(3, head.getLevel());

        head = head.getDown();
        assertEquals(new Integer(1), head.getNext().getData());
        assertNull(head.getNext().getNext());

        head = head.getDown();
        assertEquals(new Integer(1), head.getNext().getData());
        assertEquals(new Integer(2), head.getNext().getNext().getData());
        assertNull(head.getNext().getNext().getNext());
    }

    @Test(timeout = TIMEOUT)
    public void testClear() {
        list.put(1);
        list.put(2);
        list.put(3);

        list.clear();
        assertEquals(0, list.size());

        SkipListNode<Integer> head = list.getHead();
        assertNull(head.getData());
        assertNull(head.getNext());
        assertEquals(1, head.getLevel());
    }

    @Test(timeout = TIMEOUT)
    public void testDataSet() {
        list.put(1);
        list.put(2);
        list.put(3);
        Set<Integer> actual = list.dataSet();

        Set<Integer> expected = new HashSet<Integer>();
        expected.add(1);
        expected.add(2);
        expected.add(3);

        assertEquals(expected, actual);
    }

    @Test(timeout = TIMEOUT)
    public void testGet() {
        list.put(new Integer(1));
        list.put(new Integer(2));
        list.put(new Integer(3));

        assertEquals(new Integer(1), list.get(1));
        assertEquals(new Integer(2), list.get(2));
        assertEquals(new Integer(3), list.get(3));
    }

    @Test(timeout = TIMEOUT)
    public void testContains() {
        list.put(new Integer(1));
        list.put(new Integer(2));
        list.put(new Integer(3));

        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        assertFalse(list.contains(4));
    }
}
