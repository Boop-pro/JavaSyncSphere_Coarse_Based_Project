package repository;

/**
 * Generic repository implementation backed by an internal array.
 * Demonstrates Unit II concepts:
 * - Generic class (Repository<T>)
 * - Internal Object[] array storage and dynamic resizing
 * - Type casting ((T) elements[i])
 * - Generic methods (copy, toArray)
 *
 * @param <T> Type of entity stored in the repository
 */
public class Repository<T> {

    private static final int INITIAL_CAPACITY = 10;
    private Object[] elements;
    private int size;

    /**
     * Initializes repository with default array capacity.
     */
    public Repository() {
        this.elements = new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Adds an item to the repository, resizing the array if necessary.
     */
    public void add(T item) {
        if (item == null) {
            return;
        }
        ensureCapacity();
        elements[size++] = item;
    }

    /**
     * Retrieves an item at a specific index.
     * Demonstrates explicit type casting from Object to generic parameter T.
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for repository size " + size);
        }
        return (T) elements[index];
    }

    /**
     * Removes a specific item using its equals() method.
     */
    public boolean remove(T item) {
        if (item == null) return false;
        for (int i = 0; i < size; i++) {
            if (item.equals(elements[i])) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the item at index and shifts subsequent elements left.
     */
    @SuppressWarnings("unchecked")
    public T removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        T removed = (T) elements[index];
        int shiftCount = size - index - 1;
        if (shiftCount > 0) {
            System.arraycopy(elements, index + 1, elements, index, shiftCount);
        }
        elements[--size] = null; // Prevent memory leak
        return removed;
    }

    /**
     * Returns the number of items stored.
     */
    public int size() {
        return size;
    }

    /**
     * Checks if repository is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears all stored items.
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    /**
     * Ensures capacity by doubling array length when full.
     */
    private void ensureCapacity() {
        if (size >= elements.length) {
            int newCapacity = elements.length * 2;
            Object[] newArray = new Object[newCapacity];
            System.arraycopy(elements, 0, newArray, 0, size);
            elements = newArray;
        }
    }

    /**
     * Populates and returns an array of type E[].
     * Demonstrates a generic method within a generic class.
     */
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        if (a.length < size) {
            return (E[]) java.util.Arrays.copyOf(elements, size, a.getClass());
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    /**
     * Generic utility method to copy elements between repositories.
     * Demonstrates static generic method (Unit II).
     */
    public static <E> void copy(Repository<E> source, Repository<E> destination) {
        if (source == null || destination == null) return;
        for (int i = 0; i < source.size(); i++) {
            destination.add(source.get(i));
        }
    }
}
