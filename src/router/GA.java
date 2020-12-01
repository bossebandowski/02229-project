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
        generatedChildRoute.addAll(parentRoute1);
        List<Integer> commons = new ArrayList<>();
        for(int i = 0; i < parentRoute1.size(); i++)
        {
            for(int k = 0; k < parentRoute2.size(); k++)
            {
                if(parentRoute1.get(i) == parentRoute2.get(k) && i != 0 && i != (parentRoute1.size() - 1))
                {
                    commons.add(parentRoute1.get(i));
                }
            }
        }
        // if there is no common node return by one a mutated child since crossover is not possible
        if(commons.size() == 0)
        {
            generatedChildRoute = _sb.generateRandomRoute(a.getStream(parentRoute1.get(0),parentRoute1.get(parentRoute1.size()-1)));
            return generatedChildRoute;
        }
        //If only one node common then there is no return
        if(commons.size() == 1)
        {
            generatedChildRoute.clear();
            int index = 0;
            while(parentRoute1.get(index) != commons.get(0))
            {
                generatedChildRoute.add(parentRoute1.get(index));
                index++;
            }
            int index2 = -1;
            for(int i = 0; i < parentRoute2.size(); i++)
            {
                if(commons.get(0) == parentRoute2.get(i))
                {
                    index2 = i;
                    break;
                }
            }
            for(int i = index2; i < parentRoute2.size(); i++)
            {
                generatedChildRoute.add(parentRoute2.get(i));
            }

        }
        else if(commons.size() > 2)
        {
            generatedChildRoute.clear();
            int random1 = rand.nextInt(commons.size() - 1);
            int index = 0;
            while(parentRoute1.get(index) != commons.get(random1))
            {
                generatedChildRoute.add(parentRoute1.get(index));
                index++;
            }
            int index2 = -1;
            for(int i = 0; i < parentRoute2.size(); i++)
            {
                if(commons.get(random1) == parentRoute2.get(i))
                {
                    index2 = i;
                    break;
                }
            }
            for(int i = index2; i < parentRoute2.size(); i++)
            {
                generatedChildRoute.add(parentRoute2.get(i));
            }
        }
        return generatedChildRoute;
    }


    @Override
    public void run(int runtimeSeconds, float targetScore, long initTime) {
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
            prepareFinalSolution();
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
