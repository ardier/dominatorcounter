import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Counter maps dominator mutants to subsumed mutants exports the outcome into a CSV file
 */
public class Counter {

    //Let's just count some mutants
    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            throw new InvalidParameterException("Usage: javac Counter killMapPath project version triggerSettings");
        }


        //adding the mutant records from kill map
        var killMapPath = args[0];
        //Project/Version/Trigger/
        var dominatorFile = Paths.get("graphs", args[1] + "-" + args[2] + "-" + args[3] + ".csv");

        Map<Integer, Set<Integer>> mutantToTest = readKillMap(killMapPath);

        //sort mutants alphabetically
        List<Integer> sortedMutantList = sortMutants(mutantToTest);


        //map dominator mutants to subsumed mutants
        Map<Integer, Set<Integer>> domToSub = domToSub(mutantToTest);

        //printing the output
        export(sortedMutantList, domToSub, dominatorFile);

    }


    /**
     * Reads a `killMap.csv`-formatted file as a map from tests to mutants killed.
     *
     * @param path The filesystem path to the file to read.
     * @return A mapping from test IDs (as integers) to the set of mutants kill (as integers).
     */
    private static HashMap<Integer, Set<Integer>> readKillMap(String path) throws IOException {
        HashMap<Integer, Set<Integer>> mutantToTest = new HashMap<>();
        var scanner = new BufferedReader(new FileReader(path));

        //parsing a CSV file into Scanner class constructor
        //var sc = new Scanner((scanner));
        //sc.useDelimiter(";");   //sets the delimiter pattern
        var lineTracker = scanner.readLine();
        while ((lineTracker = scanner.readLine()) != null)  //returns a boolean value
        {
            String[] lineKeeper = lineTracker.split(",");
            int test = Integer.parseInt(lineKeeper[0]);
            int mutant = Integer.parseInt(lineKeeper[1]);
            if (mutantToTest.containsKey(mutant)) {
                mutantToTest.get(mutant).add(test);
            } else {
                Set<Integer> setter = new HashSet<>();
                mutantToTest.put(Integer.parseInt(lineKeeper[1]), setter);
                mutantToTest.get(mutant).add(test);
            }
        }

        scanner.close();
        return mutantToTest;
    }

    /**
     * Reads a mapping of mutants to tests failed for those mutants
     *
     * @param mutantToTestMethod A map from mutants to tests failed
     * @return a mapping from dominator mutant IDs (as integers) that make a larger set of tests fail to the set
     * of mutants (as integers) also known as subsumed mutants that fail a subset of the same tests
     */


    private static HashMap<Integer, Set<Integer>> domToSub(Map<Integer, Set<Integer>> mutantToTestMethod) {
        HashMap<Integer, Set<Integer>> domToSub = new HashMap<>();
        for (Integer m : mutantToTestMethod.keySet()) {
            for (Integer n : mutantToTestMethod.keySet()) {
                if (mutantToTestMethod.get(m).containsAll(mutantToTestMethod.get(n))) {
                    if (!domToSub.containsKey(m)) {
                        domToSub.put(m, new HashSet<>());
                    }
                    domToSub.get(m).add(n);
                }
            }
        }
        return domToSub;
    }


    /**
     * Reads a mapping of mutants to tests failed
     *
     * @param mutantToTest A map from mutants to the tests failed for those mutants
     * @return a list of all mutant IDs (as integers) sorted in numeric order
     */
    private static List<Integer> sortMutants(Map<Integer, Set<Integer>> mutantToTest) {
        //List<String> sortedMutantList = new ArrayList<>(mutantToTest.keySet());
        //sortedMutantList.sort(Comparator.naturalOrder());
        List<Integer> sortedMutantList = mutantToTest.keySet().stream()
                .sorted()
                .distinct()
                .collect(Collectors.toList());
        return sortedMutantList;


    }


    /**
     * Reads a list of all mutant IDs (as integers) sorted in order
     * and a mapping of all dominator mutants (as integers)
     * to their subsumed mutants (as strings).
     * Writes csv file
     *
     * @param sortedMutantListFinal     a list of all mutant IDs (as integers) sorted in order
     * @param dominatorToSubsumedMethod a mapping from dominator mutant IDs (as integers)
     *                                  that make a larger set of tests fail to the set
     *                                  of mutants (as integers) also known as subsumed mutants that fail
     *                                  a subset of the same tests
     * @param dominatorFile             a path for the generated csv file based on project name, version, and whether
     *                                  the test is triggering or non-triggering
     *
     */
    private static void export(List<Integer> sortedMutantListFinal,
                               Map<Integer, Set<Integer>> dominatorToSubsumedMethod,
                               Path dominatorFile) throws IOException {

        ArrayList<String> dominatorGraph = new ArrayList<>();

        var firstRow = sortedMutantListFinal.stream()
                .map(i -> i.toString())
                .collect(Collectors.joining(","));
        dominatorGraph.add("," + firstRow + ",");


        //going through all mutants
        for (var counter : sortedMutantListFinal) {
            var rowBuilder = new StringBuilder(counter + ",");

            for (var innerCounter : sortedMutantListFinal) {
                if (dominatorToSubsumedMethod.containsKey(counter)) {
                    if (dominatorToSubsumedMethod.get(counter).contains(innerCounter)) {
                        rowBuilder.append(innerCounter + ",");
                    } else {
                        rowBuilder.append(",");
                    }
                } else {
                    rowBuilder.append(",");
                }
            }

            dominatorGraph.add(rowBuilder.toString());

        }


        //Saving the final graph
        Files.write(dominatorFile, dominatorGraph, StandardCharsets.UTF_8);

    }

}