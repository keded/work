package pro.filatov.workstation4ceb.view;


import pro.filatov.workstation4ceb.form.AppFrame;
import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.MainFrame;
import pro.filatov.workstation4ceb.form.editor.CustomOutputStream;
import pro.filatov.workstation4ceb.form.editor.TextLineNumber;
import pro.filatov.workstation4ceb.form.terminal.IModeFace;
import pro.filatov.workstation4ceb.form.terminal.Terminal;
import pro.filatov.workstation4ceb.form.terminal.TerminalMain;
import pro.filatov.workstation4ceb.form.terminal.graph.GraphPanel;
import pro.filatov.workstation4ceb.form.terminal.graph.GraphTextField;
import pro.filatov.workstation4ceb.form.terminal.EngineModeFace;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;
import pro.filatov.workstation4ceb.model.uart.MemoryModel;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;
import pro.filatov.workstation4ceb.model.uart.UartModel;

import javax.annotation.processing.SupportedSourceVersion;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;


public class AppViewFrame  extends JFrame{

    private ExchangeModel exchangeModel;

    private JPanel panel;

    private GraphPanel  graphPanel;

    private JPanel grahButtonPanel;
    private GraphTextField sinGO, cosGO, sinTO, cosTO, tsy;
    private JPanel grahButtomPanel;

    private JMenuBar menuBar;
    private JMenu file, settings, about;
    private JMenuItem save,exit;

    private String[] portList;

    private MemoryModel memoryModel;
    private UartModel uartModel;

    private boolean test = false;
    private boolean init = false;
    private boolean obmen = false;
    private boolean rotate = false;


    private JToggleButton grapfButtom = new JToggleButton("Graph");


    private JPanel buttomPanel = new JPanel();
    private JPanel plotPanel = new JPanel();


    private Panel panelGI = new Panel();
    private JLabel numGILabel = new JLabel("ГИ №:");
    private JTextField numGITextField = new JTextField(10);

    private Panel panelPP = new Panel();
    private JLabel numPPLabel = new JLabel("ПП №:");
    private JTextField numPPTextField = new JTextField(10);

