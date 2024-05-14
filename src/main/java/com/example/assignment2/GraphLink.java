package com.example.assignment2;

public class GraphLink<T> {
    public GraphNode<T> destNode; //Could also store source node if required
    public int cost=1; //Other link attributes could be similarly stored

    public GraphNode<T> getDestNode() {
        return destNode;
    }

    public void setDestNode(GraphNode<T> destNode) {
        this.destNode = destNode;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public GraphLink(GraphNode<T> destNode, int cost) {
        this.destNode = destNode;
        this.cost = cost;
    }
}
