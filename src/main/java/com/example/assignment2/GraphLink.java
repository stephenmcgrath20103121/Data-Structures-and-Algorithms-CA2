package com.example.assignment2;

public class GraphLink {
    public GraphNode<?> sourceNode;
    public GraphNode<?> destNode; //Could also store source node if required
    public int cost; //Other link attributes could be similarly stored
    public GraphLink(GraphNode<?> destNode,int cost) {
        this.destNode=destNode;
        this.cost=cost;
    }
}
