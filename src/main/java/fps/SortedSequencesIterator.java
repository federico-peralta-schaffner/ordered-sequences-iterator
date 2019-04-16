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

        Objects.requireNonNull(input, "input cannot be null");

        if (input.isEmpty()) return Collections.emptyIterator();

        List<Iterator<T>> iterators = input.stream()
                .map(it -> Objects.requireNonNull(it, "input sequences cannot be null"))
                .map(Iterable::iterator)
                .filter(Iterator::hasNext)
                .collect(Collectors.toList());

        if (iterators.isEmpty()) return Collections.emptyIterator();

        TreeMap<T, List<Iterator<T>>> nextPerSequence = iterators.stream()
                .collect(Collectors.groupingBy(
                        Iterator::next,
                        TreeMap::new,
                        Collectors.mapping(
                                Function.identity(),
                                Collectors.toCollection(ArrayList::new))));

        class Box {
            Iterator<T> it;
        }
        Box box = new Box();

        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return !nextPerSequence.isEmpty();
            }

            @Override
            public T next() {
                // Get min element
                T min = nextPerSequence.firstKey();

                if (min == null) throw new NoSuchElementException();

                nextPerSequence.computeIfPresent(min, (k, its) -> {
                    box.it = its.remove(0);
                    return its.isEmpty() ? null : its;
                });

                Iterator<T> sequenceIterator = box.it;
                if (sequenceIterator.hasNext()) {
                    nextPerSequence.computeIfAbsent(sequenceIterator.next(), k -> new ArrayList<>())
                            .add(sequenceIterator);
                }

                return min;
            }
        };
    }
}
