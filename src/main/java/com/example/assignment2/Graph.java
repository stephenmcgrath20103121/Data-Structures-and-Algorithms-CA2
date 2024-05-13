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

    public void addGraphNodesToList() throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/main/java/com/example/bwImage.JPG"));
        PixelReader pixelReader= image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color=pixelReader.getColor(x,y);
                GraphNode<String> node=null;
                if(color.equals(Color.WHITE)) {
                    node = new GraphNode<>("Node Location: " + x + ", " + y, x, y);
                }
                allGraphNodes.add(node);
            }
        }
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

                    GraphNode<String> node = new GraphNode<>("Node Location: " + x + ", " + y, x, y);
                    int rightIndex = indexOfGraphNode + 1;
                    int belowIndex = indexOfGraphNode + ((int) image.getWidth());

                    // If pixel to the right is a white pixel, link up with it
                    if ((rightIndex < allGraphNodes.size()) && (allGraphNodes.get(rightIndex)!=null)) {
                        allGraphNodes.get(rightIndex).connectToNodeUndirected(node, 1);
                    }
                    // If pixel below is white, link up with it
                    if ((belowIndex < allGraphNodes.size()) && (allGraphNodes.get(belowIndex)!=null)) {
                        allGraphNodes.get(belowIndex).connectToNodeUndirected(node, 1);
                    }

                }
            }
        }
    }
    
    // Method to initialize the Graph object
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Set the static graph object to this
        graph = this;
    }
}