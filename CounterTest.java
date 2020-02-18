import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;


public class CounterTest {


    /**
     * A randomly generated stress test to see if the program runs on random input
     *
     * @throws IOException when it is not able to read or save the file
     */

    @Test
    public void test0() throws IOException {

        int[] randomTests = new Random().ints(7000, 1, 50).toArray();
        int[] randomMutants = new Random().ints(7000, 1, 50).toArray();

        List<String> test0 = new ArrayList<>(List.of("Test No,Mutant No"));
        test0.addAll(IntStream.range(0, randomTests.length)
                .mapToObj(i -> randomTests[i] + "," + randomMutants[i])
                .collect(Collectors.toList()));

        //Saving the final graph
        Files.write(Paths.get("test-data/test0.csv"), test0, StandardCharsets.UTF_8);
        String[] test00 = {"test-data/test0.csv", "Test", "0", "trigger.1"};
        Counter.main(test00);

    }

    /**
     * A simple test to see whether the program generates the correct CSV output
     *
     * @throws IOException when it is not able to read or write the file
     */

    @Test
    public void test1() throws IOException {
        String[] test2 = {"test-data/killMap1.csv", "Test", "1", "trigger.1"};
        Counter.main(test2);

        var path1 = Paths.get("graphs/Test-1-trigger.1.csv");
        var path2 = Paths.get("graphs/Test-1-trigger.1-compare.csv");

        BufferedReader scanner = new BufferedReader(new FileReader(String.valueOf(path1)));
        BufferedReader scanner2 = new BufferedReader(new FileReader(String.valueOf(path2)));

        var lineTracker = "";
        var lineTracker2 = "";
        while (((lineTracker = scanner.readLine()) != null) && (lineTracker2 = scanner2.readLine()) != null) {

            if (!lineTracker.equals(lineTracker2)) {
                assertEquals(lineTracker2, lineTracker);
            }

        }

        scanner.close();
        scanner2.close();

    }

    /**
     * A simple test to see whether the program would handle incorrect input as intended
     *
     * @throws InvalidParameterException on purpose when the output is wrong
     */

    @Test(expected = InvalidParameterException.class)
    public void test2InvalidInput() throws IOException {
        String[] test2 = {"test-data/killMap1.csv", "Test", "1"};
        Counter.main(test2);

    }


}