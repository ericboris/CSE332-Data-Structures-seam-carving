package astar;

import edu.princeton.cs.algs4.Stopwatch;
import pq.TreeMapMinPQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @see ShortestPathsSolver for more method documentation
 */
public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    TreeMapMinPQ<Vertex> fringe;
    HashSet<Vertex> visited;

    HashMap<Vertex, Vertex> edgeTo;
    HashMap<Vertex, Double> distanceTo;

    SolverOutcome outcome;
    List<Vertex> solution;
    double solutionWeight;
    double explorationTime;
    int numStatesExplored;

    /**
     * Immediately solves and stores the result of running memory optimized A*
     * search, computing everything necessary for all other methods to return
     * their results in constant time. The timeout is given in seconds.
     */
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        // Initialize the timer handle timeout
        Stopwatch sw = new Stopwatch();

        // initialize instance variables
        solution = new ArrayList<>();
        fringe = new TreeMapMinPQ<>();
        visited = new HashSet<>();
        edgeTo = new HashMap<>();
        distanceTo = new HashMap<>();
        numStatesExplored = 0;

        // initialize the start vertex
        fringe.add(start, input.estimatedDistanceToGoal(start, end));
        visited.add(start);
        edgeTo.put(start, null);
        distanceTo.put(start, 0d);

        // While the fringe is not empty
        // search through it for the next highest priority vertex
        while (fringe.size() != 0 && sw.elapsedTime() < timeout) {

            Vertex v = fringe.removeSmallest();
            //System.out.println("v = " + v);

            // If that vertex is the end vertex
            if (v.equals(end)) {
                // store the distance it took to get to it
                solutionWeight = distanceTo.get(v);
                // and add all the vertices it took to get to it to the solution
                while (edgeTo.get(v) != null) {
                    solution.add(0, v);
                    v = edgeTo.get(v);
                }
                solution.add(0, v);

                // set the states
                outcome = SolverOutcome.SOLVED;
                explorationTime = sw.elapsedTime();

                // the shortest path is found
                return;
            // Otherwise, if the vertex isn't the end
            } else {
                // Increment the number of steps that have been taken to get to this vertex
                numStatesExplored++;

                // Iterate over this vertices neighbor's and for each edge
                List<WeightedEdge<Vertex>> edges = input.neighbors(v);
                for (WeightedEdge<Vertex> e : edges) {
                    Vertex neighbor = e.to();
                    //System.out.println("n = " + neighbor);
                    double weight = distanceTo.get(v) + e.weight();
                    double heuristic = input.estimatedDistanceToGoal(neighbor, end);

                    relax(v, neighbor, weight, heuristic);
                }
            }
        }

        // If the while loop ends there are possibilities
        // either the fringe ran out of vertices without finding the end
        if (fringe.isEmpty()) {
            outcome = SolverOutcome.UNSOLVABLE;
        // or the solution took too long to find
        } else {
            outcome = SolverOutcome.TIMEOUT;
        }
        explorationTime = sw.elapsedTime();
    }

    private void relax(Vertex v, Vertex neighbor, double weight, double heuristic) {
        // If this neighbor as already been encountered
        if (visited.contains(neighbor)) {
            // check if the distance to that vertex when it was last encountered
            // is still the shortest distance

            // if it's not the shortest distance
            if (weight < distanceTo.get(neighbor)) {
                // update it to reflect the new shorter distance
                distanceTo.put(neighbor, weight);
                edgeTo.put(neighbor, v);
                // and if it was already in the fringe
                if (fringe.contains(neighbor)) {
                    fringe.changePriority(neighbor, weight + heuristic);
                }
            }
            // Otherwise, if this neighbor hasn't been encountered
        } else {
            // then store it's data
            edgeTo.put(neighbor, v);
            distanceTo.put(neighbor, weight);
            visited.add(neighbor);
            fringe.add(neighbor, weight + heuristic);
        }
    }

    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List<Vertex> solution() {
        return solution;
    }

    @Override
    public double solutionWeight() {
        return solutionWeight;
    }

    /** The total number of priority queue removeSmallest operations. */
    @Override
    public int numStatesExplored() {
        return numStatesExplored;
    }

    @Override
    public double explorationTime() {
        return explorationTime;
    }
}
