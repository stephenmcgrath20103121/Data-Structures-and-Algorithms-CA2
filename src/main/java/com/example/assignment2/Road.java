package com.example.assignment2;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private String roadName;
    private List<Landmark> landmarks;

    public Road(String roadName) {
        // Initialize the road with a name
        this.roadName = roadName;
        // Initialize the list of landmarks as an empty ArrayList
        this.landmarks = new ArrayList<>();
    }

    public String getLineName() {
        // Get the name of the road
        return roadName;
    }

    public List<Landmark> getLandmarks() {
        // Get the list of landmarks on the road
        return landmarks;
    }

    public void addLandmark(Landmark landmark) {
        // Add a landmark to the road by adding it to the list of landmarks
        this.landmarks.add(landmark);
        landmark.addLine(this);// add this road to the landmark
    }
}
