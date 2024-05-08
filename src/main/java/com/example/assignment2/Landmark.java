package com.example.assignment2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Landmark {
    private String landmarkName; // The name of the landmark
    private double x; // The x-coordinate of the landmark
    private double y; // The y-coordinate of the landmark
    private Map<Landmark, Double> neighborLandmarks; // A map of neighboring landmarks and their distances
    private List<Double> distances; // A list of distances to the neighborLandmarks
    private List<Road> roads; // A list of roads that the landmark is on
    private double distanceFromFirstLandmark = Double.MAX_VALUE; // Distance from the start landmark for Dijkstra's algorithm
    private Landmark previousLandmark; // Previous landmark in the shortest path from the start landmark

    // CONSTRUCTOR
    public Landmark(String landmarkName, double x, double y) {
        this.landmarkName = landmarkName; // Initialize the landmark with a name
        this.x = x; // Initialize the x-coordinate of the landmark
        this.y = y; // Initialize the y-coordinate of the landmark
        this.neighborLandmarks = new HashMap<>(); // Initialize the map of neighborLandmarks as an empty HashMap
        this.distances = new ArrayList<>(); // Initialize the list of distances as an empty ArrayList
        this.roads = new ArrayList<>(); // Initialize the list of roads as an empty ArrayList
    }

    // GETTERS
    public String getLandmarkName() {
        return landmarkName; // Get the name of the landmark
    }

    public double getX() {
        return x; // Get the x-coordinate of the landmark
    }

    public double getY() {
        return y; // Get the y-coordinate of the landmark
    }

    public Map<Landmark, Double> getNeighborLandmarks() {
        return neighborLandmarks; // Get the map of neighboring landmarks and their distances
    }

    public List<Road> getLines() {
        return roads; // Get the list of roads that the landmark is on
    }

    public double getDistanceFromFirstLandmark() {
        return distanceFromFirstLandmark; // Get the distance from the start landmark
    }

    public Landmark getPreviousLandmark() {
        return previousLandmark; // Get the previous landmark in the shortest path from the start landmark
    }

    // SETTERS
    public void setLandmarkName(String landmarkName) {
        this.landmarkName = landmarkName; // Set the name of the landmark
    }

    public void setX(double x) {
        this.x = x; // Set the x-coordinate of the landmark
    }

    public void setY(double y) {
        this.y = y; // Set the y-coordinate of the landmark
    }

    public void setNeighborLandmarks(Map<Landmark, Double> neighborLandmarks) {
        this.neighborLandmarks = neighborLandmarks; // Set the map of neighboring landmarks and their distances
    }

    public void setDistanceFromFirstLandmark(double distanceFromFirstLandmark) {
        this.distanceFromFirstLandmark = distanceFromFirstLandmark; // Set the distance from the start landmark
    }

    public void setPreviousLandmark(Landmark previousLandmark) {
        this.previousLandmark = previousLandmark; // Set the previous landmark in the shortest path from the start landmark
    }
    public void addNeighbor(Landmark neighbor) {
        double distance = this.calculateDistanceTo(neighbor); // Calculate the distance between this landmark and the neighbor
        this.neighborLandmarks.put(neighbor, distance); // Add the neighboring landmark and its distance to the map of neighborLandmarks
        distances.add(distance); // Add the distance to the list of distances
    }
    public double calculateDistanceTo(Landmark other) {
        return Math.sqrt(Math.pow((this.x - other.getX()), 2) + Math.pow((this.y - other.getY()), 2));
    }

    public void addLine(Road road) {
        this.roads.add(road); // Add a road to the list of roads
    }

    @Override
    public String toString() {
        String neighborNames = neighborLandmarks.keySet().stream()
                .map(Landmark::getLandmarkName)
                .collect(Collectors.joining(", "));

        return "Landmark: " + "Name: " + landmarkName + ", xCoordinate: " + x + ", yCoordinate: " + y + ", neighboring landmarks: " + neighborNames;
    }
}