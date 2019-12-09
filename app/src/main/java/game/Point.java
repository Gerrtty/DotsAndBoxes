package game;

import javafx.scene.control.Button;

import java.util.ArrayList;

public class Point {

    private int x;
    private int y;

    private ArrayList<Point> listOfNeighbors;

    private Button button;

    public Point() {
        listOfNeighbors = new ArrayList<Point>();
    }

    public Point(int x, int y) {
        listOfNeighbors = new ArrayList<Point>();
        this.x = x;
        this.y = y;
    }

    public Point(Button button, int x, int y) {
        listOfNeighbors = new ArrayList<Point>();
        this.x = x;
        this.y = y;
        this.button = button;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void addNeighbor(Point point) {
        listOfNeighbors.add(point);
    }

    public ArrayList<Point> getListOfNeighbors() {
        return listOfNeighbors;
    }
}
