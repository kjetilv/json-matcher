package com.github.kjetilv.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public sealed interface Pointer<T> extends Comparable<Pointer<T>> {

    @SuppressWarnings({ "ComparatorMethodParameterNotUsed", "NullableProblems" })
    @Override
    default int compareTo(Pointer<T> o) {
        return -1;
    }

    Optional<T> get(T main);

    Object map(Object leaf);

    record Node<T>(String name, Pointer<T> next, Structure<T> structure) implements Pointer<T>, NameChain {

        @Override
        public int compareTo(Pointer<T> pointer) {
            return NameChain.compare(this, pointer);
        }

        @Override
        public Optional<T> get(T main) {
            return structure.get(main, name).flatMap(next::get);
        }

        @Override
        public Map<String, ?> map(Object leaf) {
            return Map.of(name, next.map(leaf));
        }

        @Override
        public Stream<String> stream() {
            return Stream.concat(
                Stream.of(name()),
                next() instanceof NameChain node ? node.stream() : Stream.empty()
            );
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + name + ": " + next + "]";
        }
    }

    record Array<T>(int index, Pointer<T> elem, Structure<T> structure) implements Pointer<T>, NameChain {

        @Override
        public Object map(Object leaf) {
            List<Object> list = new ArrayList<>(index + 1);
            if (index > 0) {
                for (int i = 0; i < index; i++) {
                    list.add(null);
                }
            }
            list.add(elem.map(leaf));
            return list;
        }

        @Override
        public int compareTo(Pointer<T> pointer) {
            if (pointer instanceof Pointer.Array<T> array) {
                return Integer.compare(index(), array.index());
            }
            return -1;
        }

        @Override
        public Optional<T> get(T main) {
            return structure.arrayElements(main)
                .skip(index)
                .findFirst()
                .flatMap(elem::get);
        }

        @Override
        public Stream<String> stream() {
            return Stream.concat(
                IntStream.of(index).mapToObj(Integer::toString),
                elem instanceof NameChain nameChain ? nameChain.stream() : Stream.empty()
            );
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + index + ": " + elem + "]";
        }
    }

    record Leaf<T>() implements Pointer<T> {

        @Override
        public Optional<T> get(T t) {
            return Optional.of(t);
        }

        @Override
        public Object map(Object leaf) {
            return leaf;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[]";
        }
    }
}
