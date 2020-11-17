import java.util.List;

public class SA extends MetaHeuristic {
    private float alpha;
    private float t_start;
    private float stop_Criteria;
    private List<List<Integer>> solution;


    public SA(Architecture a, float alpha, float t_start, float stop_criteria,List<List<Integer>> solution) {
        super(a);
        this.alpha = alpha;
        this.t_start = t_start;
        this.stop_Criteria = stop_criteria;
        this.solution = solution;

    }



    @Override
    public List<List<Integer>> generateNeighborhood(List<List<Integer>> s_i,Integer numPaths) {
        return super.generateNeighborhood(s_i,numPaths);
    }

    @Override
    float calculateCostFunction(List<List<Integer>> solution) {
        return super.calculateCostFunction(solution);
    }

    public void run(){
        List<List<Integer>> s_i = solution;
        float t = t_start;
        List<List<Integer>> next = null;
        
        long t0 = System.currentTimeMillis();

        while ((System.currentTimeMillis() - t0)/1000f < stop_Criteria) {
            next = generateNeighborhood(s_i,3);

            float costCurrent = calculateCostFunction(s_i);
            float costNext = calculateCostFunction(next);

            float delta = costCurrent - costNext;
            if (delta > 0 || p(delta, t)) {
                s_i = next;
                t = t*alpha;
            }
        }

        this.solution = s_i;

    }
    private boolean p(float delta, float t) {
        double random = Math.random();
        return Math.exp(delta / t) > random;
    }
}
