package fps;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SortedSequencesIterator {

    public static void main(String[] args) {

        Iterator<Integer> iterator = flatSort(List.of(
                List.of(1, 3, 5, 7, 9),
                List.of(2, 4, 6, 8),
                List.of(0, 10, 20, 30)));

        iterator.forEachRemaining(System.out::println);
    }

    public static <T extends Comparable<? super T>> Iterator<T> flatSort(List<Iterable<T>> input) {

        // Preconditions check
        Objects.requireNonNull(input, "input cannot be null");

        // Border case: empty input
        if (input.isEmpty()) return Collections.emptyIterator();

        // Preparation:
        //   - Blow up if there's some null iterable
        //   - Convert iterables to iterators
        //   - Remove iterators without elements
        List<Iterator<T>> iterators = input.stream()
                .map(it -> Objects.requireNonNull(it, "input sequences cannot be null"))
                .map(Iterable::iterator)
                .filter(Iterator::hasNext)
                .collect(Collectors.toList());

        // Check border case again: empty iterators list
        if (iterators.isEmpty()) return Collections.emptyIterator();

        // Group iterators by their first element
        // (We need a multimap because there can be
        // duplicated elements among different iterators)
        TreeMap<T, List<Iterator<T>>> nextPerSequence = iterators.stream()
                .collect(Collectors.groupingBy(
                        Iterator::next,
                        TreeMap::new,
                        Collectors.mapping(
                                Function.identity(),
                                Collectors.toCollection(ArrayList::new))));

        // Helper Box class
        class Box { Iterator<T> it; }
        Box box = new Box();

        // Iterator to be returned
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return !nextPerSequence.isEmpty();
            }

            @Override
            public T next() {
                // Get min element from iterators map
                T min = nextPerSequence.firstKey();

                if (min == null) throw new NoSuchElementException();

                // Remove min element's entry and keep a reference to its iterator
                nextPerSequence.computeIfPresent(min, (k, its) -> {
                    box.it = its.remove(0);
                    return its.isEmpty() ? null : its;
                });

                Iterator<T> sequenceIterator = box.it;

                // Add next element from min element's iterator
                if (sequenceIterator.hasNext()) {
                    nextPerSequence.computeIfAbsent(sequenceIterator.next(), k -> new ArrayList<>())
                            .add(sequenceIterator);
                }

                return min;
            }
        };
    }
}
