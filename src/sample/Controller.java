package sample;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Controller {

    private long record;
    private long time;
    private List<Tile> tileList;
    private Tile buffer;
    private Rectangle rectangle;
    private boolean canMove;
    @FXML
    private AnchorPane panel;
    @FXML
    private Label recordLabel;
    @FXML
    private Label label;

    @FXML
    private void initialize() {

        buffer = null;
        canMove = false;
        rectangle = new Rectangle (114,114, Color.TRANSPARENT);
        tileList = new ArrayList<Tile>();

        Image image = new Image("picture0.png",300,300,false,true);

        Tile tile;
        record = 999999999999999999L;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("src/record.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Nie ma pliku");
            try{
                PrintWriter out = new PrintWriter("src/record.txt");
                out.print(record);
                out.close();
            } catch (FileNotFoundException eve) {
                System.out.println("Err");
            }
        }
        if(scanner != null) {
            if(new File("src/record.txt").length() != 0)
                 record = scanner.nextLong();
        }

        long second = TimeUnit.MILLISECONDS.toSeconds(record);
        long minute = TimeUnit.MILLISECONDS.toMinutes(record);
        long hour = TimeUnit.MILLISECONDS.toHours(record);
        long millis = record - TimeUnit.SECONDS.toMillis(second);

        recordLabel.setText(String.format("%02d:%02d:%02d:%d", hour, minute, second, millis));


        int count;
        for(int i = 0; i < 3; i++) {
            count = i*3;
            for (int j = 0; j < 3; j++) {
                tile = new Tile(100, 100, new WritableImage(image.getPixelReader(),100*j, 100*i,100,100),count+j );
                tile.setLayoutY(14 * (1+i) + 100 * i);
                tile.setLayoutX(14 * (1+j) + 100 * j);
                tileList.add(tile);

            }
        }
        panel.getChildren().add(rectangle);
        panel.getChildren().addAll(tileList);

    }


    @FXML
    private void handleStartBtnAction()
    {
        time = 0;
        canMove = true;
        Collections.shuffle(tileList);
        for(int i = 0; i< 9; i++)
        {
            tileList.get(i).setActivenumber(i);
        }

        int count;
        for(int i = 0; i < 3; i++) {
            count = i*3;
            for (int j = 0; j < 3; j++) {
                tileList.get(count+j).setLayoutY(14 * (1+i) + 100 * i);
                tileList.get(count+j).setLayoutX(14 * (1+j) + 100 * j);
            }
        }

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(10),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent ae) {
                        updateTime();
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        for(Tile tileN: tileList)
        {
            tileN.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(canMove && clicked(tileN)) {
                        timeline.stop();
                        if(time < record)
                        {
                            try {
                                record = time-10;
                                setTimeLabel(recordLabel,record);
                                PrintWriter out = new PrintWriter("src/record.txt");
                                out.print(record);
                                out.close();
                            } catch (FileNotFoundException e) {
                                System.out.println("Err");
                            }
                        }
                        win();
                        canMove=false;

                    }
                }
            });
        }
    }

    private boolean clicked(Tile tileN)
    {
        boolean flag = false;
        if(buffer == null)
        {
            buffer = tileN;
            rectangle.setLayoutY(tileN.getLayoutY()-7);
            rectangle.setLayoutX(tileN.getLayoutX()-7);
            rectangle.setFill(Color.RED);
        }
        else
        {
            rectangle.setFill(Color.TRANSPARENT);
            int spr = buffer.getActivenumber() - tileN.getActivenumber();
            if(spr == -3 || spr == 3 || spr == 1 || spr == -1)
                flag = swap(buffer.getActivenumber(),tileN.getActivenumber());
            buffer = null;
        }
        return flag;

    }
    private void updateTime() {
        setTimeLabel(label,time);
        time += 10;
    }


    private void win() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Winner");
        alert.setHeaderText("Wygrałeś!");
        alert.setContentText("Zwycięzca!");

        alert.showAndWait();
    }

    private boolean swap(int first, int second)
    {
        int numb = tileList.get(first).getActivenumber();

        tileList.get(first).setActivenumber(tileList.get(second).getActivenumber());
        tileList.get(second).setActivenumber(numb);

        Collections.swap(tileList,first,second);


        double xs = tileList.get(first).getLayoutX();
        double ys = tileList.get(first).getLayoutY();

        double x = tileList.get(second).getLayoutX();
        double y = tileList.get(second).getLayoutY();

        PathTransition ptr = getPathTransition(tileList.get(first), tileList.get(second));
        PathTransition ptr2 = getPathTransition(tileList.get(second), tileList.get(first));

        ParallelTransition pt = new ParallelTransition(ptr, ptr2);
        pt.play();

        pt.setOnFinished(new EventHandler<ActionEvent>() {
        @Override
        public void
        handle(ActionEvent event) {
            tileList.get(first).setTranslateY(0);
            tileList.get(first).setTranslateX(0);
            tileList.get(second).setTranslateY(0);
            tileList.get(second).setTranslateX(0);

            tileList.get(first).setLayoutY(y);
            tileList.get(first).setLayoutX(x);
            tileList.get(second).setLayoutX(xs);
            tileList.get(second).setLayoutY(ys);

        }
    });

/*
        tileList.get(first).setLayoutY(ys);
        tileList.get(first).setLayoutX(xs);
        tileList.get(second).setLayoutX(x);
        tileList.get(second).setLayoutY(y);
        */
        return check();
    }

        private void showLayout(Tile tit)
        {
            System.out.println("Real."+ tit.getNumber()+" No."+tit.getActivenumber()+" X: "+tit.getLayoutX()+" Y: "+tit.getLayoutY());
        }

        private PathTransition getPathTransition(Tile first, Tile second) {
            PathTransition ptr = new PathTransition();
            Path path = new Path();
            path.getElements().clear();
            path.getElements().add(new MoveToAbs(first));
            path.getElements().add(new LineToAbs(first, second.getLayoutX(), second.getLayoutY()));
            ptr.setPath(path);
            ptr.setNode(first);
            return ptr;
        }

    private boolean check()
    {
        for(int i = 0; i<9; i++)
        {
            if(tileList.get(i).getActivenumber() != tileList.get(i).getNumber())
            {
                return false;
            }
        }

        return true;
    }

    private void setTimeLabel(Label timeLabel, long value)
    {
        long second = TimeUnit.MILLISECONDS.toSeconds(value);
        long minute = TimeUnit.MILLISECONDS.toMinutes(value);
        long hour = TimeUnit.MILLISECONDS.toHours(value);
        long millis = value - TimeUnit.SECONDS.toMillis(second);

        timeLabel.setText(String.format("%02d:%02d:%02d:%d", hour, minute, second, millis));
    }
}

