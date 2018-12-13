package pro.filatov.workstation4ceb.form.terminal.graph;


import java.awt.*;

/**
 * Created by user on 18.07.2017.
 */
public class PointStruct {
    private double value;
    private String ind;
    private Color color;

    public PointStruct(double value, String ind, Color color) {
        this.value = value;
        this.ind = ind;
        this.color = color;
    }

    public double getValue() {
        return value;
    }

    public String getInd() {
        return ind;
    }

    public Color getColor() {
        return color;
    }
}
