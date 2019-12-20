package display.windows;

import client.Client;
import game.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utills.JSON;

import java.util.ArrayList;
import java.util.HashMap;

public class Field extends Application {
    private static Field field;
    private static boolean isVisible = true;

    private Field() {

    }

    public static Field getField() {
        if(field == null) {
            field = new Field();
        }

        return field;
    }

    @Override
    public void start(Stage stage) {

        GridPane gridPane = generateField(Main.game.getHeight(), Main.game.getWidth());

        Scene scene = new Scene(gridPane);
        stage.setScene(scene);

        // size of the window
        stage.setWidth(600);
        stage.setHeight(500);

        stage.setOnCloseRequest(event -> System.exit(0));

        stage.show();

    }

    private GridPane generateField(int height, int width) {

        int h = height * 2 + 1;
        int w = width * 2 + 1;

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setGridLinesVisible(true);

        gridPane.setStyle("-fx-background-color: #09095f");

        setColumns(gridPane, w);
        setRows(gridPane, h);

        ArrayList<Point> points = setPoints(h, w);

        // HashMap<Point, ArrayList<Point>> pointAndNeighbors = new Point().setNeighbors(points);

        HashMap<Point, ToggleButton> pointToButton = setButtons(gridPane, points);

        ArrayList<MyLine> listOfAllLines = new MyLine().initAllLines(h, w);

        Main.game.setLines(listOfAllLines);

        ArrayList<Square> squares = initAllSquares(h, w);

        System.out.println(Main.game.getSquares().size());

        setActions(gridPane, listOfAllLines, squares, pointToButton);

        return gridPane;
    }

    private ArrayList<Square> initAllSquares(int h, int w) {
        ArrayList<Square> squares = new ArrayList<>();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if(i % 2 != 0 && j % 2 != 0) {
                    Square square = new Square(i, j);
                    square.initLines();
                    squares.add(square);
                }
            }
        }

        Main.game.setSquares(squares);

        return squares;
    }

    private void setColumns(GridPane gridPane, int w) {

        for (int i = 0; i < w; i++) {
            if(i % 2 != 0) {
                gridPane.getColumnConstraints().add(new ColumnConstraints(100));
            }
            else {
                gridPane.getColumnConstraints().add(new ColumnConstraints(20));
            }
        }
    }

    private void setRows(GridPane gridPane, int h) {

        for (int i = 0; i < h; i++) {
            if(i % 2 != 0) {
                gridPane.getRowConstraints().add(new RowConstraints(100));
            }
            else {
                gridPane.getRowConstraints().add(new RowConstraints(20));
            }
        }

    }

    private ToggleButton createButton() {

        ToggleButton button = new ToggleButton();

        button.setStyle("-fx-base: #ffffff");
        double r = 10;
        button.setShape(new Circle(r));
        button.setMinSize(2 * r, 2 * r);
        button.setMaxSize(2 * r, 2 * r);

        return button;

    }

    private HashMap<Point, ToggleButton> setButtons(GridPane gridPane, ArrayList<Point> points) {

        HashMap<Point, ToggleButton> buttons = new HashMap<>();

        points.forEach(point -> {
            ToggleButton button = createButton();
            buttons.put(point, button);
            gridPane.add(button, point.getX(), point.getY());
        });

        return buttons;
    }

    private void setActions(GridPane gridPane, ArrayList<MyLine> allLines,
                            ArrayList<Square> squares, HashMap<Point, ToggleButton> map) {

        map.forEach((point, toggleButton) -> toggleButton.setOnAction(event -> {

            if(isVisible) {
                point.setWaiting(true);

                ArrayList<Point> waitingPoints = new ArrayList<>();

                map.forEach((waitingPoint, button) -> {

                    if(waitingPoint.isWaiting()) {
                        waitingPoints.add(waitingPoint);
                    }
                });


                if(waitingPoints.size() == 2) {
                    System.out.println("Player drawing line");
                    drawLine(gridPane, allLines, squares, waitingPoints.get(0), waitingPoints.get(1));
                    waitingPoints.get(0).setWaiting(false);
                    waitingPoints.get(1).setWaiting(false);
                    isVisible = false;

                    String s = new JSON<Game>().createJSON(Main.game);

                    Client.sendMessage(s);
                }
            }

            else {
                System.out.println("Computer move");
            }

        }));

    }

    private void drawLine(GridPane gridPane, ArrayList<MyLine> allLines,
                          ArrayList<Square> squares, Point p1, Point p2) {

        MyLine myLine = new MyLine(p1, p2);

        allLines.forEach(line -> {
            if(line.equals(myLine)) {
                line.setOnField(true);
                System.out.println("Line set of field = true");
                squares.forEach(square -> square.getLines().forEach(l -> {
                    if(l.equals(line)) {
                        l.setOnField(true);
                    }
                }));
            }
        });

        Line line = new Line();
        line.setStrokeWidth(5);
        line.setStroke(Color.WHITE);

        if(myLine.getPosition() == null) {
            System.out.println("Error in drawLine method");
        }

        else if(myLine.getPosition().equals(LinePosition.VERTICAL)) {
            line.setEndY(100);
            line.setTranslateX(8);

        }

        else {
            line.setEndX(100);
        }

        gridPane.add(line, myLine.getX(), myLine.getY());

        squares.forEach(square -> {
            if(square.isSet()) {
                System.out.println("Square is set");
                square.setLetter(Main.game.getPlayer().getLetter());
                gridPane.add(setText(square.getLetter()), square.getX(), square.getY());
            }
        });

    }

    private Text setText(String letter) {
        Text text = new Text(letter);
        text.setTranslateX(30);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-size: 60px;");

        return text;
    }


    public static void setIsVisible(boolean isVisible) {
        Field.isVisible = isVisible;
    }

    private ArrayList<Point> setPoints(int h, int w) {

        ArrayList<Point> list = new ArrayList<>();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if(i % 2 == 0 && j % 2 == 0) {
                    list.add(new Point(i, j));

                }
            }
        }

        Main.game.setPoints(list);

        return list;

    }

    //    private void makeGreen(ToggleButton button) {
//        button.setStyle("-fx-base: #31c318");
//    }

//    private void makeWhite(ToggleButton button) {
//        button.setStyle("-fx-base: #ffffff");
//    }

    // TODO
//    private void setNeighbors(Point point) {
//
//        ArrayList<Point> neighbors = point.getNeighbors();
//
//        neighbors.forEach(neighbor -> map.forEach((point1, toggleButton1) -> {
//            if(point1.equals(neighbor)) {
//                makeGreen(toggleButton1);
//            }
//        }));
//
//    }
}
