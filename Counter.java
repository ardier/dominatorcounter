import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

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

        Map<String, Set<String>> mutantToTest = readKillMap2(killMapPath);
        //Map<String, Set<String>> testToMutant = readKillMap(killMapPath);

        List<String> sortedMutantList = sortMutants(mutantToTest);
        //sort mutants alphabetically
        //List<Integer> sortedMutantListFinal = mutantSort(testToMutant);


        //Map<String, Set<String>> testToSubsumedMutants = testToSubsumed(testToMutant);

        Map<String, Set<String>> domToSub = domToSub(testToMutant);
        //populate the dominator to subsumed list
        //Map<String, Set<String>> dominatorToSubsumed = dominatorMap(testToSubsumedMutants, testToMutant);

        //printing the output
        export(sortedMutantListFinal, dominatorToSubsumed, dominatorFile);

    }

    /**
     * Reads a `killMap.csv`-formatted file as a map from tests to mutants killed.
     *
     * @param path The filesystem path to the file to read.
     * @return A mapping from test IDs (as strings) to the set of mutants kill (as strings).
     * @requires passed in map and it's member set should not be empty
     */
    private static HashMap<String, Set<String>> readKillMap(String path) throws IOException {
        HashMap<String, Set<String>> testToMutant = new HashMap<String, Set<String>>();
        BufferedReader scanner = new BufferedReader(new FileReader(path));

        //parsing a CSV file into Scanner class constructor
        //var sc = new Scanner((scanner));
        //sc.useDelimiter(";");   //sets the delimiter pattern
        String lineTracker = scanner.readLine();
        while ((lineTracker = scanner.readLine()) != null)  //returns a boolean value
        {
            String[] lineKeeper = lineTracker.split(",");
            if (testToMutant.containsKey(lineKeeper[0])) {
                testToMutant.get(lineKeeper[0]).add(lineKeeper[1]);
            } else {
                Set<String> setter = new HashSet<String>();
                testToMutant.put(lineKeeper[0], setter);
                testToMutant.get(lineKeeper[0]).add(lineKeeper[1]);
            }
        }

        scanner.close();
        return testToMutant;
    }

    /**
     * Reads a `killMap.csv`-formatted file as a map from tests to mutants killed.
     *
     * @param path The filesystem path to the file to read.
     * @return A mapping from test IDs (as strings) to the set of mutants kill (as strings).
     * @requires passed in map and it's member set should not be empty
     */
    private static HashMap<String, Set<String>> readKillMap2(String path) throws IOException {
        HashMap<String, Set<String>> mutantToTest = new HashMap<String, Set<String>>();
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
                Set<String> setter = new HashSet<String>();
                mutantToTest.put(lineKeeper[1], setter);
                mutantToTest.get(lineKeeper[1]).add(lineKeeper[0]);
            }
        }

        scanner.close();
        return mutantToTest;
    }

    /**
     * Reads a mapping of tests to mutants killed
     *
     * @param testToMutant A map from tests to mutants killed
     * @requires passed in map and it's member set should not be empty
     * @returns a mapping from test IDs (as Strings) to the set of mutants subsumed (as strings).
     */
    private static HashMap<String, Set<String>> testToSubsumed(Map<String, Set<String>> testToMutant) {
        HashMap<String, Set<String>> testToSubsumedMutantsMethod = new HashMap<String, Set<String>>();

        for (String m : testToMutant.keySet()) {
            for (String n : testToMutant.keySet()) {
                if (testToMutant.get(m).containsAll(testToMutant.get(n)) && m != n) {
                    System.out.println(m + " contains all of " + n);

                    //map test to dominator subset
                    if (testToSubsumedMutantsMethod.containsKey(m)) {

                        testToSubsumedMutantsMethod.get(m).addAll(testToMutant.get(n));


                    } else {
                        Set<String> setter = new HashSet<String>();
                        Set<String> setter2 = new HashSet<String>();
                        testToSubsumedMutantsMethod.put(m, setter2);
                        testToSubsumedMutantsMethod.get(m).addAll(testToMutant.get(n));
                    }

                }

            }

        }

        return testToSubsumedMutantsMethod;
    }

    private static HashMap<String, Set<String>> domToSub(Map<String, Set<String>> mutatnToTestMethod) {
        HashMap<String, Set<String>> domToSub = new HashMap<String, Set<String>>();
        for (String m : mutatnToTestMethod.keySet()) {
            for (String n : mutatnToTestMethod.keySet()) {
                if (mutatnToTestMethod.get(m).containsAll(mutatnToTestMethod.get(n))) {
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
     * Reads a mapping of tests to subsumed mutants and a mapping of tests to all mutants
     * to the set of mutants subsumed (as strings).
     *
     * @param testToSubsumedMutantsMethod A map from tests to subsumed Mutants
     * @param testToMutantMethod          A map from tests to mutants killed
     * @returns a mapping from dominator mutant IDs (as Strings)
     * @requires passed in maps and their member sets should not be empty
     */
    private static HashMap<String,
            Set<String>> dominatorMap(Map<String, Set<String>> testToSubsumedMutantsMethod,
                                      Map<String, Set<String>> testToMutantMethod) {
        HashMap<String, Set<String>> dominatorToSubsumedMethod = new HashMap<String, Set<String>>();

        for (String test : testToSubsumedMutantsMethod.keySet()) {
            //System.out.println("Trying to add the mutants from test: "+test);

            for (String dominator : testToMutantMethod.get(test)) {

                //System.out.println("Trying to add the mutant: "+dominator);

                if (!dominatorToSubsumedMethod.containsKey(dominator)) {
                    Set<String> setter = new HashSet<String>();
                    dominatorToSubsumedMethod.put(dominator, setter);
                    dominatorToSubsumedMethod.get(dominator)
                            .addAll(testToSubsumedMutantsMethod.get(test));

                } else {
                    dominatorToSubsumedMethod.get(dominator)
                            .addAll(testToSubsumedMutantsMethod.get(test));
                }

            }


        }
        return dominatorToSubsumedMethod;
    }

    /**
     * Reads a mapping of tests to all mutants
     *
     * @param testToMutantMethod A map from tests to mutants killed
     * @requires passed in map and its member set should not be empty
     * to the set of mutants subsumed (as strings).
     * @returns a list of all mutant IDs (as Integers) sorted in order
     */
    private static List<Integer> mutantSort(Map<String, Set<String>> testToMutantMethod) {
        List<Integer> sortedMutantListFinalMethod = testToMutantMethod.values().stream()
                .flatMap(valueSet -> valueSet.stream())
                .map(valueAsString -> Integer.parseInt(valueAsString))
                .sorted()
                .distinct()
                .collect(Collectors.toList());
        return sortedMutantListFinalMethod;
    }

    /**
     * Reads a mapping of tests to all mutants
     *
     * @param testToMutantMethod A map from tests to mutants killed
     * @requires passed in map and its member set should not be empty
     * to the set of mutants subsumed (as strings).
     * @returns a list of all mutant IDs (as Integers) sorted in order
     */
    private static List<String> sortMutants(Map<String, Set<String>> mutantToTest) {
        List<String> sortedMutantList = new ArrayList<>(mutantToTest.keySet());
        sortedMutantList.sort(Comparator.naturalOrder());
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
     * @requires passed in map and its member set should not be empty
     * to the set of mutants subsumed (as strings).
     */
    private static void export(List<Integer> sortedMutantListFinal,
                               Map<String, Set<String>> dominatorToSubsumedMethod,
                               Path[] dominatorFile) throws IOException {

        ArrayList<String> dominatorGraph = new ArrayList<String>();

        //print the header
        String firstRow = ",";
        for (Integer mutantNumber : sortedMutantListFinal) {

            firstRow += mutantNumber.toString();
            firstRow += ",";
        }


        dominatorGraph.add(firstRow);


        //going through all mutants
        for (Integer counter : sortedMutantListFinal) {
            String row = counter.toString() + ",";
            for (Integer innerCounter : sortedMutantListFinal) {
                if (dominatorToSubsumedMethod.containsKey(counter.toString())) {
                    if (dominatorToSubsumedMethod.get(counter.toString()).contains(innerCounter.toString())) {
                        row += innerCounter + ",";
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