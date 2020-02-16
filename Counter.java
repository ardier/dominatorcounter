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
        HashMap<String, List<String>> testToDominated = new HashMap<String, List<String>>();
        ArrayList<Integer> sortedMutantList = new ArrayList<Integer>();


        //TODO pass in killMap.csv

        //adding the mutant records from kill map
        //String killMapPath=args[0];
        String killMapPath="../tailored-mutants-data/results/Lang/3/killmatrix/natural-mutants/non-triggering/";
        BufferedReader scanner = new BufferedReader(new FileReader(killMapPath+"killMap.csv"));

        //parsing a CSV file into Scanner class constructor
        //TODO enable multiple CSV files
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







        //TODO sort the mutant
//       testToMutant.forEach((test1, test2) -> {
//          if (testToMutant.get(test1).containsAll(testToMutant.get(test2))){
//              System.out.println("bingo");
//          }
//       }
//                        );
//
//
        for (String m : testToMutant.keySet()){
            for (String n : testToMutant.keySet()){
                if (testToMutant.get(m).containsAll(testToMutant.get(n))&& m!=n) {
                    //System.out.println(m + " contains all of " +n);

                    if (testToDominated.containsKey(m)) {

                        testToDominated.get(m).add(n);


                    } else {

                        testToDominated.put(m, new ArrayList<>());
                        testToDominated.get(m).add(n);
                    }

                }

            }

        }


        /*for (String m : testToDominated.keySet()){
            System.out.println(testToDominated.get(m));
        }*/

        //TODO the generated dominator graph in a CSV
        ArrayList<String> dominatorGraph = new ArrayList<String>();
        //Print all the mutants(including non-dominated ones)
        //sort mutants alphabetically
        Collections.sort(sortedMutantList);

        //print the header
        String firstRaw="";
        for (Integer mutantNumber : sortedMutantList) {
            firstRaw+=  ",";
            firstRaw+=  mutantNumber.toString();
        }
        firstRaw+="\n";

        dominatorGraph.add(firstRaw);

        //




        //Saving the final graph
        Path dominatorFile = Paths.get("graphs/dominator.csv");
        Files.write(dominatorFile, dominatorGraph, StandardCharsets.UTF_8);



    }
}