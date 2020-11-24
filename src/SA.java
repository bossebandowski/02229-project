import java.util.List;

public class SA extends MetaHeuristic {
    private float alpha;
    private float t_start;
    private List<List<Integer>> solution;


    public SA(Architecture a, float alpha, float t_start, List<List<Integer>> solution) {
        super(a);
        this.alpha = alpha;
        this.t_start = t_start;
        this.solution = solution;
    }


    @Override
    public void run(int runtimeSeconds) {
        List<List<Integer>> s_i = solution;
        float t = t_start;
        List<List<Integer>> next = null;

        long t0 = System.currentTimeMillis();

        while ((System.currentTimeMillis() - t0)/1000f < runtimeSeconds) {
            next = generateNeighborhood(s_i,1);

            float costCurrent = calculateCostFunction(s_i);
            float costNext = calculateCostFunction(next);

            float delta = costCurrent - costNext;

            if (delta > 0 || p(delta, t)) {
                s_i = next;
                t = t*alpha;
            }
        }

        this.bestSolution = s_i;
    }

    private boolean p(float delta, float t) {

        double random = Math.random();

        return Math.exp(delta / t) > random;
    }
}
