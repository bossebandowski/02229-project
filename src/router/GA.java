package router;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GA extends MetaHeuristic
{
    int _init_population_size;
    int _populationSize;
    int _number_of_children;
    RandomSolutionBuilder _sb;
    List<List<List<Integer>>> _population;
    List<Float> _populationFitness;
    Random rand = new Random();


    public GA(Architecture architecture, int nn, int initPopulationSize, int normalPopulationSize, int numberOfChildren)
    {
        super(architecture, nn);
        this._init_population_size = initPopulationSize;
        this._populationSize = normalPopulationSize;
        this._number_of_children = numberOfChildren;
        this._sb = new RandomSolutionBuilder(this.a);
        this._population = new ArrayList<>();
        _populationFitness = new ArrayList<>();
        generateInitPopulation();
        calculateFitness();
    }

    private void generateInitPopulation()
    {
        for(int i = 0; i < _init_population_size; i++)
        {
            this._population.add(_sb.builtSolution());
        }
    }

    private void calculateFitness()
    {
        this._populationFitness.clear();
        for(List<List<Integer>> currentSolution:_population)
        {
            this._populationFitness.add(calculateCostFunction(currentSolution));
        }
    }

    private float sumFitness() {
        float result = 0.0f;
        for (float f : _populationFitness)
        {
            result += f;
        }
        return result;
    }

    private List<List<List<Integer>>> selectParents(int number_of_pairs)
    {
        List<List<List<Integer>>> selectedParents = new ArrayList<>();
        // Roulette-wheel selection
        List<Float> probability = new ArrayList<>();
        List<Float> cumulativeProbability = new ArrayList<>();
        float sumFitness = sumFitness();
        for (Float f : _populationFitness)
        {
            probability.add(f / sumFitness);
        }
        float probabilityBuffer = 0.0f;
        for (Float f : probability)
        {
            probabilityBuffer += f;
            cumulativeProbability.add(probabilityBuffer);
        }
        for (int i = 0; i < 2 * number_of_pairs; i++)
        {
            float rand1 = (float) rand.nextDouble();
            if (cumulativeProbability.get(0) > rand1)
            {
                selectedParents.add(_population.get(0));
            }
            else
            {
                for (int k = 1; k < cumulativeProbability.size(); k++)
                {
                    if (cumulativeProbability.get(k) > rand1)
                    {
                        if (k + 1 < cumulativeProbability.size())
                        {
                            selectedParents.add(_population.get(k + 1));
                        }
                        else
                        {
                            selectedParents.add(_population.get(k));
                        }
                        break;
                    }
                }
            }
        }
        if(selectedParents.size() != number_of_pairs * 2)
        {
            int diff = number_of_pairs * 2 - selectedParents.size();
            for(int i = 0; i < diff; i++)
            {
                selectedParents.add(_population.get(i));
            }
        }
        return selectedParents;
    }

    public List<List<List<Integer>>> generateChildren(List<List<List<Integer>>> parents)
    {
        List<List<List<Integer>>> generatedChildren = new ArrayList<>();
        if(parents.size() != 2 * _number_of_children)
        {
            System.err.println("The size of the parents list not appropriate");
        }
        else
        {
            for(int i = 0; i < 2 * _number_of_children; i += 2)
            {
                generatedChildren.add(generateChild(parents.get(i),parents.get(i+1)));
            }
        }
        return generatedChildren;
    }

    public List<List<Integer>> generateChild(List<List<Integer>> parent1,List<List<Integer>> parent2)
    {
        List<List<Integer>> generatedChild = new ArrayList<>();
        for(int i = 0; i < parent1.size(); i++)
        {
            generatedChild.add(generateChildRoute(parent1.get(i),parent2.get(i)));
        }
        return generatedChild;
    }

    private List<Integer> generateChildRoute(List<Integer> parentRoute1,List<Integer> parentRoute2)
    {
        List<Integer> generatedChildRoute = new ArrayList<>();
        //generatedChildRoute = parentRoute1;
        boolean foundCommonNode = false;
        int nodeToChangeIndex1 = -1;
        int nodeToChangeIndex2 = -1;
        int returnIndex1 = -1;
        int returnIndex2 = -1;
        int maxTry = parentRoute1.size();
        int probes = 0;
        while (!foundCommonNode && probes <= maxTry) {
            nodeToChangeIndex1 = rand.nextInt(parentRoute1.size()-2) + 1;
            nodeToChangeIndex2 = -1;
            int nodeToChange = parentRoute1.get(nodeToChangeIndex1);
            for (int i = 0; i < parentRoute2.size(); i++) {
                if (parentRoute2.get(i) == nodeToChange) {
                    nodeToChangeIndex2 = i;
                    foundCommonNode = true;
                }
            }
            if(!foundCommonNode)
            {
                continue;
            }
            // Find return node (worst case this is the destination node)
            for (int i = nodeToChangeIndex1 + 1; i < parentRoute1.size(); i++) {
                for (int k = nodeToChangeIndex2 + 1; k < parentRoute2.size(); k++) {
                    if (parentRoute1.get(i) == parentRoute2.get(k)) {
                        returnIndex1 = i;
                        returnIndex2 = k;
                    }
                }
            }
        }
        // Copy the remaining nodes from parent1 to result
        for (int i = 0; i < nodeToChangeIndex1; i++) {
            generatedChildRoute.add(parentRoute1.get(i));
        }
        // Copy the nodes from the parent2
        for (int i = nodeToChangeIndex2; i < returnIndex2; i++) {
            generatedChildRoute.add(parentRoute2.get(i));
        }
        // Copy last elements of parent1
        for (int i = returnIndex1; i < parentRoute1.size(); i++) {
            generatedChildRoute.add(parentRoute1.get(i));
        }

        return generatedChildRoute;
    }


    @Override
    public void run(int runtimeSeconds)
    {
        long initTime = System.currentTimeMillis();
        while((System.currentTimeMillis() - initTime)/1000 < runtimeSeconds)
        {
            calculateFitness();
            List<List<List<Integer>>> selectedParents = selectParents(_number_of_children);
            List<List<List<Integer>>> generatedChildren = generateChildren(selectedParents);
            List<List<List<Integer>>> selectedChildren = new ArrayList<>();
            for(int i = 0; i < _populationSize / 2; i++)
            {
                int rand1 = rand.nextInt(_population.size());
                _population.remove(rand1);
                if(generatedChildren.size() == 0)
                {
                    int k = 0;
                }
                int rand2 = rand.nextInt(generatedChildren.size());
                selectedChildren.add(generatedChildren.get(rand2));
            }
            _population.addAll(selectedChildren);
        }
        prepareFinalSolution();
    }

    private void prepareFinalSolution()
    {

        List<List<Integer>> solution = new ArrayList<>();
        float maxFitness = -1.0f;
        for(List<List<Integer>> currentSolution:_population)
        {
            float costFunction = calculateCostFunction(currentSolution);
            if(costFunction >= maxFitness)
            {
                maxFitness = costFunction;
                solution = currentSolution;
            }
        }
        System.out.println(maxFitness);
        this.bestSolution = solution;
    }
}