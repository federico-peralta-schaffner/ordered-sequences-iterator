package fps;

import java.util.*;
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

        List<Iterable<T>> iterables = input.stream()
                .map(it -> Objects.requireNonNull(it, "input elements cannot be null"))
                .filter(it -> it.iterator().hasNext())
                .collect(Collectors.toList());

        if (iterables.isEmpty()) return Collections.emptyIterator();

        PriorityQueue<T> pq = new PriorityQueue<>(iterables.size());

        iterables.forEach(it -> pq.offer(it.iterator().next()));

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !pq.isEmpty();
            }

            @Override
            public T next() {
                T elem = pq.poll();

                return elem;
            }
        };
    }
}
