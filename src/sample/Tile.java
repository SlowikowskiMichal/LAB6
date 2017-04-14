package sample;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle{
    private int number;
    private int activenumber;

    public Tile(double width, double height, WritableImage part, int number)
    {
        super(width,height);
        this.setFill(new ImagePattern(part));
        this.number = number;
        this.activenumber = number;
    }

    public void setActivenumber(int activenumber) {
        this.activenumber = activenumber;
    }

    public int getActivenumber() {
        return activenumber;
    }

    public int getNumber() {
        return number;
    }
}
