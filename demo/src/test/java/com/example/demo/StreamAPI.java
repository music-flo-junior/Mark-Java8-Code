package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest
class StreamAPI {

    List<String> example = Arrays.asList("123", "456", "789", "012", "345", "678", "1", "2", "3");
    String[] array = {"123", "456", "789", "012", "345", "678", "1", "2", "3"};

    @Test
    void forLoop_vs_Stream() {
        // for loop
        long count = 0;
        List<String> example = Arrays.asList("123", "456", "789", "012", "345", "678", "1", "2", "3");
        for (String word : example) {
            if (word.length() > 2) count++;
        }

        // stream
        count = example.stream().filter(word -> word.length() > 2).count();

        // stream with parallel
        count = example.parallelStream().filter(word -> word.length() > 2).count();
    }

    @Test
    void stream_create() {
        // with array
        Stream<String> words = Stream.of(array);

        // with array elements
        Stream<String> fromToArray = Arrays.stream(array, 0, 1);

        // empty stream
        Stream<String> empty = Stream.empty(); // Stream.<String>empty();

        // infinite stream (generate / no args / Supplier<T>)
        Stream<String> infiniteStreamByGenerate = Stream.generate(() -> "Echo");
        long count = infiniteStreamByGenerate.count(); // 무한 스트림이기 때문에 count 측정 불가능..?

        // infinite stream (iterate / seed value with method / UnaryOperator<T>)
        Stream<BigInteger> infiniteStreamByIterate = Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.ONE));
        count = infiniteStreamByIterate.count();
    }

    @Test
    void filter_map_flatMap() {

        // filter
        Stream<String> filterStream = example.stream().filter(word -> word.length() > 3);
        // Stream<T> filter(Predicate<? super T> predicate);

        // map
        Stream<String> mapStream = example.stream().map(String::toLowerCase);
        // <R> Stream<R> map(Function<? super T, ? extends R> mapper);

        // flatMap
        Stream<Stream<Character>> streamOfStream = example.stream().map(word -> characterStream(word));
        Stream<Character> flatMapStream = example.stream().flatMap(word -> characterStream(word));
    }

    @Test
    void subStream_streamConcat() {
        // get limited subStream from infinite stream
        Stream<Double> limitStream = Stream.generate(Math::random).limit(10);

        // get skipped subStream from infinite stream
        Stream<String> skipStream = Stream.of(array).skip(10);

        // get concat Stream
        Stream<Character> concatStream = Stream.concat(characterStream("Hello"), characterStream("World"));

        // peek
        Stream.iterate(1.0, p -> p * 2)
                .peek(e -> System.out.println(e))
                .limit(10);
    }

    public static Stream<Character> characterStream(String s) {
        List<Character> result = new ArrayList<>();
        for (char c : s.toCharArray()) result.add(c);
        return result.stream();
    }

    @Test
    public void stateful_transformation() {
        Stream<String> statefulStream = Stream.of("merrily", "merrily", "merrily", "gently").distinct(); // "merrily"를 한개만 유지한다.
    }

    @Test
    public void basic_reduction() {
        Optional<String> largest = example.stream().max(String::compareToIgnoreCase);
        if (largest.isPresent()) System.out.println("largest : " + largest.get());

        Optional<String> startsWithQFirst = example.stream().filter(word -> word.startsWith("Q")).findFirst();

        Optional<String> startsWithQAny = example.stream().filter(word -> word.startsWith("Q")).findAny();

        boolean startsWithQisExist = example.stream().anyMatch(word -> word.startsWith("Q"));
    }

    @Test
    public void use_optional_bad_good_example() {
        /* bad example */
        // Optional<T> optionalValue = ...;
        // optionalValue.get().someMethod();

        /* good example */
        // optionalValue.ifPresent(v -> v 처리)

        /* optional default behavior */
        // String result = optionalString.orElse(""); // 감싸고 있는 문자열 또는 문자열이 없는 경우 ""
        // String result = optionalString.orElseGet(() -> System.getProperty(""user.dir")); // 필요할 때만 함수가 호출된다.
        // String result = optionalString.orElseThrow(NoSuchElementException::new); // 예외 객체를 돌려주는 메서드를 제공한다.
    }

    public static Optional<Double> inverse(Double x) {
        return x == 0 ? Optional.empty() : Optional.of(1 / x);
    }

    public static Optional<Double> squareRoot(Double x) {
        return x < 0 ? Optional.empty() : Optional.of(Math.sqrt(x));
    }

    @Test
    public static void optional_of_optional_flatMap() {
        Optional<Double> result = inverse(1.0).flatMap(StreamAPI::squareRoot);
    }

    @Test
    public void reduce() {
        Stream<Integer> values = Stream.of(1, 2, 3, 5, 5);
        Optional<Integer> sum = values.reduce(Integer::sum);
    }

    @Test
    public void stream_to_array() {
        String[] result = example.stream().toArray(String[]::new);
    }

    @Test
    public void stream_collectors() {
        List<String> resultList = example.stream().collect(Collectors.toList());
        /*public static <T>
                Collector<T, ?, List<T>> toList() {
            return new Collectors.CollectorImpl<>((Supplier<List<T>>) ArrayList::new, List::add,
                    (left, right) -> { left.addAll(right); return left; },
                    CH_ID);
        }*/

        Set<String> resultSet = example.stream().collect(Collectors.toSet());
        /*public static <T>
                Collector<T, ?, Set<T>> toSet() {
            return new Collectors.CollectorImpl<>((Supplier<Set<T>>) HashSet::new, Set::add,
                    (left, right) -> { left.addAll(right); return left; },
                    CH_UNORDERED_ID);
        }*/

        String resultString = example.stream().collect(Collectors.joining());
        String resultStringWithDelimiter = example.stream().collect(Collectors.joining(","));

    }

    List<Person> people = Arrays.asList(new Person(1, "p1"), new Person(2, "p2"));

    @Test
    public void stream_toMap() {
        Map<Integer, String> idToName = people.stream().collect(Collectors.toMap(Person::getId, Person::getName));

        Map<Integer, Person> idToPerson = people.stream().collect(Collectors.toMap(Person::getId, Function.identity()));

        Map<Integer, Person> idToPersonWhenDuplicateKey = people.stream().collect(Collectors.toMap(Person::getId, Function.identity(),
                (oldOne, newOne) -> oldOne));
    }

    public class Person {
        int id;
        String name;

        Person(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }
    }

    Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());

    @Test
    public void grouping_partitioning() {
        Map<String, List<Locale>> countryToLocales = locales.collect(
                Collectors.groupingBy(Locale::getCountry)
        );

        List<Locale> swissLocales = countryToLocales.get("CH");

        Map<Boolean, List<Locale>> englishAndOtherLocales = locales.collect(Collectors.partitioningBy(
                l -> l.getLanguage().equals("en")
        ));

        List<Locale> englishLocales = englishAndOtherLocales.get(true);
    }

    @Test
    public void basic_type_stream() {
        IntStream intStream = IntStream.of(1, 2, 3, 4, 5, 6);

        IntStream zeroToNinetyNine = IntStream.range(0, 100); // 상한값 제외
        IntStream zeroToHundred = IntStream.rangeClosed(0, 100); // 상한값 포함

        Stream<Integer> integers = IntStream.range(0, 100).boxed();
    }

    @Test
    public void parallel_stream_unordered() {
        Stream<Locale> sample = locales.parallel().unordered().limit(1);
    }
}
