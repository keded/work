package pro.filatov.workstation4ceb.view;


import pro.filatov.workstation4ceb.form.editor.CustomOutputStream;
import pro.filatov.workstation4ceb.form.editor.TextLineNumber;
import pro.filatov.workstation4ceb.form.terminal.graph.GraphFrame;
import pro.filatov.workstation4ceb.form.terminal.graph.GraphPanel;
import pro.filatov.workstation4ceb.form.terminal.graph.PointData;
import pro.filatov.workstation4ceb.form.tree.OTKFrame;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.uart.MemoryModel;
import pro.filatov.workstation4ceb.model.uart.UartModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

/**
 * Created by user on 13.12.2018.
 */
public class appViewFrame extends JFrame {

    private JTextArea textAreaErrors;
    private MemoryModel memoryModel;
    private UartModel uartModel;
    boolean init = false;
    GraphFrame grapf;
    JToggleButton grapfButtom = new JToggleButton("Graph");

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
    JButton initButton;
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

        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setTitle("Провыкрка ПП-032 в составе ГИ");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

//        setLayout();


        initPanel();
        JPanel panel = new JPanel();
        //****************** SYSTEM LOG **********************
        textAreaErrors = new JTextArea();
        JScrollPane sysLogScrollPane =  new JScrollPane(textAreaErrors, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sysLogScrollPane.setRowHeaderView(new TextLineNumber(textAreaErrors));
        PrintStream printStream = new PrintStream(new CustomOutputStream(textAreaErrors));
        System.setOut(printStream);
        System.setErr(printStream);

        JSplitPane splitMain0 = new JSplitPane();
        splitMain0.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitMain0.setDividerSize(9);
        splitMain0.setSize(getWidth(), getHeight());
        splitMain0.setDividerLocation(0.6);
        splitMain0.setResizeWeight(1);
        splitMain0.setOneTouchExpandable(true);
        splitMain0.setTopComponent(panel);//createPanel("TopPanelMain0")
        splitMain0.setBottomComponent(sysLogScrollPane);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
//        plotPanel.add(new JButton("Kirill"));

        buttomPanel.setMaximumSize(new Dimension(400, 600));
        panel.add(buttomPanel);
        panel.setSize(new Dimension(getWidth(),getHeight()));
        GraphPanel  graphPanel = new GraphPanel(Model.pointData, 5000, 10);
        panel.add(graphPanel.getGraphPanel());
//        panel.add(plotPanel);

       // add(panel);
        add(splitMain0);
        pack();
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
           // setBorder(new LineBorder(new Color(200, 16, 14), 1, true));
        }

        public void on(){
            setBackground(Color.green);
        }
        public void off(){
            setBackground(Color.red);
        }
        public void refresh(){
            if (getBackground() == Color.red )
                setBackground(Color.green);
            else setBackground(Color.red);
        }

    }

    void initPanel(){

        //plotPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, plotPanel.getMinimumSize().height));



        rotateSlider.setMajorTickSpacing(1);
        rotateSlider.setPaintTicks(true);
        rotateSlider.setPaintLabels(true);

        panelGI.add(numGILabel);
        panelGI.add(numGITextField);
        panelGI.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelGI.getMinimumSize().height));

        panelPP.add(numPPLabel);
        panelPP.add(numPPTextField);
        panelPP.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelPP.getMinimumSize().height));

        initButton = new JButton("Инициализация");
        initButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int error = 0;
                init = !init;

                if(!init){
                    error = uartModel.reOpenFTDIInt();
                    initButton.setText("Денициализация");
                }else {
                    initLed.off();
                    initButton.setText("Инициализация");
                }
                if(error == 0){
                    if(!init){
                        initLed.on();
                    }
                }else {
                    initLed.off();
                }
            }
        });

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



        grapfButtom.setMargin(new Insets(0,0,0,0));
        grapfButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(grapfButtom.isSelected()) {
                    if (!Model.flagQueue){
                        //creating and showing this application's GUI.
                        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Model.flagQueue = true;
                                grapf = new GraphFrame(Model.pointData, 5000, 10);
                            }
                        });
                    }
                }

            }
        });

        buttomPanel.add(panelGI);
        buttomPanel.add(panelPP);
        buttomPanel.add(initPanel);
        buttomPanel.add(testPanel);
        buttomPanel.add(workPanel);
        buttomPanel.add(rotatePanel);
        buttomPanel.add(rotateLabel);
        buttomPanel.add(rotateSlider);
        buttomPanel.add(grapfButtom);

        plotPanel.setBackground(Color.green);





    }

    private class EnableOTKModeRutine extends  Thread {

        @Override
        public void run() {
            try {
                int error = memoryModel.resetInitOTKMode(this);
                if(error == 0){
                        initLed.on();
                }else {
                    initLed.off();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                initLed.refresh();
            }



        }
    }

}
