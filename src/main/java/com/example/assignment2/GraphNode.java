package com.example.assignment2;

import java.util.ArrayList;
import java.util.List;

public class GraphNode<T> {
    public T data;
    public List<GraphLink<T>> adjList=new ArrayList<>();
    public String name="";
    public int x=0;
    public int y=0;
    public GraphNode(String name,int x, int y) {
        setName(name);
        setX(x);
        setY(y);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
    public void connectToNodeUndirected(GraphNode<T> destNode,int cost) {
        adjList.add( new GraphLink<>(destNode,cost) );
        destNode.adjList.add( new GraphLink<>(this, cost) );
    }
}