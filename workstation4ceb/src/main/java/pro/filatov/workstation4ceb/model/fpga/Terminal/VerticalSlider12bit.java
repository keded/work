package pro.filatov.workstation4ceb.model.fpga.Terminal;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;

public class VerticalSlider12bit extends JPanel{


    static final int V_M2048 = -2047;
    static final int V_2047 = 2047;
    static final int V_INIT = 0;    //initial frames per second

    JSlider slider;
    JTextField currentTextField;

    public VerticalSlider12bit(JTextField textField){

        currentTextField = textField;
        //Create the slider
        slider = new JSlider(JSlider.VERTICAL, V_M2048, V_2047, V_INIT);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textField.setText(String.valueOf(((JSlider)e.getSource()).getModel().getValue()));
            }
        });
        //slider.setMajorTickSpacing(10);
        // slider.setPaintTicks(true);

        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel("0") );
        labelTable.put( new Integer( V_2047 ), new JLabel("2047") );
        labelTable.put( new Integer(V_M2048), new JLabel("-2047") );
        slider.setLabelTable( labelTable );
        slider.setPaintLabels(true);
        add(slider);

    }

    public VerticalSlider12bit(JTextField textField, int orient, Integer min, Integer max, Integer defaultValue){

        currentTextField = textField;
        //Create the slider
        slider = new JSlider(orient, min, max, defaultValue);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textField.setText(String.valueOf(((JSlider)e.getSource()).getModel().getValue()));
            }
        });
        //slider.setMajorTickSpacing(10);
        // slider.setPaintTicks(true);

        //Create the label table
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer(min ), new JLabel(min.toString()) );
        labelTable.put( new Integer( max ), new JLabel(max.toString()) );
        slider.setLabelTable( labelTable );
        slider.setPaintLabels(true);
        add(slider);

    }


    public void setValue(int value){
        slider.getModel().setValue(value);
    }


}
