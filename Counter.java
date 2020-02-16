import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Counter {


    //Let's just count some mutants
    public static void main(String[] args) throws IOException {
        HashMap<String, Set<String>> testToDominatedTests = new HashMap<String, Set<String>>();
        HashMap<String, Set<String>> testToSubsumedMutants = new HashMap<String, Set<String>>();
        HashMap<String, Set<String>> dominatorToSubsumed = new HashMap<String, Set<String>>();


        //TODO pass in killMap.csv
        //TODO enable multiple CSV files

        //adding the mutant records from kill map
        //String killMapPath=args[0];
        String killMapPath="../tailored-mutants-data/results/Lang/3/killmatrix/natural-mutants/non-triggering/";
        Map<String, Set<String>> testToMutant = readKillMap(killMapPath);


        for (String m : testToMutant.keySet()){
            for (String n : testToMutant.keySet()){
                if (testToMutant.get(m).containsAll(testToMutant.get(n))&& m!=n) {
                    System.out.println(m + " contains all of " +n);


                    //map test to dominator subset
                    if (testToDominatedTests.containsKey(m)) {

                        testToDominatedTests.get(m).add(n);
                        testToSubsumedMutants.get(m).addAll(testToMutant.get(n));


                    } else {
                        Set<String> setter= new HashSet<String>();
                        Set<String> setter2= new HashSet<String>();
                        testToDominatedTests.put(m, setter);
                        testToDominatedTests.get(m).add(n);
                        testToSubsumedMutants.put(m, setter2);
                        testToSubsumedMutants.get(m).addAll(testToMutant.get(n));
                    }

                }

            }

        }

        for (String s: testToSubsumedMutants.keySet()){
            System.out.println("for test: "+ s + " mutants are: "+ testToSubsumedMutants.get(s));
        }


        //populate the dominator to subsumed list
        for (String test: testToDominatedTests.keySet()){
//            System.out.println("Trying to add the mutants from test: "+test);

            for (String dominator: testToMutant.get(test)){

//                System.out.println("Trying to add the mutant: "+dominator);

                if (!dominatorToSubsumed.containsKey(dominator)) {
                    Set<String> setter= new HashSet<String>();
                    dominatorToSubsumed.put(dominator, setter);
                    dominatorToSubsumed.get(dominator).addAll(testToSubsumedMutants.get(test));

                } else {
                    dominatorToSubsumed.get(dominator).addAll(testToSubsumedMutants.get(test));
                }

            }


        }


        //check if they are mapped right


        /*for (String m : dominatorToSubsumed.keySet()) {
            if (!dominatorToSubsumed.get(m).equals(Collections.emptyList())) {
                System.out.println("For dominator " + m + " we have subsumed dominators " + dominatorToSubsumed.get(m));
            }
        }*/

        //TODO the generated dominator graph in a CSV
        ArrayList<String> dominatorGraph = new ArrayList<String>();
        //Print all the mutants(including non-dominated ones)
        //sort mutants alphabetically
        List<Integer> sortedMutantListFinal = testToMutant.values().stream()
                .flatMap(valueSet -> valueSet.stream())
                .map(valueAsString -> Integer.parseInt(valueAsString))
                .sorted()
                .distinct()
                .collect(Collectors.toList());

        //print the header
        String firstRow="";
        for (Integer mutantNumber : sortedMutantListFinal) {

            firstRow+=  mutantNumber.toString();
            firstRow+=  ",";
        }


        dominatorGraph.add(firstRow);





        //going through all mutants
        for (Integer counter: sortedMutantListFinal) {
            String row=counter.toString()+",";
            for (Integer innerCounter: sortedMutantListFinal) {
                if (dominatorToSubsumed.containsKey(counter.toString())) {
                    if (dominatorToSubsumed.get(counter.toString()).contains(innerCounter.toString())) {
                        row += innerCounter+",";
                    } else {row+=","; }
                } else {row+=","; }
            }


            dominatorGraph.add(row);


        }

        //Saving the final graph
        Path dominatorFile = Paths.get("graphs/dominator.csv");
        Files.write(dominatorFile, dominatorGraph, StandardCharsets.UTF_8);


    }

    /**
     * Reads a `killMap.csv`-formatted file as a map from tests to mutants killed.
     * @param path The filesystem path to the file to read.
     * @return A mapping from test IDs (as strings) to the set of mutants kill (as strings).
     */
    private static HashMap<String, Set<String>> readKillMap(String path) throws IOException {
        HashMap<String, Set<String>> testToMutant = new HashMap<String, Set<String>>();
        BufferedReader scanner = new BufferedReader(new FileReader(path +"killMap.csv"));

        //parsing a CSV file into Scanner class constructor
        //var sc = new Scanner((scanner));
        //sc.useDelimiter(";");   //sets the delimiter pattern
        String lineTracker=scanner.readLine();
        while ((lineTracker = scanner.readLine()) != null)  //returns a boolean value
        {
            String[] lineKeeper = lineTracker.split(",");
            if (testToMutant.containsKey(lineKeeper[0])) {
                testToMutant.get(lineKeeper[0]).add(lineKeeper[1]);
            } else {
                Set<String> setter= new HashSet<String>();
                testToMutant.put(lineKeeper[0], setter);
                testToMutant.get(lineKeeper[0]).add(lineKeeper[1]);
            }
        }

        scanner.close();
        return testToMutant;
    }
}