package com.example.assignment2;

import java.util.*;

public class Algorithms {
    static List<GraphNode<String>> path;

    public static List<GraphNode<String>> bfs(GraphNode<String> start, GraphNode<String> destination){

        //if there isn't a start or destination stop
        if (start == null || destination == null){
            return null;
        }
        //Generating the Queue, Visited and Previous
        Queue<GraphNode<String>> queue = new LinkedList<>();

        Set<GraphNode<String>> visited = new HashSet<>();

        HashMap<GraphNode<String>, GraphNode<String>> previous = new HashMap<>();

        queue.add(start);

        while (!queue.isEmpty()){

            GraphNode<String> current = queue.poll();

            if (current.equals(destination)){
                break;
            }
            //Go through all edges and add the node that is next to the queue and makes it visited and added to the Previous Map
            for (GraphLink<String> edge : current.getAdjList()){
                GraphNode<String> next = edge.getDestNode();
                if(!visited.contains(next)){
                    visited.add(next);
                    queue.add(next);
                    previous.put(next, current);
                }
            }
        }
        //Sets the path
        path = getNodeListFromMap(previous, start, destination);
        return path;
    }

    public static List<GraphNode<String>> dfs(GraphNode<String> start, GraphNode<String> destination){

        //if there isn't a start or destination stop
        if (start == null || destination == null){
            return null;
        }

        //Generating the Stack, Visited and Previous
        Stack<GraphNode<String>> stack = new Stack<>();
        Set<GraphNode<String>> visited = new HashSet<>();
        HashMap<GraphNode<String>, GraphNode<String>> previous = new HashMap<>();

        stack.push(start);

        while (!stack.isEmpty()) {
            GraphNode<String> current = stack.pop();

            if (current.equals(destination)) {
                break;
            }

            if (!visited.contains(current)) {
                visited.add(current);

                //Go through each edge
                for (GraphLink<String> edge : current.getAdjList()){
                    GraphNode<String> next = edge.getDestNode();
                    //if haven't visited add it to being visited
                    if(!visited.contains(next)){
                        stack.push(next);
                        previous.put(next, current);
                    }
                }
            }
        }
        // Sets the path
        path = getNodeListFromMap(previous, start, destination);
        return path;
    }

    public static List<GraphNode<String>> Dijkstra(List<GraphNode<String>> graph,GraphNode<String> start, GraphNode<String> destination){
        //Generating the Queue, Visited and Previous
        Queue<GraphNode<String>> queue = new LinkedList<>();

        HashMap <GraphNode<String>, GraphNode<String>> previous = new HashMap<>();

        Map<GraphNode<String>, Integer> cost = new HashMap<>();

        for (GraphNode<String> node : graph){
            cost.put(node, Integer.MAX_VALUE);
        }
        cost.put(start,0);

        queue.add(start);

        while (queue.size() != 0){

            GraphNode<String> current = queue.poll();

            // Go through each edge
            for (GraphLink edge : current.getAdjList()){

                GraphNode next = edge.getDestNode();

                int nextCost = edge.getCost();

                //if the cost from the current + cost is less then the path we take then update
                if (cost.get(current) + nextCost < cost.get(next)){
                    cost.put(next, cost.get(current) + nextCost);

                    previous.put(next, current);

                    queue.add(next);
                }
            }
        }
        // Sets the path
        path = getNodeListFromMap(previous, start, destination);
        return path;
    }

    public static List<GraphNode<String>> getNodeListFromMap(HashMap<GraphNode<String>, GraphNode<String>> previous,GraphNode<String> start,GraphNode<String> destination){
        List<GraphNode<String>> path=new ArrayList<>();
        while(!destination.equals(start)){
            path.add(destination);
            destination=previous.get(destination);
        }
        return path;
    }
}
