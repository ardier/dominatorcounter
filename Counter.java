import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Counter {


    //Let's just count some mutants
    public static void main(String[] args) throws FileNotFoundException {
        HashMap<Integer, Integer> mutantCounter = new HashMap<Integer, Integer>();

        //TODO pass in killMap.csv

        //adding the mutant records from kill map
        //TODO automate this
        String killMapPath="../tailored-mutants-data/results/Lang/3/killmatrix/natural-mutants/non-triggering";
        var scanner = new File(killMapPath+"killMap.csv");

        //parsing a CSV file into Scanner class constructor
        //TODO enable multiple CSV files
        var sc = new Scanner((scanner));
        sc.useDelimiter(";");   //sets the delimiter pattern
        var index = 0;
        while (sc.hasNext())  //returns a boolean value
        {

            System.out.println(sc.next());
            //make sure this is the correct syntax


        }

        sc.close();
        //closes the scanner




        //TODO sort the mutant

        //TODO the generated dominator graph in a CSV


    }
}