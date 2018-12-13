package pro.filatov.workstation4ceb.view;

import pro.filatov.workstation4ceb.form.terminal.Indicator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by user on 13.12.2018.
 */
public class appViewFrame extends JFrame {


    private  int radius = 30;

    JPanel buttomPanel = new JPanel();
    JPanel plotPanel = new JPanel();


    Panel panelGI = new Panel();
    JLabel numGILabel = new JLabel("ГИ №:");
    JTextField numGITextField = new JTextField(10);

    Panel panelPP = new Panel();
    JLabel numPPLabel = new JLabel("ПП №:");
    JTextField numPPTextField = new JTextField(10);

    Panel initPanel = new Panel();
    JButton initButton = new JButton("Инициализация");
    Led initLed = new Led();
    Panel testPanel = new Panel();
    JButton testButton = new JButton("Включить ркежим проверки");
    Led testLed = new Led();
    Panel workPanel = new Panel();
    JButton workButton = new JButton("Включить обмен");
    Led workLed = new Led();
    Panel rotatePanel = new Panel();
    JButton rotateButton = new JButton("Включить вращение ГИ");
    Led rotateLed = new Led();
    JLabel rotateLabel = new JLabel("Скорость вращения");
    JSlider rotateSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);



    public appViewFrame(){

//        setLocationRelativeTo(null);
        setSize(1000,600);
        setTitle("Провыкрка ПП-032 в составе ГИ");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        setLayout(new BorderLayout());


        initPanel();
        add(plotPanel,BorderLayout.CENTER);

        add(buttomPanel,BorderLayout.LINE_START);


//        pack();
        setVisible(true);
    }

    class Panel extends JPanel{
       public Panel(){
           setLayout(new FlowLayout (FlowLayout.LEFT, 10, 10));

       }
    }

    class Led extends JPanel{
        public Led(){
            setBackground(Color.red);
            setBorder(new LineBorder(new Color(200,0,0), 1, true));
        }
    }

    void initPanel(){

        plotPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, plotPanel.getMinimumSize().height));



        rotateSlider.setMajorTickSpacing(1);
        rotateSlider.setPaintTicks(true);
        rotateSlider.setPaintLabels(true);





        panelGI.add(numGILabel);
        panelGI.add(numGITextField);
        panelGI.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelGI.getMinimumSize().height));

        panelPP.add(numPPLabel);
        panelPP.add(numPPTextField);
        panelPP.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelPP.getMinimumSize().height));

        initPanel.add(initButton);
        initPanel.add(initLed);
        initPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, initPanel.getMinimumSize().height));

        testPanel.add(testButton);
        testPanel.add(testLed);
        testPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, testPanel.getMinimumSize().height));

        workPanel.add(workButton);
        workPanel.add(workLed);
        workPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, workPanel.getMinimumSize().height));

        rotatePanel.add(rotateButton);
        rotatePanel.add(rotateLed);
        rotatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,rotatePanel.getMinimumSize().height));
        rotateSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE,rotateSlider.getMinimumSize().height));


        buttomPanel.setLayout(new BoxLayout(buttomPanel, BoxLayout.PAGE_AXIS));



        buttomPanel.add(panelGI);
        buttomPanel.add(panelPP);
        buttomPanel.add(initPanel);
        buttomPanel.add(testPanel);
        buttomPanel.add(workPanel);
        buttomPanel.add(rotatePanel);
        buttomPanel.add(rotateLabel);
        buttomPanel.add(rotateSlider);

        plotPanel.setBackground(Color.green);


    }



}
