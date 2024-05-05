package com.example.assignment2;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

public class Graph implements Initializable {
        // A static graph object to represent the graph
        public static Graph graph;

        // A landmark adjacency list to represent vertices on the graph
        private Map<Landmark, List<Landmark>> adjacencyList;

        // Method to get the set of neighbors of a given landmark
        public Set<Landmark> getNeighbors(Landmark landmark) {
            return landmark.getNeighborLandmarks().keySet();
        }

        // Method to find the shortest path between two landmarks using BFS
        public Path bfsAlgorithm(Landmark start, Landmark end) {
            // Initialize a previous map, queue, and visited set
            Map<Landmark, Landmark> previous = new HashMap<>();
            Queue<Landmark> queue = new LinkedList<>();
            Set<Landmark> visited = new HashSet<>();

            // Add the start landmark to the queue and visited set
            queue.add(start);
            visited.add(start);

            // While the queue is not empty
            while (!queue.isEmpty()) {
                // Get the next landmark from the queue
                Landmark current = queue.poll();

                // If we have reached the end landmark, build the path and return it
                if (current.equals(end)) {
                    List<Landmark> path = new ArrayList<>();
                    int stops = 0;

                    Landmark pathLandmark = end;
                    while (pathLandmark != null) {
                        path.add(0, pathLandmark);
                        pathLandmark = previous.get(pathLandmark);
                        stops++;
                    }

                    return new Path(path, stops - 1);
                }

                // Otherwise, get the neighbors of the current landmark and explore them
                Set<Landmark> neighbors = getNeighbors(current);
                for (Landmark neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                        previous.put(neighbor, current);
                    }
                }
            }

            // If no path is found, return null
            return null;
        }


        // Method to find the shortest path between two landmarks using Dijkstra's algorithm
        public Path dijkstraAlgorithm(Set<Landmark> allLandmarks, Landmark start, Landmark end) {
            // Initialize data structures for distances, previous landmarks, and a priority queue
            Map<Landmark, Double> distances = new HashMap<>();
            Map<Landmark, Landmark> previous = new HashMap<>();
            PriorityQueue<Landmark> queue = new PriorityQueue<>((a, b) -> Double.compare(distances.get(a), distances.get(b)));

            // Initialize the distances map with INFINITY distance for all landmarks, except for the start landmark, which has distance 0
            for (Landmark landmark : allLandmarks) {
                distances.put(landmark, Double.MAX_VALUE);
            }
            distances.put(start, 0.0);

            // Start the search from the start landmark
            queue.add(start);

            while (!queue.isEmpty()) {
                Landmark current = queue.poll();

                // If we have reached the end landmark, build the path and return it
                if (current.equals(end)) {
                    List<Landmark> path = new ArrayList<>();
                    double totalDistance = 0.0;

                    // Build the path and calculate the total distance by backtracking through the previous landmarks
                    for (Landmark landmark = end; landmark != null; landmark = previous.get(landmark)) {
                        path.add(0, landmark);
                        if (previous.get(landmark) != null) {
                            totalDistance += distances.get(landmark);
                        }
                    }

                    return new Path(path, totalDistance);
                }

                // Otherwise, get the neighbors of the current landmark and explore them
                for (Landmark neighbor : getNeighbors(current)) {
                    // Calculate the distance from the start landmark to the neighbor through the current landmark
                    double distance = distances.get(current) + current.getNeighborLandmarks().get(neighbor);

                    // If the calculated distance is smaller than the previously recorded distance, update it
                    if (distance < distances.get(neighbor)) {
                        distances.put(neighbor, distance);
                        previous.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }

            // If no path is found, return null
            return null;
        }


        // Method to initialize the Graph object
        public void initialize(URL url, ResourceBundle resourceBundle){
            // Set the static graph object to this
            graph = this;
        }
    }

