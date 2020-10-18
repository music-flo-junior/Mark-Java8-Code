package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 설명 :
 *
 * @author 이민호(Mark) / minholee93@sk.com
 * 2020/10/18
 * 9:31 오후
 */
public class Concurrent {

    public static AtomicLong largest = new AtomicLong();

    @Test
    public void atomic() {
        largest.set(Math.max(largest.get(), 100L)); // not valid

        Long oldValue = largest.get();
        Long newValue = Math.max(oldValue, 100L);
        while (!largest.compareAndSet(oldValue, newValue)) ; // thread-safe concurrent

        largest.updateAndGet(x -> Math.max(x, 100L));
        largest.accumulateAndGet(100L, Math::max);

        final LongAdder adder = new LongAdder();
        for (; ; ) {
            adder.increment();
            System.out.println("Add : " + adder.sum());
        }
    }

    @Test
    public void concurrentMap() {
        ConcurrentHashMap<String, LongAdder> map = new ConcurrentHashMap<>();
        map.putIfAbsent("test", new LongAdder());
        map.get("test").increment(); // thread-safe set value to map

        ConcurrentHashMap<String, Long> mapLong = new ConcurrentHashMap<>();
        mapLong.compute("test", (k, v) -> v == null ? 1 : v + 1);

        ConcurrentHashMap<String, LongAdder> mapComputeIfAbsent = new ConcurrentHashMap<>();
        mapComputeIfAbsent.computeIfAbsent("test", k -> new LongAdder()).increment();

        ConcurrentHashMap<String, Long> mapMerge = new ConcurrentHashMap<>();
        mapMerge.merge("test", 1L, (oldValue, newValue) -> oldValue + newValue);
    }

    @Test
    public void parallel_search() {

        ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.putIfAbsent("test", 1L);
        }

        // MAX threshold == 1 thread
        String result = map.search(Long.MAX_VALUE, (k, v) -> v > 1000 ? k : null);

        map.forEach(Long.MAX_VALUE, (k, v) -> System.out.println(k + " -> " + v));
        map.forEach(Long.MAX_VALUE, (k, v) -> k + " -> " + v, System.out::println); // transform

        Long sum = map.reduceValues(Long.MAX_VALUE, Long::sum);
        Integer maxLenght = map.reduceKeys(Long.MAX_VALUE, String::length, Integer::max); // transform key
        Long count = map.reduceValues(Long.MAX_VALUE, v -> v > 1000 ? 1L : null, Long::sum);
        sum = map.reduceValuesToLong(Long.MAX_VALUE, Long::longValue, 0, Long::sum); // with default value

        Set<String> words = map.keySet(1L);
        words.add("Java");
    }

    @Test
    public void parallel_sort() {
        Integer[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Arrays.parallelSetAll(arr, i -> i % 10);
        Arrays.parallelPrefix(arr, (x, y) -> x * y);
    }

    @Test
    public void computableFuture() {
        CompletableFuture<String> contents = readPage("test");
        contents.thenApply(this::getLinks);

        contents = CompletableFuture.supplyAsync(() -> blockingReadPage("//"));

        CompletableFuture<List<String>> links =
                CompletableFuture.supplyAsync(() -> blockingReadPage("//"))
                    .thenApply(this::getLinks);

        CompletableFuture<Void> voidResult =
                CompletableFuture.supplyAsync(() -> blockingReadPage("//"))
                        .thenApply(this::getLinks)
                        .thenAccept(System.out::println);
    }

    private List<String> getLinks(String page){
        return Arrays.asList(page);
    }

    private CompletableFuture<String> readPage(String url) {
        return CompletableFuture.completedFuture(url);
    }

    private String blockingReadPage(String url){
        return url;
    }
}
