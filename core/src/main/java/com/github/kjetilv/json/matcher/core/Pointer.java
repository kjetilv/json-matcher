package com.github.kjetilv.json.matcher.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public sealed interface Pointer<T> extends Comparable<Pointer<T>> {

    Optional<T> get(T main);

    Object map(Object leaf);

    record Node<T>(String name, Pointer<T> next, Structure<T> structure) implements Pointer<T>, NameChain {

        @Override
        public Optional<T> get(T main) {
            return structure.get(main, name).flatMap(next::get);
        }

        @Override
        public Map<String, ?> map(Object leaf) {
            return Map.of(name, next.map(leaf));
        }

        @Override
        public Stream<String> get() {
            return Stream.concat(
                Stream.of(name()),
                next() instanceof NameChain nameChain ? nameChain.get() : Stream.empty()
            );
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(Pointer<T> pointer) {
            if (pointer instanceof NameChain chain) {
                return path().compareTo(chain.path());
            }
            return 1;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + name + ": " + next + "]";
        }
    }

    record Array<T>(int index, Pointer<T> elem, Structure<T> structure) implements Pointer<T>, NameChain {

        @Override
        public Optional<T> get(T main) {
            return structure.arrayElements(main).skip(index)
                .findFirst().flatMap(elem::get);
        }

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
        public Stream<String> get() {
            Stream<String> stream = IntStream.of(index).mapToObj(Integer::toString);
            return switch (elem) {
                case NameChain nameChain -> Stream.concat(stream, nameChain.get());
                case null, default -> stream;
            };
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(Pointer<T> pointer) {
            return pointer instanceof Pointer.Array<T> array
                ? Integer.compare(index(), array.index())
                : -1;
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

        @SuppressWarnings({"ComparatorMethodParameterNotUsed", "NullableProblems"})
        @Override
        public int compareTo(Pointer<T> pointer) {
            return -1;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[]";
        }
    }
}