    private Panel initPanel = new Panel();
    private JButton initButton, testButton, workButton, rotateButton;
    private Led initLed = new Led();
    private Panel testPanel = new Panel();
    private Led testLed = new Led();
    private Panel workPanel = new Panel();
    private Led workLed = new Led();
    private Panel rotatePanel = new Panel();
    private Led rotateLed = new Led();
    private JLabel rotateLabel = new JLabel("Скорость вращения");
    private JSlider rotateSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);



    public AppViewFrame(){

        exchangeModel = Model.getExchangeModel();
//        exchangeModel.addCebModeEventListener(CebExchangeMode.ENGINE_MODE, this );
        uartModel = Model.getUartModel();
        memoryModel = Model.getMemoryModel();

        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setTitle("Проверка ПП-032 в составе ГИ");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        panel = new JPanel();

        initMenuBar();
        initPanel();
        initGraphbutton();
        initTerminal();

        pack();
        setVisible(true);
    }

    private void initMenuBar(){

        portList = Model.getUartModel().getSerialPortList();
        if(portList.length != 0){
            uartModel.setComPort(portList[0]);
        }

        menuBar = new JMenuBar();

        file = new JMenu("Файл");
//        file.addMenuListener(new thisMenuListener());
        menuBar.add(file);

        save = new JMenuItem("Сохранить", new ImageIcon("src/main/resources/icons/save.png"));
        save.setMnemonic(KeyEvent.VK_S);
//        save.addActionListener(this);
        file.add(save);

        exit = new JMenuItem("Закрыть", new ImageIcon("src/main/resources/icons/exit.png"));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(exit);


        settings = new JMenu("Порт");
        settings.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                portList =  Model.getUartModel().getSerialPortList();
                if (portList.length != 0){
                    settings.removeAll();

                    for (int i = 0; i < portList.length ; i++) {
                        JRadioButtonMenuItem com = new JRadioButtonMenuItem(portList[i]);
                        com.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                uartModel.setComPort(com.getText());
                                System.out.println("Selected: " + uartModel.getPortName());
                            }
                        });




                        if ( uartModel.getPortName().equals(com.getText() )) {
                            System.out.println("setSelected: "  + uartModel.getPortName());
                            com.setSelected(true);
                        }

                        settings.add(com);
                    }
                }

            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });

        menuBar.add(settings);





        about = new JMenu("Помощь");
        menuBar.add(about);


        setJMenuBar(menuBar);
    }

    private void initPanel(){

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

                if(init){
                    error = uartModel.reOpenFTDIInt();
                    initButton.setText("Денициализация");
                    initLed.on();
                }else {

                    initButton.setText("Инициализация");
                    uartModel.closeFTDI();
                    initLed.off();
                }
                if(error == 0){
                    if(!init){
                        initLed.refresh();
                    }
                }else {
                    initLed.refresh();
                }
            }
        });

        testButton = new JButton("Включить проверку");
        testButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                test = !test;
                if(test) {
                    EnableOTKModeRutine rutine = new EnableOTKModeRutine();
                    testButton.setText("Выключить проверку");
                    testLed.on();
                    rutine.start();
                }else {
                    testLed.off();
                    testButton.setText("Включить проверку");
                }



                //memoryModel.resetCeb();

            }
        });

        workButton = new JButton("Включить обмен");
        workButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obmen = !obmen;
                if(obmen){
                    workButton.setText("Выключить обмен");
                    workLed.on();
                    ObmenCEBRutine obmenCEBRutine = new ObmenCEBRutine();
                    obmenCEBRutine.start();

                }else {
                    workButton.setText("Включить обмен");
                    workLed.off();

                }
            }
        });

        rotateButton = new JButton("Включить ШИМ");
        rotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotate = !rotate;
                if(rotate){
                    rotateButton.setText("Выключить ШИМ");
                    rotateLed.refresh();
                }else {
                    rotateButton.setText("Включить ШИМ");
                    rotateLed.refresh();
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

        plotPanel.setBackground(Color.green);

    }

    private void initGraphbutton(){

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(buttomPanel);

        grahButtonPanel = new JPanel();
        JPanel butPan = new JPanel();

        butPan.add(getTextFieldLabeled(sinGO = new GraphTextField("SIN GO:", new Color(255, 0, 0)), " Sin GO:"));
        butPan.add(getTextFieldLabeled(cosGO = new GraphTextField("COS GO:", new Color(64, 74, 255)), " Cos GO:"));
        butPan.add(getTextFieldLabeled(sinTO = new GraphTextField("SIN TO:", new Color(18, 255, 16)), "Sin TO:"));
        butPan.add(getTextFieldLabeled(cosTO = new GraphTextField("COS TO:", new Color(255, 55, 209)), " Cos TO:"));
        butPan.add(getTextFieldLabeled(tsy = new GraphTextField("TSY:", new Color(255, 137, 0)), "TSY:"));

        graphPanel = new GraphPanel(Model.pointData, 5000, 10);
        grahButtonPanel.setLayout(new BoxLayout(grahButtonPanel, BoxLayout.Y_AXIS));

        grahButtonPanel.add(butPan);
        grahButtonPanel.add(graphPanel.getGraphPanel());

        panel.add(grahButtonPanel);


    }

    private void initTerminal(){
        //****************** SYSTEM LOG **********************
        JTextArea textAreaErrors = new JTextArea();
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



        buttomPanel.setMaximumSize(new Dimension(400, 600));

        panel.setSize(new Dimension(getWidth(),getHeight()));

        add(splitMain0);
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

    private class ObmenCEBRutine extends  Thread {

        @Override
        public void run() {
            try {
                while(obmen){
                    byte []request = createPacket();
                    byte[] cebPacket = PacketHelper.createCebPacket(request);
                    byte[] packetToBox = PacketHelper.createBoxPacketToCeb(cebPacket);
                    UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(packetToBox);
                    t.join(3000);
                    if (t.isAlive()) {
                        t.interrupt();
                        t.join();
                        System.out.println("Error for sending packet. This packet will be ignored! ");
                        return;
                    }
                    sleep(20);
                    byte[]response = Model.getUartModel().getResponse();
                    if (response.length < 10) {
                        System.out.println("Length response from CEB are SMALL ");
                        obmen=!obmen;
                        workButton.setText("Включить обмен");
                        workLed.off();
                        return;
                    }
//                    byte[] resp = PacketHelper.extractCebPacket(Model.getUartModel().getResponse());
                    sinGO.setText(getSensor(response[3], response[4]));
                    cosGO.setText(getSensor(response[5], response[6]));
                    sinTO.setText(getSensor(response[7], response[8]));
                    cosTO.setText(getSensor(response[9], response[10]));

//                    System.out.println("sinGO" + getSensor(response[3], response[4]));
//                    System.out.println("cosGO" + getSensor(response[5], response[6]));
//                    System.out.println("sinTO" + getSensor(response[7], response[8]));
//                    System.out.println("cosTO" + getSensor(response[9], response[10]));
//                    System.out.println("tsy" + getSensor(response[11], response[12]));


                    tsy.setText(getSensor(response[11], response[12]));

                    synchronized (Model.pointData) {
                        try {
                            Model.pointData.wait();
                            Model.pointData.addPointPackage();
                            sinGO.addPoint(getSensorDouble(response[3], response[4]));
                            cosGO.addPoint(getSensorDouble(response[5], response[6]));
                            sinTO.addPoint(getSensorDouble(response[7], response[8]));
                            cosTO.addPoint(getSensorDouble(response[9], response[10]));
                            tsy.addPoint(getSensorDouble(response[11], response[12]));
                        } catch (InterruptedException ex) {
                            System.err.println("own:: Interrupted: " + ex.getMessage());
                        }
                    }



                }
                return;

            } catch (InterruptedException e) {
                e.printStackTrace();
                testLed.refresh();
            }



        }
    }

    private byte[]createPacket(){

        byte []packet = new byte[] {} ;// {(byte)0xbb, (byte)0xbb};




        packet = PacketHelper.addDataToPacket(packet, PacketHelper.i2b(0x5000));

        Integer nPwm = Integer.parseInt("1650");
        packet = PacketHelper.addDataToPacket(packet, nPwm);

        Integer death = Integer.parseInt("350");
        packet = PacketHelper.addDataToPacket(packet, death);


        boolean[] arg0 = {
                true,
                true,
                true
        };
        packet = PacketHelper.addDataToPacket(packet, rotate ? (byte) 0x07 : (byte) 0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);
        boolean[] arg1 = {
                true,
                true,
                true
        };

        packet = PacketHelper.addDataToPacket(packet, rotate ? (byte) 0x07 : (byte) 0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);


        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("004"));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("004"));

        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("0000")); // 1900
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("0"));

        boolean[] argMatching = {false, false, false, true};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argMatching));
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        byte modeKPUint;

        if (false) {

            boolean[] modeKPU = {true,
                    false};
            modeKPUint = PacketHelper.bool2byte(modeKPU);
        } else {
            {
                modeKPUint = (byte) 0x02;
            }
        }
        packet = PacketHelper.addDataToPacket(packet, modeKPUint);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        Integer step = Integer.parseInt("2");
        packet = PacketHelper.addDataToPacket(packet, step);

        if (step < 0) {
            byte b = (byte) 0xFF;
            packet = PacketHelper.addDataToPacket(packet, b);
            packet = PacketHelper.addDataToPacket(packet, b);
        } else {
            byte b = (byte) 0x00;
            packet = PacketHelper.addDataToPacket(packet, b);
            packet = PacketHelper.addDataToPacket(packet, b);
        }


        Integer porogFHVgoOrToInt = Integer.parseInt("0");
        packet = PacketHelper.addDataToPacket(packet, porogFHVgoOrToInt);

        Integer fhvTOdelitelInt = Integer.parseInt("0");
        packet = PacketHelper.addDataToPacket(packet, fhvTOdelitelInt);



        /*
        Integer init_angle = Integer.parseInt(constInitAngle.getText());
        packet = PacketHelper.addDataToPacket(packet, init_angle&0xFFFF);

        Integer high_bits = init_angle >> 16;
        packet = PacketHelper.addDataToPacket(packet, high_bits&0x00FF);
        */


        boolean[] argEnaCalcUqToShim = {false};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argEnaCalcUqToShim));
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);


        Integer rateAdcInt = Integer.parseInt("28");
        if (false) {
            rateAdcInt = rateAdcInt | 0x4000;
        }
        if (false) {
            rateAdcInt = rateAdcInt | 0x2000;
        }
        packet = PacketHelper.addDataToPacket(packet, rateAdcInt);
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("0"));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("0"));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("0"));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("0", 16));


        packet = PacketHelper.addDataToPacket(packet,  Integer.parseInt("0"));

        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt("0"));



        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);


        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        Integer capt_delay_i = Integer.parseInt("0");
        packet = PacketHelper.addDataToPacket(packet, capt_delay_i);

        Integer limit = Integer.parseInt("0");
        packet = PacketHelper.addDataToPacket(packet, limit);

        Integer phaseSpiInt = Integer.parseInt("0");
        packet = PacketHelper.addDataToPacket(packet, phaseSpiInt);





        return packet;


    }

    private class Panel extends JPanel{
        public Panel(){
            setLayout(new FlowLayout (FlowLayout.LEFT, 10, 10));

        }
    }

    private class Led extends JPanel{
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

    private JPanel getTextFieldLabeled(JTextField textField, String label) {
        return AppFrameHelper.getTextFieldLabeled(textField, label, 100, 40);
    }

    private String getSensor(byte low_byte, byte high_byte) {
        return PacketHelper.getSensor(low_byte, high_byte);
    }

    private Double getSensorDouble(byte low_byte, byte high_byte) {
        return PacketHelper.getSensorDouble(low_byte, high_byte);
    }
}
