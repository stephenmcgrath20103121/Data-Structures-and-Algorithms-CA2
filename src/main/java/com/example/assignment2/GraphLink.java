package com.example.assignment2;

public class GraphLink<T> {
    public GraphNode<T> sourceNode;
    public GraphNode<T> destNode; //Could also store source node if required
    public int cost; //Other link attributes could be similarly stored
    public GraphLink(GraphNode<T> destNode, int cost) {
        this.destNode = destNode;
        this.cost = cost;
    }
}
