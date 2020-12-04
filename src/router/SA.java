package router;

import java.util.List;

public class SA extends MetaHeuristic {
    private float alpha;
    private float t_start;
    private List<List<Integer>> solution;
    private int TL;


    public SA(Architecture a, int nn, float alpha, float t_start, List<List<Integer>> solution, int TL) {
        super(a, nn);
        this.alpha = alpha;
        this.t_start = t_start;
        this.solution = solution;
        this.TL = TL; //iterations pr. temperature
    }


    @Override
    public void run(int runtimeSeconds, float targetCost, long t0 ) {
        List<List<Integer>> sCurrent = solution;
        this.bestSolution = sCurrent;
        float currentBestScore = calculateCostFunction(sCurrent);
        float t = t_start;
        List<List<Integer>> next = null;

        while (((System.currentTimeMillis() - t0)/1000f < runtimeSeconds) && (targetCost < currentBestScore)) {
            //iterations pr. temp
            for (int i=0; i<TL; i++){
                next = generateNeighborhood(sCurrent,nn);

                float costCurrent = calculateCostFunction(sCurrent);
                float costNext = calculateCostFunction(next);

                float delta = costCurrent - costNext;

                if (delta > 0 || p(delta, t)) {

                    sCurrent = next;


                    if (costNext < currentBestScore){
                        currentBestScore = costNext;
                        this.bestSolution = next;

                    }
                }
            }

            t = t*alpha;
        }
        

    }

    private boolean p(float delta, float t) {

        double random = Math.random();

        return Math.exp(delta / t) > random;
    }
}
