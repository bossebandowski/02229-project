// import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*enum ReplacementType
{

}*/

public class GA extends MetaHeuristic {
    ArrayList<Stream> _streams;
    // Architecture _architecture;
    int _init_population_size;
    int _population_size;
    int _number_of_generations;
    int _number_of_children;
    HashMap<Stream, List<List<Integer>>> _population;
    HashMap<Stream, List<Float>> _fitness; // Fitness level of each member of population
    Random rand = new Random();
    private RandomSolutionBuilder sb;

    public GA(Architecture architecture, int init_population_size, int _population_size,
              int _number_of_children) {
        super(architecture);
        this._streams = architecture.getStreams();
        //this._architecture = architecture;
        this._init_population_size = init_population_size;
        this._population_size = _population_size;
        this._number_of_children = _number_of_children;
        this.sb = new RandomSolutionBuilder(architecture);
    }

    public void initPopulation() {
        for (Stream currentStream : _streams) {
            List<List<Integer>> generated_population = new ArrayList<>();
            for (int i = 0; i < _init_population_size; i++) {
                generated_population.add(this.sb.generateRandomRoute(currentStream));
            }
            _population.put(currentStream, generated_population);
        }
    }

    public void calculateFitness() {
        for (Stream currentStream : _streams) {
            _fitness.get(currentStream).clear();
            List<List<Integer>> currentPopulation = _population.get(currentStream);
            for (List<Integer> currentSolution : currentPopulation) {
                _fitness.get(currentStream).add(calculateSolutionFitness(currentSolution));
            }
        }
    }

    public float calculateSolutionFitness(List<Integer> solution) {
        float result = 0.0f;
        // TODO calculate fitness
        return result;
    }

    public HashMap<Stream, List<List<Integer>>> selectParents(int number_of_pairs) {
        HashMap<Stream, List<List<Integer>>> result = new HashMap<>(); // number_of_pairs * 2 solution assigned to each streams
        // Roulette-wheel selection
        for (Stream currentStream : _streams) {
            List<List<Integer>> currentPopulation = _population.get(currentStream);
            List<Float> currentFitness = _fitness.get(currentStream);
            List<Float> probability = new ArrayList<>();
            List<Float> cumulativeProbability = new ArrayList<>();
            List<List<Integer>> selectedParents = new ArrayList<>();
            float sumFitness = sumFitness(currentStream);
            for (Float f : currentFitness) {
                probability.add(f / sumFitness);
            }
            float probabilityBuffer = 0.0f;
            for (Float f : probability) {
                probabilityBuffer += f;
                cumulativeProbability.add(probabilityBuffer);
            }

            for (int i = 0; i < 2 * number_of_pairs; i++) {
                float rand1 = (float) rand.nextDouble();
                System.out.println(rand1);
                if (cumulativeProbability.get(0) > rand1) {
                    selectedParents.add(currentPopulation.get(0));
                } else {
                    for (int k = 1; k < cumulativeProbability.size(); k++) {
                        if (cumulativeProbability.get(k) > rand1) {
                            if (k + 1 < cumulativeProbability.size()) {
                                selectedParents.add(currentPopulation.get(k + 1));
                            } else {
                                selectedParents.add(currentPopulation.get(k));
                            }
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public float sumFitness(Stream stream) {
        float result = 0.0f;
        for (float f : _fitness.get(stream)) {
            result += f;
        }
        return result;
    }

    public HashMap<Stream, List<List<Integer>>> generateGeneration() {
        HashMap<Stream, List<List<Integer>>> newGeneration = new HashMap<>();
        HashMap<Stream, List<List<Integer>>> parents = selectParents(_number_of_children);
        for (Stream currentStream : _streams) {
            newGeneration.put(currentStream, null);
            List<List<Integer>> currentParents = parents.get(currentStream);
            int parentPointer = 0;
            for (int i = 0; i < _number_of_children; i++) {
                int rand1 = rand.nextInt(1) + parentPointer;
                int rand2 = 0;
                if (rand1 == 0) {
                    rand2 = 1;
                }
                newGeneration.get(currentStream).add(changeRandomSegment(currentParents.get(parentPointer + rand1), currentParents.get(parentPointer + rand2)));
                parentPointer += 2;
            }
        }
        return newGeneration;
    }

    public List<Integer> changeRandomSegment(List<Integer> parent1, List<Integer> parent2) {
        List<Integer> mergedRoute = new ArrayList<>();
        boolean foundCommonNode = false;
        int nodeToChangeIndex1 = -1;
        int nodeToChangeIndex2 = -1;
        int returnIndex1 = -1;
        int returnIndex2 = -1;
        while (!foundCommonNode) {
            nodeToChangeIndex1 = rand.nextInt(parent1.size() - 2) + 1;
            nodeToChangeIndex2 = -1;
            int nodeToChange = parent1.get(nodeToChangeIndex1);
            for (int i = 0; i < parent2.size(); i++) {
                if (parent2.get(i) == nodeToChange) {
                    nodeToChangeIndex2 = i;
                    foundCommonNode = true;
                }
            }
            // Find return node (worst case this is the destination node)
            for (int i = nodeToChangeIndex1 + 1; i < parent1.size(); i++) {
                for (int k = nodeToChangeIndex2 + 1; k < parent2.size(); k++) {
                    if (parent1.get(i) == parent2.get(k)) {
                        returnIndex1 = i;
                        returnIndex2 = k;
                    }
                }
            }

        }
        // Copy the remaining nodes from parent1 to result
        for (int i = 0; i < nodeToChangeIndex1; i++) {
            mergedRoute.add(parent1.get(i));
        }
        // Copy the nodes from the parent2
        for (int i = nodeToChangeIndex2; i < returnIndex2; i++) {
            mergedRoute.add(parent2.get(i));
        }
        // Copy last elements of parent1
        for (int i = returnIndex1; i < parent1.size(); i++) {
            mergedRoute.add(parent1.get(i));
        }
        return mergedRoute;
    }

    @Override
    public void run(int runtimeSeconds) {
        long initTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - initTime)/1000 < runtimeSeconds) {
            calculateFitness();
            for (Stream currentStream : _streams) {
                HashMap<Stream, List<List<Integer>>> newGeneration = generateGeneration();
                if (_population_size == _number_of_children) {
                    _population.clear();
                    _population = newGeneration;
                } else {
                    for (int k = 0; k < (_population_size - _number_of_children); k++) {
                        int rand1 = rand.nextInt(_population.get(currentStream).size());
                        _population.get(currentStream).remove(rand1);
                    }
                    _population.get(currentStream).addAll(newGeneration.get(currentStream));
                }
            }
        }
    }
}
