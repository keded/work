package pro.filatov.workstation4ceb.form.terminal.graph;

import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by user on 07.02.2017.
 */
public class HistoryTextField extends JPanel {

    private JTextField textField;
    private JCheckBox is_graph;
    private String name;
    private TerminalModel terminal;
    private Boolean isGraphing;

    public HistoryTextField (String label, int sizeLabel, int sizeField){
        textField = new JTextField();
        is_graph = new JCheckBox();
        is_graph.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGraphing = is_graph.isSelected();
                if(isGraphing){
                    terminal.addFunctionToGraph(name);
                }else{
                    terminal.removeFunctionFromGraph(name);
                }
            }
        });
        name = label;
        //textField.setEditable(false);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JLabel comment= new JLabel(label, JLabel.RIGHT);
        comment.setMaximumSize(new Dimension(sizeLabel, 20));
        this.add(comment);
        textField.setColumns(5);
        textField.setMaximumSize(new Dimension(sizeField, 20));
        this.add(textField);
        this.add(is_graph);
        terminal = Model.getTerminalModel();
    }


    public void setValue(Integer value){
        textField.setText(String.valueOf(value));
        if(isGraphing){
            terminal.addValueToRowGraph(name, value);
        }
    }




}
