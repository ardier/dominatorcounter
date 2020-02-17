import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.*;


public class CounterTest {


    @Test
    public void test0() throws IOException {
        //randomly generated stress test to see if the program runs on random input

        int[] randomTests = new int[7000];
        for (int i = 0; i < randomTests.length; i++) {
            randomTests[i] = (int) (Math.random() * 50)+1;
        }
        int[] randomMutants = new int[7000];
        for (int i = 0; i < randomMutants.length; i++) {
            randomMutants[i] = (int) (Math.random() * 50)+1;
        }

        List<String> test0 = new ArrayList<String>(List.of("Test No,Mutant No"));
        test0.addAll(IntStream.range(0, randomTests.length)
                .mapToObj(i -> new String(randomTests[i]+","+ randomMutants[i]))
                .collect(Collectors.toList()));

        //Saving the final graph
        Files.write(Paths.get("test-data/test0.csv"), test0, StandardCharsets.UTF_8);
        String[] test00 = {"test-data/test0.csv", "Test", "0", "trigger.1"};
        Counter.main(test00);

    }

    @Test
    public void test1() throws IOException {
        String[] test2 = {"test-data/killMap1.csv", "Test", "1", "trigger.1"};
        Counter.main(test2);

        Path path1 = Paths.get("graphs/Test-1-trigger.1.csv");
        Path path2 = Paths.get("graphs/Test-1-trigger.1-compare.csv");

        BufferedReader scanner = new BufferedReader(new FileReader(String.valueOf(path1)));
        BufferedReader scanner2 = new BufferedReader(new FileReader(String.valueOf(path2)));

        String lineTracker = "";
        String lineTracker2 = "";
        while (((lineTracker = scanner.readLine()) != null) && (lineTracker2 = scanner2.readLine()) != null)  //returns a boolean value
        {

            if (lineTracker != lineTracker2) {
                assertEquals(lineTracker2, lineTracker);
            }

        }

        scanner.close();
        scanner2.close();

    }


    @Test(expected = InvalidParameterException.class)
    public void invalidInput() throws IOException {
        String[] test2 = {"test-data/killMap1.csv", "Test", "1"};
        Counter.main(test2);

    }


}