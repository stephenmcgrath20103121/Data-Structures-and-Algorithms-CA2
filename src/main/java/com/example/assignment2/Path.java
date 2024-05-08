package com.example.assignment2;

import java.util.List;

public class Path {
    private List<Landmark> path; // Represents the sequence of Landmark objects that make up the route
    private double distance; // The total distance covered by the route
    // Constructor to initialize a Path object with a given path
    public Path(List<Landmark> path) {
        this.path = path;
        this.distance = 0; // Default value for distance
    }

    // Constructor to initialize a Path object with a given path and distance
    public Path(List<Landmark> path, double distance) {
        this.path = path;
        this.distance = distance;
    }

    // Getter method to retrieve the path of the route
    public List<Landmark> getPath() {
        return path;
    }

    // Getter method to retrieve the total distance of the route
    public double getDistance() {
        return distance;
    }
}