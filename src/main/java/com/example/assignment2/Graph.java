package com.example.assignment2;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Graph implements Initializable {
    // A static graph object to represent the graph
    public static Graph graph;
    public List<GraphNode<String>> allGraphNodes=new ArrayList<>();

    public void addGraphNodesToList(Map<String, GraphNode<String>> nodeMap) throws FileNotFoundException {
        allGraphNodes.addAll(nodeMap.values());
        Image image = new Image(new FileInputStream("src/main/java/com/example/bwImage.JPG"));
        PixelReader pixelReader= image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                boolean isLandmark=false;
                for(int i=0;i<17;i++){
                    if((allGraphNodes.get(i).getX()==x)&&(allGraphNodes.get(i).getY()==y)) {
                        isLandmark=true;
                    }
                }
                if(isLandmark){
                    continue;
                }
                Color color=pixelReader.getColor(x,y);
                GraphNode<String> node=new GraphNode<>("blackNode",x,y);
                if(color.equals(Color.WHITE)) {
                    node = new GraphNode<>("Node Location: " + x + ", " + y, x, y);
                }
                allGraphNodes.add(node);
            }
        }
        allGraphNodes.sort(Comparator.comparing(GraphNode::getIndex));
        linkGraphNodes(image);
    }

     public void linkGraphNodes(Image image){
         PixelReader pixelReader= image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                if (color.equals(Color.WHITE)) {
                    int indexOfGraphNode = y * (int) image.getWidth() + x;

                    GraphNode<String> node = allGraphNodes.get(indexOfGraphNode);
                    int rightIndex = indexOfGraphNode + 1;
                    int belowIndex = indexOfGraphNode + ((int) image.getWidth());
                    int leftIndex=-1;
                    int aboveIndex=-1;
                    if(!(x==0)) leftIndex=indexOfGraphNode-1;
                    if(!(y==0)) aboveIndex=indexOfGraphNode - ((int) image.getWidth());

                    // If pixel to the right is a white pixel, link up with it
                    if ((rightIndex < allGraphNodes.size()) && (!(allGraphNodes.get(rightIndex).getName().equals("blackNode")))) {
                        allGraphNodes.get(rightIndex).connectToNodeUndirected(node, 1);
                    }
                    // If pixel below is white, link up with it
                    if ((belowIndex < allGraphNodes.size()) && (!(allGraphNodes.get(belowIndex).getName().equals("blackNode")))) {
                        allGraphNodes.get(belowIndex).connectToNodeUndirected(node, 1);
                    }
                    // If pixel below is white, link up with it
                    if ((leftIndex!=-1) && (!(allGraphNodes.get(leftIndex).getName().equals("blackNode")))) {
                        allGraphNodes.get(leftIndex).connectToNodeUndirected(node, 1);
                    }
                    // If pixel above is white, link up with it
                    if ((aboveIndex!=-1) && (!(allGraphNodes.get(aboveIndex).getName().equals("blackNode")))) {
                        allGraphNodes.get(aboveIndex).connectToNodeUndirected(node, 1);
                    }

                }
            }
        }
    }

    public List<GraphNode<String>> getAllGraphNodes() {
        return allGraphNodes;
    }

    // Method to initialize the Graph object
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Set the static graph object to this
        graph = this;
    }
}