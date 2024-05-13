package com.example.assignment2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    private Image displayImage;
    @FXML
    private ImageView imageView;

    @FXML
    public AnchorPane imageViewPane;

    private Graph graph = new Graph(); //Will be used to store the graph

    private Map<String, GraphNode<String>> nodes = new HashMap<>(); //Will be used to store all nodes

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

    private boolean isMapPopulated = false;

    public void initialize () throws FileNotFoundException {
        initialiseMap();
        initialiseGraphNodes();
    }

    public void initialiseMap() throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/main/java/com/example/Paris.JPG"));
        displayImage = image;
        imageView.setImage(image);
        // Check if the map is populated
        if (isMapPopulated) {
            System.out.println("Map already populated");
            return;
        }
        String csvData = "Eiffel Tower,201,345\n" +
                "Cimetière du Père-Lachaise,909,312\n" +
                "Panthéon,580,480\n"+
                "Arc de Triomphe,208,172\n"+
                "Cathédrale Notre-Dame de Paris,615,403\n"+
                "Sainte-Chapelle,579,376\n"+
                "Musée du Louvre,517,315\n"+
                "Place de la Concorde,401,264\n"+
                "Palais Garnier,487,190\n"+
                "Jardin du Luxembourg,513,477\n"+
                "Basilique du Sacré-Cœur,550,19\n"+
                "Champs-Élysées,385,256\n"+
                "Hôtel des Invalides,337,366\n"+
                "Les Catacombes de Paris,477,601\n"+
                "Institut du Monde Arabe,657,451\n"+
                "Muséum National d'Histoire Naturelle,667,507\n"+
                "Statue de la Liberté Paris,96,439\n";


        nodes = parseCSVData(csvData);
        // Iterate over the nodes and print their nodes
        for (Map.Entry<String, GraphNode<String>> entry : nodes.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println("\t" + entry.getValue().getName()+ " (" + entry.getValue().getX() + "," + entry.getValue().getY() + ")\n");
        }

        populateMenuButtons(nodes);


        //After filling the map, set the boolean to true.
        isMapPopulated = true;
    }

    public void initialiseGraphNodes() throws FileNotFoundException {
        graph.addGraphNodesToList();
    }

    private void populateMenuButtons(Map<String, GraphNode<String>> lines) {
        List<GraphNode<String>> uniqueGraphNodesList = new ArrayList<>();
        // Loop through each GraphNode<String> object in the lines Map
            // Loop through each GraphNode<String> object in the GraphNode<String>'s nodes
            // Add the node to the uniqueGraphNode<String>s set
            uniqueGraphNodesList.addAll(lines.values());
        // Convert the Set to a List

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

    public void drawLine() {
        GraphNode<String> startGraphNode=new GraphNode<>(selectedStartGraphNode.getName(), selectedStartGraphNode.getX(),selectedStartGraphNode.getY());
        Canvas canvas = new Canvas(displayImage.getWidth(), displayImage.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight());
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(5);
        gc.strokeLine(startGraphNode.getX(), startGraphNode.getY(), selectedDestinationGraphNode.getX(), selectedDestinationGraphNode.getY());
        WritableImage image = new WritableImage((int) displayImage.getWidth(), (int) displayImage.getHeight());
        canvas.snapshot(null, image);
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
}
