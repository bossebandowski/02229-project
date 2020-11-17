import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PathPlanner {
    static ArrayList<String> parsedArgs = new ArrayList<>(Arrays.asList("../data/TC0_example.app_network_description", "out.xml", "bfs", "sa", "10"));
    static ArrayList<String> allowedFlags = new ArrayList<String>(Arrays.asList("-in", "-out", "-sb", "-mh", "-rt"));

    private static void printHelp() {
        System.err.println("You can use the following flags to change the program:");
        System.err.println("-in <path/to/input/file>");
        System.err.println("-out <path/to/output/file>");
        System.err.println("-sb <sbName>");
        System.err.println("-mh <mhName>");
        System.err.println("-rt <runTimeSeconds>");
        System.err.println();
        System.err.println("Valid sbNames are:");
        System.err.println("\tastar");
        System.err.println("\tbfs");
        System.err.println("\trnd");
        System.err.println("Valid mhNames are:");
        System.err.println("\ttest");
        System.err.println("\tsa");
        System.err.println("\tga");
        System.err.println("Example (from terminal while being in src folder):");
        System.err.println("\t>>>javac *.java");
        System.err.println("\t>>>java PathPlanner -mh sa -sb bfs -rt 5");
    }

    private static void parseArgs(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                if (allowedFlags.contains(args[i])) {
                    parsedArgs.set(allowedFlags.indexOf(args[i]), args[i + 1]);
                }
            }
        } catch (Exception e) {
            System.err.println("ERR: COULD NOT PARSE ARGUMENTS");
            printHelp();
            System.exit(-1);
        }
    }

    public static void run(String pIn, String pOut, String sbType, String mhType, int runTime) {

        // built problem model
        IOInterface ioHandler = new IOInterface();
        Architecture architecture = ioHandler.parse(pIn);
        architecture.buildGraph();

        // built initial solution
        SolutionBuilder sb = null;
        switch (sbType) {
            case "bfs":     sb = new BfsSolutionBuilder(architecture);
                System.out.println("Initialising BfsSolutionBuilder");
                            break;
            case "astar":   sb = new AStarSolutionBuilder(architecture);
                System.out.println("Initialising AStarSolutionBuilder");
                            break;
            case "rnd":     sb = new RandomSolutionBuilder(architecture);
                System.out.println("Initialising RandomSolutionBuilder");
                            break;
            default:        System.err.println("invalid initial solution builder name.");
                            printHelp();
                            System.exit(-1);
        }

        List<List<Integer>> initSol = sb.builtSolution();

        // run optimization
        MetaHeuristic mh = null;
        switch (mhType) {
            case "sa":      mh = new SA(architecture, 0.99f, 100, initSol);
                System.out.println("Initialising SA");

                break;
            case "ga":      mh = new GA(architecture, 10, 10, 20);
                System.out.println("Initialising GA");
                            break;
            case "test":    mh = new TestMH(architecture);
                System.out.println("Initialising TestMH");
                            break;
            default:        System.err.println("invalid metaheuristic name.");
                            printHelp();
                            System.exit(-1);
        }

        // safe initial solution for later eval
        List<List<Integer>> solutionCopy = mh.createSolutionCopy(initSol);

        // run metaheuristic
        System.out.println("Running optimisation for " + runTime + "s...");
        mh.run(runTime);
        // extract solution
        List<List<Integer>> best = mh.getSolution();

        System.out.printf("Initial cost:\t %.2f\n", mh.calculateCostFunction(solutionCopy));
        System.out.printf("Optimized cost:\t %.2f\n", mh.calculateCostFunction(best));

        // verify solution
        System.out.print("Checking viability...");
        if (mh.isViable(best)) {
            System.out.println("ok");
        } else {
            System.out.println("ERR");
        }

        // write output to file
        System.out.println("Writing solution to " + pOut);
        ioHandler.writeSolution(initSol, pOut, "testName");
    }

    public static void main(String[] args) throws Exception {
        parseArgs(args);
        String pIn = parsedArgs.get(0);
        String pOut = parsedArgs.get(1);
        String sbType = parsedArgs.get(2);
        String mhType = parsedArgs.get(3);
        int runTimeSeconds = Integer.parseInt(parsedArgs.get(4));

        run(pIn, pOut, sbType, mhType, runTimeSeconds);
    }
}
