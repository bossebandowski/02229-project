package router;

import java.util.List;

public class SA extends MetaHeuristic {
    private float alpha;
    private float t_start;
    private List<List<Integer>> solution;


    public SA(Architecture a, int nn, float alpha, float t_start, List<List<Integer>> solution) {
        super(a, nn);
        this.alpha = alpha;
        this.t_start = t_start;
        this.solution = solution;
    }


    @Override
    public void run(int runtimeSeconds, float targetCost, long t0) {
        float currentBestScore = Float.MAX_VALUE;
        List<List<Integer>> s_i = solution;
        float t = t_start;
        List<List<Integer>> next = null;

        while (((System.currentTimeMillis() - t0)/1000f < runtimeSeconds) && (targetCost < currentBestScore)) {
            next = generateNeighborhood(s_i,1);

            float costCurrent = calculateCostFunction(s_i);
            float costNext = calculateCostFunction(next);

            float delta = costCurrent - costNext;

            if (delta > 0 || p(delta, t)) {
                s_i = next;
                t = t*alpha;
                currentBestScore = costNext;
            }
        }

        this.bestSolution = s_i;
    }

    private boolean p(float delta, float t) {

        double random = Math.random();

        return Math.exp(delta / t) > random;
    }
}
