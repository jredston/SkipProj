import java.util.HashSet;
import java.util.Set;

/**
 * Your implementation of a skip list.
 * 
 * @author jredston3
 * @version 1.0
 */
public class SkipList<T extends Comparable<? super T>> implements
        SkipListInterface<T> {
    // Do not add any additional instance variables
    private CoinFlipper coinFlipper;
    private int size;
    private SkipListNode<T> head;

    /**
     * Constructs a SkipList object that stores data in ascending order. When an
     * item is inserted, the flipper is called until it returns a tails. If, for
     * an item, the flipper returns n heads, the corresponding node has n + 1
     * levels.
     *
     * @param coinFlipper
     *            the source of randomness
     */
    public SkipList(CoinFlipper coinFlipper) {
        head = new SkipListNode<T>(null, 1);
        this.coinFlipper = coinFlipper;
        size = 0;
    }

    @Override
    public T first() {
        SkipListNode<T> current = head;
        while (current.getDown() != null) {
            current = current.getDown();
        }
        if (current.getNext() == null) {
            throw new java.util.NoSuchElementException("SkipList is empty");
        }
        return current.getNext().getData();
    }

    @Override
    public T last() {
        SkipListNode<T> current = head.getDown();

        if (current == null || current.getNext() == null) {
            throw new java.util.NoSuchElementException("SkipList is empty");
        }

        while (current.getNext() != null || current.getDown() != null) {
            if (current.getNext() != null) {
                current = current.getNext();
            } else {
                current = current.getDown();
            }

        }
        return current.getData();

    }

    @Override
    public void put(T data) {

        if (data == null) {
            throw new IllegalArgumentException("Data entered is null");
        }
        size++;
        int numflips = coinFlipper.getNumFlips();
        SkipListNode<T> current = head;
        SkipListNode<T> aboveNode = null;
        CoinFlipper.Coin flipvalue = coinFlipper.flipCoin();

        while (flipvalue != CoinFlipper.Coin.TAILS) {

            flipvalue = coinFlipper.flipCoin();
            // coinFlipper.flipCoin();

        }

        numflips = coinFlipper.getNumFlips() - numflips;

        while (current.getLevel() < numflips + 1) {

            current = new SkipListNode<T>(null, current.getLevel() + 1, null,
                    null, null, current);
            current.getDown().setUp(current);
        }
        head = current;
        while (current.getLevel() > numflips) {
            current = current.getDown();
        }
        for (int i = numflips; i > 0; i--) {
            while (current.getNext() != null
                    && current.getNext().getData().compareTo(data) < 0) {
                current = current.getNext();
            }
            current.setNext(new SkipListNode<T>(data, i, current, current
                    .getNext(), aboveNode, null));
            if (aboveNode != null) {
                aboveNode.setDown(current.getNext());
            }
            if (current.getNext().getNext() != null) {
                current.getNext().getNext().setPrev(current.getNext());
            }
            aboveNode = current.getNext();
            current = current.getDown();
        }

    }

    @Override
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data entered is null");
        }
        T returnData = null;
        SkipListNode<T> current = head;
        while (current != null) {
            // SkipListNode<T> frontNode = current;
            while (current.getNext() != null
                    && current.getNext().getData().compareTo(data) < 0) {
                current = current.getNext();
            }

            if (current.getNext() != null
                    && current.getNext().getData().equals(data)) {

                returnData = current.getNext().getData();
                current.setNext(current.getNext().getNext());
                // current.getNext().setNext(current);
                if (current.getNext() != null) {
                    current.getNext().setPrev(current);
                }

                if (current.getPrev() == null && current.getNext() == null) {
                    if (current.getLevel() == 1) {

                        this.clear();
                        size++;
                    }
                    if (current.getUp() != null) {
                        current.getUp().setDown(current.getDown());
                        SkipListNode<T> forward = null;
                        while (current.getUp() != null) {
                            forward = current;
                            while (forward != null) {
                                forward.setLevel(forward.getLevel() - 1);
                                forward = forward.getNext();
                            }
                            current = current.getUp();
                        }
                    } else if (current.getDown() != null) {
                        head = current.getDown();
                    }
                }
            }
            current = current.getDown();
        }
        if (returnData == null) {
            throw new java.util.NoSuchElementException(
                    "Element not in SkipList");
        }
        size--;
        return returnData;

    }

    @Override
    public boolean contains(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data entered is null");
        }
        SkipListNode<T> current = head;
        while (current != null) {
            while (current.getNext() != null
                    && current.getNext().getData().compareTo(data) < 0) {
                current = current.getNext();
            }
            if (current.getNext() != null
                    && current.getNext().getData().equals(data)) {
                return true;
            }
            current = current.getDown();
        }
        return false;

    }

    @Override
    public T get(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Data entered is null");
        }
        SkipListNode<T> current = head;
        while (current != null) {
            while (current.getNext() != null
                    && current.getNext().getData().compareTo(data) < 0) {
                current = current.getNext();
            }
            if (current.getNext() != null
                    && current.getNext().getData().equals(data)) {
                return current.getNext().getData();
            }
            current = current.getDown();
        }
        throw new java.util.NoSuchElementException("Data not in SkipList");

    }

    @Override
    public int size() {
        return size;

    }

    @Override
    public void clear() {
        head = new SkipListNode<T>(null, 1);
        size = 0;
    }

    @Override
    public Set<T> dataSet() {
        SkipListNode<T> current = head;
        Set<T> set = new HashSet<T>();
        while (current.getDown() != null) {
            current = current.getDown();
        }
        while (current.getNext() != null) {
            set.add(current.getNext().getData());
            current = current.getNext();
        }
        return set;

    }

    @Override
    public SkipListNode<T> getHead() {
        return head;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("**********************\n");
        builder.append(String.format("SkipList (size = %d)\n", size()));
        SkipListNode<T> levelCurr = getHead();

        while (levelCurr != null) {
            SkipListNode<T> curr = levelCurr;
            int level = levelCurr.getLevel();
            builder.append(String.format("Level: %2d   ", level));

            while (curr != null) {
                builder.append(String.format("(%s)%s", curr.getData(),
                        curr.getNext() == null ? "\n" : ", "));
                curr = curr.getNext();
            }
            levelCurr = levelCurr.getDown();
        }
        builder.append("**********************\n");
        return builder.toString();
    }


}
