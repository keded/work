package pro.filatov.workstation4ceb.form.terminal.graph;

import java.awt.*;

/**
 * Created by user on 22.08.2017.
 */
public class LabelList {
    private String label;
    private Color color;

    public LabelList(String label, Color color){
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
