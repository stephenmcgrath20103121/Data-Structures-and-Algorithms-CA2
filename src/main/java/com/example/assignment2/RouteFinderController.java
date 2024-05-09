package com.example.assignment2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
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

    private Map<String, Landmark> landmarks = new HashMap<>(); //Will be used to store all landmarks

    @FXML
    public MenuButton startLandmark;
    @FXML
    public MenuButton destinationLandmark;
    private Landmark selectedStartLandmark;
    private Landmark selectedDestinationLandmark;
    private Circle startLandmarkCircle;
    private Circle destinationLandmarkCircle;

    @FXML
    public Button clearMap;

    @FXML
    public Button bfsButton;

    @FXML
    public ListView routeOutput;

    private boolean isMapPopulated = false;

    //private Landmark selectedWaypointLandmark;

    //@FXML
    //public MenuButton waypointLandmark;

    public void initialize () throws FileNotFoundException {
        initialiseMap();
        //addLandmarkLinks();
        //processBwImage();
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
        String csvData = "Quai Jacques Chirac,Eiffel Tower,201,345\n" +
                "Bd. de Ménilmontant,Cimetière du Père-Lachaise,909,312\n" +
                "Pl. du Panthéon,Panthéon,580,480\n"+
                "Av. des Champs-Élysées,Arc de Triomphe,208,172\n"+
                "Rue du Cloitre Notre Dame,Cathédrale Notre-Dame de Paris,615,403\n"+
                "Bd. du Palais,Sainte-Chapelle,579,376\n"+
                "Quoi du Louvre,Musée du Louvre,517,315\n"+
                "Pl. de la Concorde,Place de la Concorde,401,264\n"+
                "Rue Auber,Palais Garnier,487,190\n"+
                "Bd. Saint-Michel,Jardin du Luxembourg,513,477\n"+
                "Rue de la Cardinal Guibert,Basilique du Sacré-Cœur,550,19\n"+
                "Av. des Champs-Élysées,Champs-Élysées,385,256\n"+
                "Bd. des Invalides,Hôtel des Invalides,337,366\n"+
                "Pl. Denfert-Rochereau,Les Catacombes de Paris,477,601\n"+
                "Quai Saint-Bernard,Institut du Monde Arabe,657,451\n"+
                "Rue Cuvier,Muséum National d'Histoire Naturelle,667,507\n"+
                "Pont de Grenelle,Statue de la Liberté Paris,96,439\n";


        Map<String, Road> roads = parseCSVData(csvData);
        // Iterate over the roads and print their landmarks
        for (Map.Entry<String, Road> entry : roads.entrySet()) {
            System.out.println(entry.getKey());
            for (Landmark landmark : entry.getValue().getLandmarks()) {
                System.out.println("\t" + landmark.getLandmarkName() + " (" + landmark.getX() + "," + landmark.getY() + ")");
            }
            System.out.println("\n");
        }

        populateMenuButtons(roads);


        //After filling the map, set the boolean to true.
        isMapPopulated = true;
    }

    private void populateMenuButtons(Map<String, Road> lines) {
        Set<Landmark> uniqueLandmarksSet = new HashSet<>();
        // Loop through each Road object in the lines Map
        for (Road line : lines.values()) {
            // Loop through each Landmark object in the Road's landmarks
            // Add the landmark to the uniqueLandmarks set
            uniqueLandmarksSet.addAll(line.getLandmarks());
        }
        // Convert the Set to a List
        List<Landmark> uniqueLandmarksList = new ArrayList<>(uniqueLandmarksSet);
        // Sort list of landmarks alphabetically
        uniqueLandmarksList.sort(Comparator.comparing(Landmark::getLandmarkName));
        startLandmark.getItems().addAll(createLandmarkMenuItems(uniqueLandmarksList));
        destinationLandmark.getItems().addAll(createLandmarkMenuItems(uniqueLandmarksList));
    }

    private List<MenuItem> createLandmarkMenuItems(List<Landmark> landmarks) {
        List<MenuItem> landmarkMenuItems = new ArrayList<>();
        for (Landmark landmark : landmarks) {
            MenuItem menuItem = new MenuItem(landmark.getLandmarkName());
            menuItem.setOnAction(e -> handleMenuClick(e, landmark));
            landmarkMenuItems.add(menuItem);
        }
        return landmarkMenuItems;
    }

    private void handleMenuClick(ActionEvent event, Landmark landmark) {
        MenuItem clickedMenuItem = (MenuItem) event.getSource();
        MenuButton parentMenuButton = (MenuButton) clickedMenuItem.getParentPopup().getOwnerNode();

        // Set the selected landmark as the text of the parent MenuButton
        parentMenuButton.setText(landmark.getLandmarkName());

        if (parentMenuButton == startLandmark) {
            selectedStartLandmark = landmark;
            drawFirstCircle(landmark);
        } else if (parentMenuButton == destinationLandmark) {
            selectedDestinationLandmark = landmark;
            drawDestinationCircle(landmark);
        }
    }


    private Map<String, Road> parseCSVData(String csvData) {
        Map<String, Road> roads = new HashMap<>();
        // Split the csvData to get each road
        String[] csvLines = csvData.split("\n");

        // Loop through each road in the csv string
        for (String road : csvLines) {
            // Split the road by a comma
            String[] values = road.split(",");

            // Check for correct values
            if (values.length != 4) {
                System.err.println("Invalid road in CSV: " + road);
                continue;
            }

            // Get values
            String roadName = values[0].trim();
            String landmarkName = values[1].trim();
            double x = Double.parseDouble(values[2].trim());
            double y = Double.parseDouble(values[3].trim());

            // Create or retrieve the Road object
            Road roadObj = roads.get(roadName);
            if (roadObj == null) {
                roadObj = new Road(roadName);
                roads.put(roadName, roadObj);
            }

            // Create or retrieve the Landmark object
            Landmark landmarkObj = landmarks.get(landmarkName);
            if (landmarkObj == null) {
                landmarkObj = new Landmark(landmarkName, x, y);
                landmarks.put(landmarkName, landmarkObj);
            }

            // Add the landmark to the road
            roadObj.addLandmark(landmarkObj);


            // Get the previous landmark on the road, if it exists
            List<Landmark> currentLineLandmarks = roadObj.getLandmarks();
            if (currentLineLandmarks.size() > 1) {
                Landmark previousLandmark = currentLineLandmarks.get(currentLineLandmarks.size() - 2);

                // Calculate the distance between the current and previous landmarks
                double distance = Math.sqrt(Math.pow(landmarkObj.getX() - previousLandmark.getX(), 2)
                        + Math.pow(landmarkObj.getY() - previousLandmark.getY(), 2));
                // Print the distances
                System.out.println("Calculating distance between " + previousLandmark.getLandmarkName() + " and " + landmarkObj.getLandmarkName());
                System.out.println("Previous landmark coordinates: " + previousLandmark.getX() + ", " + previousLandmark.getY());
                System.out.println("Current landmark coordinates: " + landmarkObj.getX() + ", " + landmarkObj.getY());
                System.out.println("Calculated distance: " + distance);

                // Add the current landmark as a neighbor to the previous landmark, if they are not already neighbors
                if (!previousLandmark.getNeighborLandmarks().containsKey(landmarkObj)) {
                    previousLandmark.addNeighbor(landmarkObj);
                    System.out.println("Added " + landmarkObj.getLandmarkName() + " as a neighbor to " + previousLandmark.getLandmarkName());
                }

                // Add the previous landmark as a neighbor to the current landmark, if they are not already neighbors
                if (!landmarkObj.getNeighborLandmarks().containsKey(previousLandmark)) {
                    landmarkObj.addNeighbor(previousLandmark);
                    System.out.println("Added " + previousLandmark.getLandmarkName() + " as a neighbor to " + landmarkObj.getLandmarkName());
                }
            }
        }

        return roads;
    }

    public void drawLine() {
        Canvas canvas = new Canvas(displayImage.getWidth(), displayImage.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight());
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(5);
        gc.strokeLine(selectedStartLandmark.getX(),selectedStartLandmark.getY(), selectedDestinationLandmark.getX(), selectedDestinationLandmark.getY());
        WritableImage image = new WritableImage((int) displayImage.getWidth(), (int) displayImage.getHeight());
        canvas.snapshot(null, image);
        imageView.setImage(image);
    }

    public void clearMap() throws FileNotFoundException {
        //resets the image back to the original
        Image image = new Image(new FileInputStream("src/main/java/com/example/Paris.JPG"));
        imageView.setImage(image);
    }

    private Point2D calculateActualCoordinates(Landmark landmark) {
        double scaleX = imageView.getBoundsInLocal().getWidth() / imageView.getImage().getWidth();
        double scaleY = imageView.getBoundsInLocal().getHeight() / imageView.getImage().getHeight();

        double actualX = landmark.getX() * scaleX;
        double actualY = landmark.getY() * scaleY;

        return new Point2D(actualX, actualY);
    }

    private void createLandmarkCircle(Circle innerCircle, Landmark landmark, Color color) {
        // Calculate the coordinates of the landmark on the mapPane
        Point2D actualCoordinates = calculateActualCoordinates(landmark);

        // Set the radius and centre of the circle
        innerCircle.setCenterX(actualCoordinates.getX());
        innerCircle.setCenterY(actualCoordinates.getY());
        innerCircle.setFill(color);
        innerCircle.setRadius(5);
    }

    private void drawFirstCircle(Landmark landmark) {
        // Remove any existing circle for the start landmark
        if (startLandmarkCircle != null) {
            imageViewPane.getChildren().removeAll(startLandmarkCircle);
        }
        // Adds a new circle on the landmark to the map
        startLandmarkCircle = new Circle();
        createLandmarkCircle(startLandmarkCircle, landmark, Color.RED);
        imageViewPane.getChildren().addAll(startLandmarkCircle);
    }

    // This method draws an aqua circle on the selected destination
    private void drawDestinationCircle(Landmark landmark) {
        // Remove any existing circle for the destination landmark
        if (destinationLandmarkCircle != null) {
            imageViewPane.getChildren().removeAll(destinationLandmarkCircle);
        }
        // Adds a new circle on the landmark to the map
        destinationLandmarkCircle = new Circle();
        createLandmarkCircle(destinationLandmarkCircle, landmark, Color.BLUE);
        imageViewPane.getChildren().addAll(destinationLandmarkCircle);
    }




























    /*
    public void bfsSearch(ActionEvent actionEvent) {
        // Determine the shortest route between the selected starting and destination landmarks using the Graph's findShortestPath method
        Path shortestRoute = graph.bfsAlgorithm(selectedStartLandmark, selectedDestinationLandmark);

        // Display an error message if no path is found
        if (shortestRoute == null) {
            System.out.println("No path found between the selected landmarks");
        } else {
            // If a path is found, print the path and the number of stops
            List<Landmark> path = shortestRoute.getPath();
            System.out.println(path.get(0).getLandmarkName() + " to " + path.get(path.size()-1).getLandmarkName());

            // Initialize variables to track the current and next lines
            Road currentLine = null;
            Road nextLine = null;

            // Initialize lists to store the landmark path and line changes
            List<String> landmarkPath = new ArrayList<>();
            List<String> lineChanges = new ArrayList<>();

            // Iterate over each landmark in the path
            for (int i = 0; i < path.size() - 1; i++) {
                Landmark landmark1 = path.get(i);
                Landmark landmark2 = path.get(i + 1);

                // Get the common lines between the two landmarks
                List<Road> commonLines = getCommonLines(landmark1, landmark2);

                // If there is no current line or the current line is not in the list of common lines,
                // update the current line and print it
                if (currentLine == null || !commonLines.contains(currentLine)) {
                    // Update the current line (select the first one from the list for simplicity)
                    nextLine = commonLines.get(0);

                    // Add the line change to the list
                    if (i != 0) { // Avoid adding a line change for the first landmark
                        lineChanges.add(currentLine.getLineName() + " to " + landmark1.getLandmarkName());
                        lineChanges.add(nextLine.getLineName());
                    } else { // Handle the initial line at the starting landmark
                        lineChanges.add(nextLine.getLineName());
                    }
                    currentLine = nextLine;
                }

                // Add the landmark to the landmark path list
                landmarkPath.add(landmark1.getLandmarkName());
            }

            // Add the final landmark to the landmark path list
            landmarkPath.add(path.get(path.size() - 1).getLandmarkName());

            // Add the final line change to the list
            lineChanges.add("Take the " + currentLine.getLineName() + " to " + path.get(path.size()-1).getLandmarkName());

            // Print the landmark path
            System.out.println("Shortest path: ");
            System.out.println(String.join(" -> ", landmarkPath));

            // Print the line changes
            System.out.println(String.join("\n", lineChanges));
            drawLine();

            // Update the ListView
            List<String> outputLines = new ArrayList<>();

            // Add the BFS header indicating the starting and destination landmarks
            outputLines.add("\nBFS: " + selectedStartLandmark.getLandmarkName() + " to " + selectedDestinationLandmark.getLandmarkName());
            // Iterate over the landmarks in the path and add appropriate markers
            for (int i = 0; i < path.size(); i++) {
                if (i == 0 || i == path.size() - 1) {
                    // Add a special marker for the first and last landmarks
                    outputLines.add("** " + path.get(i).getLandmarkName() + " **");
                } else {
                    // Add a downward arrow marker for intermediate landmarks
                    outputLines.add("-- " + path.get(i).getLandmarkName());
                }
            }

            // Add the line directions header and the list of line changes
            outputLines.add("\nRoad Directions:");
            outputLines.addAll(lineChanges);

            // Create an observable list and populate it with the output lines
            ObservableList<String> items = FXCollections.observableArrayList(outputLines);
            routeOutput.setItems(items);
        }
    }

    public List<Road> getCommonLines(Landmark station1, Landmark station2) {
        // Initialize a new list with the lines of the first station
        List<Road> commonLines = new ArrayList<>(station1.getLines());

        // Keep only the lines that are also present in the lines of the second station
        commonLines.retainAll(station2.getLines());

        // Return the list of shared lines
        return commonLines;
    }


    public static void traverseGraphDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered){
        System.out.println(from.data);
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(from);
        for(GraphLink adjLink : from.adjList)
            if(!encountered.contains(adjLink.destNode)) traverseGraphDepthFirst(adjLink.destNode,encountered);
    }

    public static void traverseGraphDepthFirstShowingTotalCost(GraphNode<?> from, List<GraphNode<?>> encountered, int totalCost ){
        System.out.println(from.data+" (Total cost of reaching node: "+ totalCost +")");
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(from);
        Collections.sort(from.adjList,(a, b)->a.cost-b.cost);
        for(GraphLink adjLink : from.adjList)
            if(!encountered.contains(adjLink.destNode))
                traverseGraphDepthFirstShowingTotalCost(adjLink.destNode,encountered, totalCost+adjLink.cost );
    }

    public static void traverseGraphBreadthFirst(List<GraphNode<?>> agenda, List<GraphNode<?>> encountered ){
        if(agenda.isEmpty()) return;
        GraphNode<?> next=agenda.remove(0);
        System.out.println(next.data);
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(next);
        for(GraphLink adjLink : next.adjList)
            if(!encountered.contains(adjLink.destNode) && !agenda.contains(adjLink.destNode)) agenda.add(adjLink.destNode); //Add children to
//end of agenda
        traverseGraphBreadthFirst(agenda, encountered );
    }

    public static <T> GraphNode<?> searchGraphDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, T lookingfor ){
        if(from.data.equals(lookingfor)) return from;
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(from);
        for(GraphLink adjLink : from.adjList)
            if(!encountered.contains(adjLink.destNode)) {
                GraphNode<?> result=searchGraphDepthFirst(adjLink.destNode,encountered, lookingfor );
                if(result!=null) return result;
            }
        return null;
    }

    public static <T> List<GraphNode<?>> findPathDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, T lookingfor){
        List<GraphNode<?>> result;
        if(from.data.equals(lookingfor)) {
            result=new ArrayList<>();
            result.add(from);
            return result;
        }
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(from);
        for(GraphLink adjLink : from.adjList)
            if(!encountered.contains(adjLink.destNode)) {
                result=findPathDepthFirst(adjLink.destNode,encountered,lookingfor);
                if(result!=null) {
                    result.add(0,from);
                    return result;
                }
            }
        return null;
    }

    public static <T> List<List<GraphNode<?>>> findAllPathsDepthFirst(GraphNode<?> from, List<GraphNode<?>> encountered, T lookingfor){
        List<List<GraphNode<?>>> result=null, temp2;
        if(from.data.equals(lookingfor)) {
            List<GraphNode<?>> temp=new ArrayList<>();
            temp.add(from);
            result=new ArrayList<>();
            result.add(temp);
            return result;
        }
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(from);
        for(GraphLink adjLink : from.adjList){
            if(!encountered.contains(adjLink.destNode)) {
                temp2=findAllPathsDepthFirst(adjLink.destNode,new ArrayList<>(encountered),lookingfor);
                if(temp2!=null) {
                    for(List<GraphNode<?>> x : temp2)
                        x.add(0,from);
                    if(result==null) result=temp2;
                    else result.addAll(temp2);
                }
            }
        }
        return result;
    }

    public static <T> List<GraphNode<?>> findPathBreadthFirst(GraphNode<?> startNode, T lookingfor){
        List<List<GraphNode<?>>> agenda=new ArrayList<>();
        List<GraphNode<?>> firstAgendaPath=new ArrayList<>(),resultPath;
        firstAgendaPath.add(startNode);
        agenda.add(firstAgendaPath);
        resultPath=findPathBreadthFirst(agenda,null,lookingfor);
        Collections.reverse(resultPath);
        return resultPath;
    }

    public static <T> List<GraphNode<?>> findPathBreadthFirst(List<List<GraphNode<?>>> agenda, List<GraphNode<?>> encountered, T lookingfor){
        if(agenda.isEmpty()) return null;
        List<GraphNode<?>> nextPath=agenda.remove(0);
        GraphNode<?> currentNode=nextPath.get(0);
        if(currentNode.data.equals(lookingfor)) return nextPath;
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(currentNode);
        for(GraphLink adjLink : currentNode.adjList)
            if(!encountered.contains(adjLink.destNode)) {
                List<GraphNode<?>> newPath=new ArrayList<>(nextPath);
                newPath.add(0,adjLink.destNode);
                agenda.add(newPath);
            }
        return findPathBreadthFirst(agenda,encountered,lookingfor);
    }

    public static <T> CostedPath searchGraphDepthFirstCheapestPath(GraphNode<?> from, List<GraphNode<?>> encountered, int totalCost, T lookingfor){
        if(from.data.equals(lookingfor)){
            CostedPath cp=new CostedPath();
            cp.pathList.add(from);
            cp.pathCost=totalCost;
            return cp;
        }
        if(encountered==null) encountered=new ArrayList<>();
        encountered.add(from);
        List<CostedPath> allPaths=new ArrayList<>();
        for(GraphLink adjLink : from.adjList)
            if(!encountered.contains(adjLink.destNode))
            {
                CostedPath temp=searchGraphDepthFirstCheapestPath(adjLink.destNode,new ArrayList<>(encountered),
                        totalCost+adjLink.cost,lookingfor);
                if(temp==null) continue;
                temp.pathList.add(0,from);
                allPaths.add(temp);
            }
        return allPaths.isEmpty() ? null : Collections.min(allPaths, (p1,p2)->p1.pathCost-p2.pathCost);
    }

    public static <T> CostedPath findCheapestPathDijkstra(GraphNode<?> startNode, T lookingfor){
        CostedPath cp=new CostedPath();
        List<GraphNode<?>> encountered=new ArrayList<>(), unencountered=new ArrayList<>();
        startNode.nodeValue=0;
        unencountered.add(startNode);
        GraphNode<?> currentNode;
        do{
            currentNode=unencountered.remove(0);
            encountered.add(currentNode);
            if(currentNode.data.equals(lookingfor)){
                cp.pathList.add(currentNode);
                cp.pathCost=currentNode.nodeValue;
                while(currentNode!=startNode) {
                    boolean foundPrevPathNode=false;
                    for(GraphNode<?> n : encountered) {
                        for(GraphLink e : n.adjList)
                            if(e.destNode==currentNode && currentNode.nodeValue-e.cost==n.nodeValue){
                                cp.pathList.add(0,n);
                                currentNode=n;
                                foundPrevPathNode=true;
                                break;
                            }
                        if(foundPrevPathNode) break;
                    }
                }
                for(GraphNode<?> n : encountered) n.nodeValue=Integer.MAX_VALUE;
                for(GraphNode<?> n : unencountered) n.nodeValue=Integer.MAX_VALUE;
                return cp;
            }
            for(GraphLink e : currentNode.adjList)
                if(!encountered.contains(e.destNode)) {
                    e.destNode.nodeValue=Integer.min(e.destNode.nodeValue, currentNode.nodeValue+e.cost);
                    if(!unencountered.contains(e.destNode)) unencountered.add(e.destNode);
                }
            Collections.sort(unencountered,(n1,n2)->n1.nodeValue-n2.nodeValue);
        }while(!unencountered.isEmpty());
        return null;
    }
     */
}
