package router;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;




public class HyperParameterTesting {

    public static void main(String[] args) throws IOException {
        IOInterface ioHandler = new IOInterface();
        Architecture architecture = ioHandler.parse("data/TC7_huge.app_network_description" );
        //Architecture architecture = ioHandler.parse("data/TC0_example.app_network_description" );
        architecture.buildGraph();
        SolutionBuilder sb = new RandomSolutionBuilder(architecture);
        List<List<Integer>> initSol = sb.builtSolution();
        MetaHeuristic mh =null;
        long t0 = System.currentTimeMillis();
        mh = new SA(architecture, 1, 0.94f, 200,  initSol,10);
        mh.run(60,1000f,t0);

        /*
        int[] saTempArray = {30,70,100,150,200,300,400,500};
        int[] nnArray = {0,1};
        int[] runtimeArray = {5,10,20,30,40,50,60};
        float[] alphaArray = {0.75f, 0.8f, 0.85f, 0.9f, 0.95f, 0.98f, 0.99f};

        IOInterface ioHandler = new IOInterface();
        Architecture architecture = ioHandler.parse("../data/TC7_huge.app_network_description" );
        architecture.buildGraph();
        // built initial solution
        SolutionBuilder sb = new RandomSolutionBuilder(architecture);
        List<List<Integer>> initSol = sb.builtSolution();


        FileWriter csvWriter = new FileWriter("SA_tuning2.csv");
        csvWriter.append("Neighborhood function");
        csvWriter.append(",");
        csvWriter.append("Temp");
        csvWriter.append(",");
        csvWriter.append("Alpha");
        csvWriter.append(",");
        csvWriter.append("Runtime");
        csvWriter.append(",");
        csvWriter.append("Initial Cost");
        csvWriter.append(",");
        csvWriter.append("Optimized Cost");
        csvWriter.append(",");
        csvWriter.append("Feasible");
        csvWriter.append("\n");

        int counter = 0;
        int all = saTempArray.length * nnArray.length * runtimeArray.length * alphaArray.length;
        MetaHeuristic mh =null;
        List<String> row;
        for (int neighborhoodFunction: nnArray){
            for (int temp : saTempArray){
                for (int runtime : runtimeArray){
                    for (float alpha : alphaArray){
                        mh = new SA(architecture, neighborhoodFunction, alpha, temp,  initSol,5,10);
                        mh.run(runtime);
                        List<List<Integer>> best = mh.bestSolution;
                        float iniCost = mh.calculateCostFunction(initSol);
                        float bestCost = mh.calculateCostFunction(best);
                        row = Arrays.asList(String.valueOf(neighborhoodFunction), String.valueOf(temp),String.valueOf(alpha), String.valueOf(runtime),
                                String.valueOf(iniCost), String.valueOf(bestCost), String.valueOf(mh.isViable(best)));
                        csvWriter.append(String.join(",",row));
                        csvWriter.append("\n");
                        counter ++;
                        System.out.println(counter + " of " + all);
                    }
                }
            }
        }
        csvWriter.flush();
        csvWriter.close();*/

    }


}



