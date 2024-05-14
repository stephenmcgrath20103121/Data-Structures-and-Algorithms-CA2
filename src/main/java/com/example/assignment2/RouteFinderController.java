package com.example.assignment2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class RouteFinderController {
    @FXML
    private WritableImage displayImage;
    @FXML
    private ImageView imageView;

    @FXML
    public AnchorPane imageViewPane;

    private Graph graph = new Graph(); //Will be used to store the graph

    private Map<String, GraphNode<String>> landmarks = new HashMap<>(); //Will be used to store all landmarks

    @FXML
    public MenuButton startGraphNode=new MenuButton();
    @FXML
    public MenuButton destinationGraphNode=new MenuButton();
    private GraphNode<String> selectedStartGraphNode;
    private GraphNode<String> selectedDestinationGraphNode;
    private Circle startGraphNodeCircle;
    private Circle destinationGraphNodeCircle;

    @FXML
    public Button clearMap;
    @FXML
    public Button bfsButton;
    @FXML
    public Button dfsButton;
    @FXML
    public Button dijkstraButton;

    private boolean isMapPopulated = false;

    public void initialize () throws FileNotFoundException {
        initialiseMap();
    }

    public void initialiseMap() throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/main/java/com/example/Paris.JPG"));
        WritableImage writableImage=new WritableImage((int)image.getWidth(),(int)image.getHeight());
        PixelReader pixelReader= image.getPixelReader();
        PixelWriter pixelWriter= writableImage.getPixelWriter();
        for(int y=0;y<image.getHeight();y++){
            for(int x=0;x<image.getWidth();x++){
                Color color= pixelReader.getColor(x,y);
                pixelWriter.setColor(x,y,color);
            }
        }
        displayImage = writableImage;
        imageView.setImage(image);
        // Check if the map is populated
        if (isMapPopulated) {
            System.out.println("Map already populated");
            return;
        }
        String csvData = "Eiffel Tower,195,339\n" +
                "Cimetière du Père-Lachaise,878,338\n" +
                "Panthéon,580,484\n"+
                "Arc de Triomphe,202,168\n"+
                "Cathédrale Notre-Dame de Paris,615,403\n"+
                "Sainte-Chapelle,585,379\n"+
                "Musée du Louvre,520,303\n"+
                "Place de la Concorde,397,264\n"+
                "Palais Garnier,487,183\n"+
                "Jardin du Luxembourg,486,456\n"+
                "Basilique du Sacré-Cœur,564,20\n"+
                "Champs-Élysées,385,258\n"+
                "Hôtel des Invalides,321,352\n"+
                "Les Catacombes de Paris,480,600\n"+
                "Institut du Monde Arabe,657,447\n"+
                "Muséum National d'Histoire Naturelle,652,512\n"+
                "Statue de la Liberté Paris,96,436\n";


        landmarks = parseCSVData(csvData);
        initialiseGraphNodes();
        // Iterate over the landmarks and print their nodes
        for (Map.Entry<String, GraphNode<String>> entry : landmarks.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println("\t" + entry.getValue().getName()+ " (" + entry.getValue().getX() + "," + entry.getValue().getY() + ")\n");
        }

        populateMenuButtons(landmarks);


        //After filling the map, set the boolean to true.
        isMapPopulated = true;
    }

    public void initialiseGraphNodes() throws FileNotFoundException {
        List<GraphNode<String>> nodes=graph.addGraphNodesToList(landmarks);
        for (Map.Entry<String, GraphNode<String>> entry : landmarks.entrySet()) {
            String key=entry.getKey();
            GraphNode<String> currentNode=nodes.get(entry.getValue().getIndex());
            landmarks.get(key).setAdjList(currentNode.getAdjList());
        }
    }

    private void populateMenuButtons(Map<String, GraphNode<String>> lines) {
        List<GraphNode<String>> uniqueGraphNodesList = new ArrayList<>();
        uniqueGraphNodesList.addAll(lines.values());
        // Sort list of nodes alphabetically
        uniqueGraphNodesList.sort(Comparator.comparing(GraphNode::getName));
        startGraphNode.getItems().addAll(createGraphNodeMenuItems(uniqueGraphNodesList));
        destinationGraphNode.getItems().addAll(createGraphNodeMenuItems(uniqueGraphNodesList));
    }

    private List<MenuItem> createGraphNodeMenuItems(List<GraphNode<String>> nodes) {
        List<MenuItem> nodeMenuItems = new ArrayList<>();
        for (GraphNode<String> node : nodes) {
            MenuItem menuItem = new MenuItem(node.getName());
            menuItem.setOnAction(e -> handleMenuClick(e, node));
            nodeMenuItems.add(menuItem);
        }
        return nodeMenuItems;
    }

    private void handleMenuClick(ActionEvent event, GraphNode<String> node) {
        MenuItem clickedMenuItem = (MenuItem) event.getSource();
        MenuButton parentMenuButton = (MenuButton) clickedMenuItem.getParentPopup().getOwnerNode();

        // Set the selected node as the text of the parent MenuButton
        parentMenuButton.setText(node.getName());

        if (parentMenuButton == startGraphNode) {
            selectedStartGraphNode = node;
            drawFirstCircle(node);
        } else if (parentMenuButton == destinationGraphNode) {
            selectedDestinationGraphNode = node;
            drawDestinationCircle(node);
        }
    }


    private Map<String, GraphNode<String>> parseCSVData(String csvData) {
        Map<String, GraphNode<String>> nodes = new HashMap<>();
        // Split the csvData to get each node
        String[] csvLines = csvData.split("\n");

        // Loop through each node in the csv string
        for (String node : csvLines) {
            // Split the node by a comma
            String[] values = node.split(",");

            // Check for correct values
            if (values.length != 3) {
                System.err.println("Invalid node in CSV: " + node);
                continue;
            }

            // Get values
            String nodeName = values[0].trim();
            int x = Integer.parseInt(values[1].trim());
            int y = Integer.parseInt(values[2].trim());

            // Create or retrieve the GraphNode<String> object
            GraphNode<String> nodeObj = nodes.get(nodeName);
            if (nodeObj == null) {
                nodeObj = new GraphNode<>(nodeName, x, y);
                nodes.put(nodeName, nodeObj);
            }
        }

        return nodes;
    }

    public void drawLine(List<GraphNode<String>> nodeList) {
        WritableImage image=new WritableImage((int)displayImage.getWidth(),(int)displayImage.getHeight());
        PixelReader pixelReader= displayImage.getPixelReader();
        PixelWriter pixelWriter= image.getPixelWriter();
        for(int y=0;y<displayImage.getHeight();y++){
            for(int x=0;x<displayImage.getWidth();x++){
                Color color= pixelReader.getColor(x,y);
                pixelWriter.setColor(x,y,color);
            }
        }

        for(int i=0;i< nodeList.size();i++){
            pixelWriter.setColor(nodeList.get(i).getX(),nodeList.get(i).getY(),Color.NAVY);
        }
        imageView.setImage(image);
    }

    public void clearMap() throws FileNotFoundException {
        //resets the image back to the original
        Image image = new Image(new FileInputStream("src/main/java/com/example/Paris.JPG"));
        imageView.setImage(image);
    }

    private Point2D calculateActualCoordinates(GraphNode<String> node) {
        double scaleX = imageView.getBoundsInLocal().getWidth() / imageView.getImage().getWidth();
        double scaleY = imageView.getBoundsInLocal().getHeight() / imageView.getImage().getHeight();

        double actualX = node.getX() * scaleX;
        double actualY = node.getY() * scaleY;

        return new Point2D(actualX, actualY);
    }

    private void createGraphNodeCircle(Circle innerCircle, GraphNode<String> node, Color color) {
        // Calculate the coordinates of the node on the mapPane
        Point2D actualCoordinates = calculateActualCoordinates(node);

        // Set the radius and centre of the circle
        innerCircle.setCenterX(actualCoordinates.getX());
        innerCircle.setCenterY(actualCoordinates.getY());
        innerCircle.setFill(color);
        innerCircle.setRadius(5);
    }

    private void drawFirstCircle(GraphNode<String> node) {
        // Remove any existing circle for the start node
        if (startGraphNodeCircle != null) {
            imageViewPane.getChildren().removeAll(startGraphNodeCircle);
        }
        // Adds a new circle on the node to the map
        startGraphNodeCircle = new Circle();
        createGraphNodeCircle(startGraphNodeCircle, node, Color.RED);
        imageViewPane.getChildren().addAll(startGraphNodeCircle);
    }

    // This method draws an aqua circle on the selected destination
    private void drawDestinationCircle(GraphNode<String> node) {
        // Remove any existing circle for the destination node
        if (destinationGraphNodeCircle != null) {
            imageViewPane.getChildren().removeAll(destinationGraphNodeCircle);
        }
        // Adds a new circle on the node to the map
        destinationGraphNodeCircle = new Circle();
        createGraphNodeCircle(destinationGraphNodeCircle, node, Color.BLUE);
        imageViewPane.getChildren().addAll(destinationGraphNodeCircle);
    }

    public void processBfs(){
        List<GraphNode<String>> pathNodeList=Algorithms.bfs(selectedStartGraphNode,selectedDestinationGraphNode);
        drawLine(pathNodeList);
    }

    public void processDfs(){
        List<GraphNode<String>> pathNodeList=Algorithms.dfs(selectedStartGraphNode,selectedDestinationGraphNode);
        drawLine(pathNodeList);
    }

    public void processDijkstra(){
        List<GraphNode<String>> pathNodeList=Algorithms.Dijkstra(graph.getAllGraphNodes(),selectedStartGraphNode,selectedDestinationGraphNode);
        drawLine(pathNodeList);
    }
}
