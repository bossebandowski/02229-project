import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathPlanner {
    public static String pathIn;
    public static String pathOut;

    private static void parseArgs(String[] args) {
        if (args.length < 2) {
            System.err.println("ERR: TOO FEW ARGUMENTS");
            System.err.println("\tYou must provide at least two arguments defining the input and the output file.");
            System.err.println("\tExample (from terminal while being in src folder):");
            System.err.println("\t\t>>>javac *.java");
            System.err.println("\t\t>>>java PathPlanner ../data/TC0_example.app_network_description out.xml");
        } else {
            pathIn = args[0];
            pathOut = args[1];
        }

    }

    public static void run(String pIn, String pOut) {
        // built problem model
        IOInterface ioHandler = new IOInterface();
        Architecture architecture = ioHandler.parse(pIn);
        architecture.buildGraph();

        // built initial solution
//        SolutionBuilder sb = new BfsSolutionBuilder(architecture);
        SolutionBuilder sb = new AStarSolutionBuilder(architecture);
        List<List<Integer>> initSol = sb.builtSolution();
        System.out.println(initSol);
        // run optimization

        // verify solution

        // write output to file
    }

    public static void main(String[] args) {
        parseArgs(args);
        run(pathIn, pathOut);
    }
}
