package com.example.assignment2;

import java.util.*;

public class Pixel {
    private int x=0;
    private int y=0;

    private String color="B";

    private Map<Pixel,Integer> neighborPixels; // A map of neighboring pixels

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Map<Pixel,Integer> getNeighborPixels() {
        return neighborPixels; // Get the map of neighboring pixels
    }

    public void setNeighborPixels(Map<Pixel,Integer> neighborPixels) {
        this.neighborPixels = neighborPixels; // Set the map of neighboring pixels
    }

    public void addNeighbor(Pixel neighbor,Integer index) {
        this.neighborPixels.put(neighbor,index); // Add the neighboring pixel to the map of neighborPixels
    }

    public void connectToPixel(Integer sourceIndex, Pixel sourcePixel, Integer destIndex, Pixel destPixel) {

    }

    public Pixel(int x, int y, String color) {
        setX(x);
        setY(y);
        setColor(color);
        this.neighborPixels = new HashMap<>(); // Initialize the map of neighborPixels as an empty HashMap
    }

    @Override
    public String toString() {
        return "Pixel{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
