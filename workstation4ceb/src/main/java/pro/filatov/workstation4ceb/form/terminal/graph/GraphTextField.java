package pro.filatov.workstation4ceb.form.terminal.graph;

import pro.filatov.workstation4ceb.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Egor on 03.07.2017.
 */
public class GraphTextField extends JTextField{


    private Color colorDef = new Color(235, 235, 235);

    private String labelGraph;
    private String labelTextField;
    private String name;
    private Color graphColor;

    private boolean flagEnable = false;


    public void  addPoint(Double valueGraph){
        if (flagEnable) {
            Model.pointData.addPointStruct(valueGraph, name, graphColor);
        }
    }

    public void  addPoint(Double valueGraph, PointData pointData){
        if (flagEnable) {
           pointData.addPointStruct(valueGraph, name, graphColor);
        }
    }

    public GraphTextField(String name, Color color) {
        this.name = name;
        this.graphColor = color;
        super.setBackground(colorDef);
        super.setEnabled(false);
        super.setDisabledTextColor(Color.black);
        super.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (flagEnable) {
                    flagEnable = false;
                    GraphTextField.this.setBackground(colorDef);
                }
                else {
                    flagEnable = true;
                    GraphTextField.this.setBackground(graphColor);
                }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }
            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    //sinGO.setText(getSensor(resp[2], resp[3]));
}
