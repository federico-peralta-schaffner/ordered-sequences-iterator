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

        // Helper PairBox class that links each iterator to its next element
        class PairBox {
            final T elem;
            final Iterator<T> it;

            PairBox(T elem, Iterator<T> it) {
                this.elem = elem;
                this.it = it;
            }
        }

        // Insert each iterator along with its first element into a
        // priority queue that sorts elements by each iterator's next element
        PriorityQueue<PairBox> nextPerSequence = new PriorityQueue<>(Comparator.comparing(p -> p.elem));
        iterators.forEach(it -> nextPerSequence.add(new PairBox(it.next(), it)));

        // Iterator to be returned
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return !nextPerSequence.isEmpty();
            }

            @Override
            public T next() {
                // Remove min element from priority queue
                PairBox min = nextPerSequence.poll();

                if (min == null) throw new NoSuchElementException();

                // Add next element from min element's iterator to priority queue
                if (min.it.hasNext()) {
                    nextPerSequence.add(new PairBox(min.it.next(), min.it));
                }

                return min.elem;
            }
        };
    }
}
