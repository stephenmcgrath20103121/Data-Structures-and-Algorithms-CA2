package com.example.assignment2;

import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteFinderController {
    @FXML
    private Image displayImage;
    @FXML
    private ImageView imageView;

    @FXML
    protected void openFileExplorer(){
        FileChooser fileChooser= new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.png"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            displayImage = image;
            imageView.setImage(image);
        }
    }

    @FXML
    private void mouseClick(MouseEvent event){
        if (displayImage != null){
            double x = event.getX();
            double y = event.getY();

            int roundedX = (int) (x/imageView.getBoundsInLocal().getWidth() * displayImage.getWidth());
            int roundedY = (int) (y/imageView.getBoundsInLocal().getHeight() * displayImage.getHeight());

            int imageWidth = (int) displayImage.getWidth();
            int imageHeight = (int) displayImage.getHeight();

            if(roundedX >= 0 && roundedX < imageWidth && roundedY >= 0 && roundedY < imageHeight){
                Color clickedPixel = displayImage.getPixelReader().getColor(roundedX,roundedY);
                Image bwImage = convertImageToBlackAndWhite(clickedPixel, displayImage);
                //add some methods for what to do once bw conversion is complete
            }
        }
    }

    @FXML
    protected Image convertImageToBlackAndWhite(Color color, Image image){

        PixelReader imageReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage writableImage = new WritableImage(width,height);
        PixelWriter writableImageWriter = writableImage.getPixelWriter();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color currentColor=imageReader.getColor(x,y);

                if ((Math.abs(color.getRed()-currentColor.getRed())<0.11)&&(Math.abs(color.getBlue()-currentColor.getBlue())<0.11)&&(Math.abs(color.getGreen()-currentColor.getGreen())<.11)){
                    writableImageWriter.setColor(x,y,Color.WHITE);
                }else{
                    writableImageWriter.setColor(x,y,Color.BLACK);
                }
            }
        }

        return writableImage;
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
}