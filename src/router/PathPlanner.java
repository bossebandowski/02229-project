package router;

import java.lang.reflect.Array;
import java.util.*;


public class PathPlanner {
    static ArrayList<String> parsedArgs = new ArrayList<>(Arrays.asList("../data/TC0_example.app_network_description", "../out.xml", "bfs", "sa", "10", "-1"));
    static ArrayList<String> allowedFlags = new ArrayList<String>(Arrays.asList("-in", "-out", "-sb", "-mh", "-rt", "-nn"));

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
        System.err.println("\t>>>javac router/*.java");
        System.err.println("\t>>>java router.PathPlanner -mh sa -sb bfs -rt 5");
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

    public static TreeMap<Long, Float> run(String pIn, String pOut, String sbType, String mhType, int runTime, int nn, float targetScore) {
        TreeMap<Long, Float> out = new TreeMap<>();

        // built problem model
        IOInterface ioHandler = new IOInterface();
        Architecture architecture = ioHandler.parse(pIn);
        architecture.buildGraph();

        // record starting time
        long initTime = System.currentTimeMillis();

        // built initial solution
        SolutionBuilder sb = null;
        switch (sbType) {
            case "bfs":
                sb = new BfsSolutionBuilder(architecture);
                System.out.println("Initialising BfsSolutionBuilder");
                break;
            case "astar":
                sb = new AStarSolutionBuilder(architecture);
                System.out.println("Initialising AStarSolutionBuilder");
                break;
            case "rnd":
                sb = new RandomSolutionBuilder(architecture);
                System.out.println("Initialising RandomSolutionBuilder");
                break;
            default:
                System.err.println("invalid initial solution builder name.");
                printHelp();
                System.exit(-1);
        }

        List<List<Integer>> initSol = sb.builtSolution();

        // run optimization
        MetaHeuristic mh = null;
        switch (mhType) {
            case "sa":
                mh = new SA(architecture, nn, 0.94f, 100, initSol, 10);
                System.out.println("Initialising SA");
                break;
            case "ga":
                mh = new GA(architecture, nn, 100, 100, 100, initSol);
                System.out.println("Initialising GA");
                break;
            case "test":
                mh = new TestMH(architecture, nn);
                System.out.println("Initialising TestMH");
                break;
            default:
                System.err.println("invalid metaheuristic name.");
                printHelp();
                System.exit(-1);
        }

        // safe initial solution for later eval
        List<List<Integer>> solutionCopy = mh.createSolutionCopy(initSol);

        System.out.println("Neighbourhood function : " + nn);

        // run metaheuristic
        System.out.println("Running optimisation for up to " + runTime + "s...");
        System.out.println("Target score: " + targetScore);
        mh.run(runTime, targetScore, initTime);
        long finalTime = System.currentTimeMillis();

        // extract solution
        List<List<Integer>> best = mh.getSolution();
        float cost = mh.calculateCostFunction(best);

        System.out.printf("Initial cost:\t %.2f\n", mh.calculateCostFunction(solutionCopy));
        System.out.printf("Optimized cost:\t %.2f\n", cost);

        // verify solution
        System.out.print("Checking viability...");
        boolean isViable = mh.isViable(best);
        if (isViable) {
            System.out.println("ok");
        } else {
            System.out.println("ERR");
        }

        // write output to file
        System.out.println("Writing solution to " + pOut);
        ioHandler.writeSolution(initSol, pOut, "testName");

        // recycle
        architecture = null;
        sb = null;
        mh = null;
        initSol = null;
        solutionCopy = null;
        best = null;
        ioHandler = null;
        // reset counts
        Node.resetCount();
        Link.resetCount();
        Stream.resetCount();

        if (isViable) {
            out.put(finalTime-initTime, cost);
        } else {
            out.put(-1L, -1f);
        }

        return out;
    }

    public static void main(String[] args) throws Exception {
        parseArgs(args);
        String pIn = parsedArgs.get(0);
        String pOut = parsedArgs.get(1);
        String sbType = parsedArgs.get(2);
        String mhType = parsedArgs.get(3);
        int runTimeSeconds = Integer.parseInt(parsedArgs.get(4));
        int nn = Integer.parseInt(parsedArgs.get(5));

        run(pIn, pOut, sbType, mhType, runTimeSeconds, nn, 0);
    }
}
