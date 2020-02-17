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
        String killMapPath = args[0];
        //Project/Version/Trigger/
        Path[] dominatorFile = {Paths.get(args[1]), Paths.get(args[2]), Paths.get(args[3])};

        Map<String, Set<String>> mutantToTest = readKillMap(killMapPath);

        //sort mutants alphabetically
        List<Integer> sortedMutantList = sortMutants(mutantToTest);


        //map dominator mutants to subsumed mutants
        Map<String, Set<String>> domToSub = domToSub(mutantToTest);

        //printing the output
        export(sortedMutantList, domToSub, dominatorFile);

    }


    /**
     * Reads a `killMap.csv`-formatted file as a map from tests to mutants killed.
     *
     * @param path The filesystem path to the file to read.
     * @return A mapping from test IDs (as strings) to the set of mutants kill (as strings).
     */
    private static HashMap<String, Set<String>> readKillMap(String path) throws IOException {
        HashMap<String, Set<String>> mutantToTest = new HashMap<>();
        BufferedReader scanner = new BufferedReader(new FileReader(path));

        //parsing a CSV file into Scanner class constructor
        //var sc = new Scanner((scanner));
        //sc.useDelimiter(";");   //sets the delimiter pattern
        String lineTracker = scanner.readLine();
        while ((lineTracker = scanner.readLine()) != null)  //returns a boolean value
        {
            String[] lineKeeper = lineTracker.split(",");
            if (mutantToTest.containsKey(lineKeeper[1])) {
                mutantToTest.get(lineKeeper[1]).add(lineKeeper[0]);
            } else {
                Set<String> setter = new HashSet<>();
                mutantToTest.put(lineKeeper[1], setter);
                mutantToTest.get(lineKeeper[1]).add(lineKeeper[0]);
            }
        }

        scanner.close();
        return mutantToTest;
    }

    /**
     * Reads a mapping of mutants to tests failed for those mutants
     *
     * @param mutantToTestMethod A map from mutants to tests failed
     * @return a mapping from dominator mutant IDs (as Strings) to the set of mutants subsumed (as strings).
     */


    private static HashMap<String, Set<String>> domToSub(Map<String, Set<String>> mutantToTestMethod) {
        HashMap<String, Set<String>> domToSub = new HashMap<>();
        for (String m : mutantToTestMethod.keySet()) {
            for (String n : mutantToTestMethod.keySet()) {
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
     * @param mutantToTest A map from mutants to tests failed
     *                     to the set of mutants subsumed (as strings).
     * @return a list of all mutant IDs (as Integers) sorted in order
     */
    private static List<Integer> sortMutants(Map<String, Set<String>> mutantToTest) {
        //List<String> sortedMutantList = new ArrayList<>(mutantToTest.keySet());
        //sortedMutantList.sort(Comparator.naturalOrder());
        List<Integer> sortedMutantList = mutantToTest.keySet().stream()
                .map(valueAsString -> Integer.parseInt(valueAsString))
                .sorted()
                .distinct()
                .collect(Collectors.toList());
        return sortedMutantList;


    }


    /**
     * Reads a list of all mutant IDs (as Integers) sorted in order
     * and a mapping of all dominator mutants (as strings)
     * to their subsumed mutants (as strings).
     * Writes csv file
     *
     * @param sortedMutantListFinal     a list of all mutant IDs (as Integers) sorted in order
     * @param dominatorToSubsumedMethod a mapping of dominator mutants (as Strings) to
     *                                  a list of their subsumed mutants (as Strings)
     * @param dominatorFile             an array of project information used to name the exported dominator
     *                                  graph
     */
    private static void export(List<Integer> sortedMutantListFinal,
                               Map<String, Set<String>> dominatorToSubsumedMethod,
                               Path[] dominatorFile) throws IOException {

        ArrayList<String> dominatorGraph = new ArrayList<>();

        //print the header
        String firstRow = ",";
        for (Integer mutantNumber : sortedMutantListFinal) {

            firstRow += mutantNumber.toString();
            firstRow += ",";
        }


        dominatorGraph.add(firstRow);


        //going through all mutants
        for (Integer counter : sortedMutantListFinal) {
            String row = counter + ",";
            for (Integer innerCounter : sortedMutantListFinal) {
                if (dominatorToSubsumedMethod.containsKey(counter.toString())) {
                    if (dominatorToSubsumedMethod.get(counter.toString()).contains(innerCounter.toString())) {
                        row += innerCounter.toString() + ",";
                    } else {
                        row += ",";
                    }
                } else {
                    row += ",";
                }
            }

            dominatorGraph.add(row);

        }
        String fileAddress = "graphs/" + dominatorFile[0] + "-" + dominatorFile[1] + "-" + dominatorFile[2] + ".csv";

        //Saving the final graph
        Files.write(Paths.get(fileAddress), dominatorGraph, StandardCharsets.UTF_8);

    }

}