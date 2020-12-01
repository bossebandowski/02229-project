# 02229-project

## Purpose
This repository has been developed as part of the course "02229 - Systems Optimization E20" at the Technical University of Denmark.
The goal is to develop and compare different methods for constructive and iterative metaheuristics on the example of routing data
traffic in vehicles. For details, check https://ieeexplore.ieee.org/document/8402374.
## Execution
```bash
cd path/to/src
javac router/*.java
java router/PathPlanner
``` 
This will invoke the programme with default parameters. Those are:
- input file path:  "../data/TC0_example.app_network_description"
- output file path: "../out.xml"
- constructive metaheuristic: "bfs"
- iterative metaheuristic: "sa"
- runtime (s): 10
- neighbourhood function: -1

### Parameters
#### Input File Path
Provide a valid path to an .app_network_description file. This file contains all the information needed to run the
optimization. You can change this parameter by adding the following flag:
```bash
java router/PathPlanner -in path/to/input/file
```
#### Output File Path
The result of the optimization is a number of routes for the traffic streams defined in the input file. They are written
to an .xml file which is in accordance with the output format standards defined in the task description for this project.
This file is saved to the output file path. You can change this parameter by adding the following flag:
```bash
java router/PathPlanner -out path/to/output/file
```
#### Constructive Metaheuristic
This parameter defines how the initial solution is build. We provide a random solution builder ("rnd"), a breadth first
search solution builder ("bfs"), and an A* based solution builder ("astar").
You can change this parameter by adding the following flag:
```bash
java router/PathPlanner -sb astar
java router/PathPlanner -sb bfs
java router/PathPlanner -sb rnd
```
#### Iterative Metaheuristic
This defines the optimization strategy. We have implemented a version of the simulated annealing metaheuristic ("sa")
and a genetic algorithm with tournament selection ("ga"). **The genetic algorithm is only compatible with the random
neighbourhood function**.
You can change this parameter by adding the following flag:
```bash
java router/PathPlanner -mh sa
java router/PathPlanner -mh ga
```
#### Runtime
The stop criterion for the iterative optimization is time. Any postive integer value is a valid argument.
You can change this parameter by adding the following flag:
```bash
java router/PathPlanner -rt 5
java router/PathPlanner -rt 300
...
```
#### Neighbourhood Function
The neighbourhood function is used by the iterative metaheuristics to generate solutions that are similar to the current
one. We implemented one neighbourhood function that replaces a segment of one route in the solution with a
randomly generate new segment. The only constraint is that the nodes must be connected to each other as defined by the
input file.

The other option is a more goal-oriented approach. For one route in the solution, the three shortest paths are generated
between the source and the destination node. Then, one of these three paths is randomly selected.

You can choose between these options:
- -1 for a random choice at every iteration
- 0 to enforce the one out of k shortest paths function
- 1 to enforce the random function

You can change this parameter by adding the following flag:
```bash
java router/PathPlanner -nn -1
java router/PathPlanner -nn 0
java router/PathPlanner -nn 1
```

###Examples

Run the genetic algorithm on the huge test file for 30 seconds with random initial solutions:
```
java router/PathPlanner -rt 30 -in "../data/TC7_huge.app_network_description" -mh ga -sb rnd
```