import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Counter {


    //Let's just count some mutants
    public static void main(String[] args) throws IOException {
        HashMap<String, List<String>> testToMutant = new HashMap<String, List<String>>();
        HashMap<String, List<String>> testToDominatedTests = new HashMap<String, List<String>>();
        HashMap<String, List<String>> testToSubsumedMutants = new HashMap<String, List<String>>();
        HashMap<String, List<String>> dominatorToSubsumed = new HashMap<String, List<String>>();
        ArrayList<Integer> sortedMutantList = new ArrayList<Integer>();


        //TODO pass in killMap.csv
        //TODO enable multiple CSV files

        //adding the mutant records from kill map
        //String killMapPath=args[0];
        String killMapPath="../tailored-mutants-data/results/Lang/3/killmatrix/natural-mutants/non-triggering/";
        BufferedReader scanner = new BufferedReader(new FileReader(killMapPath+"killMap.csv"));

        //parsing a CSV file into Scanner class constructor
        //var sc = new Scanner((scanner));
        //sc.useDelimiter(";");   //sets the delimiter pattern
        var index = 0;
        String lineTracker=scanner.readLine();
        while ((lineTracker = scanner.readLine()) != null)  //returns a boolean value
        {


            String[] lineKeeper = lineTracker.split(",");

            sortedMutantList.add(Integer.parseInt(lineKeeper[1]));

            if (testToMutant.containsKey(lineKeeper[0])) {

                testToMutant.get(lineKeeper[0]).add(lineKeeper[1]);


            } else {

                testToMutant.put(lineKeeper[0], new ArrayList<>());
                testToMutant.get(lineKeeper[0]).add(lineKeeper[1]);
            }



        }

        scanner.close();
        //closes the scanner








        for (String m : testToMutant.keySet()){
            for (String n : testToMutant.keySet()){
                if (testToMutant.get(m).containsAll(testToMutant.get(n))&& m!=n) {
                    System.out.println(m + " contains all of " +n);


                    //map test to dominator subset
                    if (testToDominatedTests.containsKey(m)) {

                        testToDominatedTests.get(m).add(n);
                        testToSubsumedMutants.get(m).addAll(testToMutant.get(n));


                    } else {

                        testToDominatedTests.put(m, new ArrayList<>());
                        testToDominatedTests.get(m).add(n);
                        testToSubsumedMutants.put(m, new ArrayList<>());
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

                    dominatorToSubsumed.put(dominator, new ArrayList<String>());
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
        Collections.sort(sortedMutantList);

        //print the header
        String firstRow="";
        for (Integer mutantNumber : sortedMutantList) {
            firstRow+=  ",";
            firstRow+=  mutantNumber.toString();
        }
        firstRow+="\n";

        dominatorGraph.add(firstRow);





        //going through all mutants
        for (Integer counter: sortedMutantList) {
            String row=counter.toString()+",";
            for (Integer innerCounter: sortedMutantList) {
                if (dominatorToSubsumed.containsKey(counter.toString())) {
                    if (dominatorToSubsumed.get(counter.toString()).contains(innerCounter.toString())) {
                        row += innerCounter+",";
                    } else {row+=","; }
                }
            }
            //System.out.print("For mutant "+counter +" the subsumed mutants are ");
//            if (dominatorToSubsumed.containsKey(counter.toString())){
//            //    System.out.println(dominatorToSubsumed.get(counter.toString()));
//            }

            dominatorGraph.add(row);


        }

        //Saving the final graph
        Path dominatorFile = Paths.get("graphs/dominator.csv");
        Files.write(dominatorFile, dominatorGraph, StandardCharsets.UTF_8);


    }
}