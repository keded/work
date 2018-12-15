package pro.filatov.workstation4ceb.form.tree;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.editor.CustomOutputStream;
import pro.filatov.workstation4ceb.form.editor.TextLineNumber;
import pro.filatov.workstation4ceb.form.terminal.GridBagHelper;
import pro.filatov.workstation4ceb.form.terminal.Indicator;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.DataRamWord;
import pro.filatov.workstation4ceb.model.uart.MemoryModel;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;
import pro.filatov.workstation4ceb.model.uart.UartModel;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

/**
 * Created by Lenovo on 22.11.2018.
 */
public class OTKFrame extends JFrame {
    private JTextArea textAreaErrors;
    JPanel otkPanel;
    JButton buttonRefreshListDevices;
    JButton buttonReopenFTDI;
    Indicator indEnaFTDI;
    JComboBox comboListDevices;
    JButton buttonEnableOTKMode;
    Indicator indOTKModeIsEnabled;
    JButton buttonRunStopObmen;
    Indicator indRunStopObmen;
    JButton buttonRunStopPWM;
    Indicator indRunStopPWM;
    JButton buttonRunStopDAC;
    Indicator indRunStopDAC;


    private Font fontButton;
    private boolean runFTDI = false;
    private boolean runOTK = false;
    private boolean runObmen = false;
    private boolean runPWM = false;
    private boolean runDAC = false;

    DefaultComboBoxModel defaultComboBoxModel;
    private UartModel uartModel;
    private MemoryModel memoryModel;
    private  int radius = 30;
    public OTKFrame() {


        fontButton = new Font("Arial", Font.PLAIN, 20);
        AppFrameHelper.setupAppLocation3(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Work station for check PP-032 in INAYA20-019");

        otkPanel = new JPanel();

        GridBagHelper helper = new GridBagHelper();
        otkPanel.setLayout(new GridBagLayout());
        helper.setWeights(1,1).fillBoth();

        uartModel = Model.getUartModel();
        memoryModel = Model.getMemoryModel();
        buttonRefreshListDevices  = new JButton("Обновить список com-портов:");
        buttonRefreshListDevices.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] portList =  Model.getUartModel().getSerialPortList();
                ComboBoxModel<String> model = new DefaultComboBoxModel<>(portList);
                comboListDevices.setModel(model);
            }
        });

        otkPanel.add(buttonRefreshListDevices, helper.get());
        String[] portList = Model.getUartModel().getSerialPortList();
        if(portList.length != 0){
            uartModel.setComPort(portList[0]);
        }
        defaultComboBoxModel = new DefaultComboBoxModel(portList);
        comboListDevices = new JComboBox(defaultComboBoxModel);
        comboListDevices.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Selected :" +comboListDevices.getSelectedItem().toString());
                uartModel.setComPort(comboListDevices.getSelectedItem().toString());
            }
        });

        comboListDevices.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                String[] portList =  Model.getUartModel().getSerialPortList();
                ComboBoxModel<String> model = new DefaultComboBoxModel<>(portList);
                comboListDevices.setModel(model);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
        otkPanel.add(comboListDevices, helper.rightColumn().get());

        buttonReopenFTDI = new JButton("Включить связь ПК с рабочим местом");
        buttonReopenFTDI.setFont(fontButton);
        //otkPanel.add(buttonReopenFTDI, helper.nextRow().get());
        indEnaFTDI = new Indicator(radius,radius);


        buttonReopenFTDI.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int error = 0;
                runOTK = !runOTK;

                if(runOTK){
                    error = uartModel.reOpenFTDIInt();
                    buttonReopenFTDI.setText("Выключить связь ПК с рабочим местом");
                }else {
                    indEnaFTDI.refresh(null);
                    buttonReopenFTDI.setText("Включить связь ПК с рабочим местом");
                }
                if(error == 0){
                    if(runOTK){
                        indEnaFTDI.refresh(true);
                    }
                }else {
                    indEnaFTDI.refresh(false);
                }
            }
        });



        otkPanel.add(AppFrameHelper.getPanelIndicator(indEnaFTDI = new Indicator(radius,radius), radius), helper.rightColumn().fillNone().get());


        buttonEnableOTKMode = new JButton("Включить режим проверки платы ЦЭБ");
        buttonEnableOTKMode.setFont(fontButton);
        buttonEnableOTKMode.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runOTK = !runOTK;
                if(runOTK) {
                    EnableOTKModeRutine rutine = new EnableOTKModeRutine();
                    buttonEnableOTKMode.setText("Выключить режим проверки платы ЦЭБ");
                    rutine.start();
                }else {
                    indOTKModeIsEnabled.refresh(null);
                    buttonEnableOTKMode.setText("Включить режим проверки платы ЦЭБ");
                }



                //memoryModel.resetCeb();

            }
        });
        otkPanel.add(buttonEnableOTKMode, helper.nextRow().fillBoth().get());
        otkPanel.add(AppFrameHelper.getPanelIndicator(indOTKModeIsEnabled = new Indicator(radius,radius), radius),  helper.rightColumn().fillNone().get());





        otkPanel.add( buttonRunStopObmen = new JButton("Включить циклический обмен с ЦЭБ"), helper.nextRow().fillBoth().get());
        buttonRunStopObmen.setFont(fontButton);
        otkPanel.add(AppFrameHelper.getPanelIndicator(indRunStopObmen = new Indicator(radius,radius), radius),  helper.rightColumn().fillNone().get());
       buttonRunStopObmen.addActionListener(new AbstractAction() {
           @Override
           public void actionPerformed(ActionEvent e) {
               runObmen = !runObmen;
               if(runObmen){
                   buttonRunStopObmen.setText("Вылючить циклический обмен с ЦЭБ");
                   indRunStopObmen.refresh(true);
                   ObmenCEBRutine obmenCEBRutine = new ObmenCEBRutine();
                   obmenCEBRutine.start();

               }else {
                   buttonRunStopObmen.setText("Включить циклический обмен с ЦЭБ");
                   indRunStopObmen.refresh(null);

               }
           }
       });

        otkPanel.add(buttonRunStopPWM = new JButton("Включить ШИМ"), helper.nextRow().fillBoth().get());
        otkPanel.add(AppFrameHelper.getPanelIndicator(indRunStopPWM = new Indicator(radius,radius), radius),  helper.rightColumn().fillNone().get());
        buttonRunStopPWM.setFont(fontButton);
        buttonRunStopPWM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runPWM = !runPWM;
                if(runPWM){
                    buttonRunStopPWM.setText("Выключить ШИМ");
                    indRunStopPWM.refresh(true);
                }else {
                    buttonRunStopPWM.setText("Включить ШИМ");
                    indRunStopPWM.refresh(null);
                }
            }
        });
        //otkPanel.add(buttonRunStopDAC= new JButton("Включить ЦАП"), helper.nextRow().fillBoth().get());
        //otkPanel.add(AppFrameHelper.getPanelIndicator(indRunStopDAC = new Indicator(radius,radius), radius),  helper.rightColumn().fillNone().get());
        /*buttonRunStopDAC.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runDAC = !runDAC;
                if(runDAC){
                    buttonRunStopDAC.setText("Выключить ЦАП");
                    indRunStopDAC.refresh(true);
                }else {
                    buttonRunStopDAC.setText("Включить ЦАП");
                    indRunStopDAC.refresh(null);
                }
            }
        });
        */
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
        splitMain0.setResizeWeight(0.5);
        splitMain0.setOneTouchExpandable(true);
        splitMain0.setTopComponent(otkPanel);//createPanel("TopPanelMain0")
        splitMain0.setBottomComponent(sysLogScrollPane);
        add(splitMain0);




       // JPanel freePanel = AppFrameHelper.createPanel("free");
       // otkPanel.add(freePanel);
        this.setVisible(true);

    }


    private class EnableOTKModeRutine extends  Thread {

        @Override
        public void run() {
            try {
                int error = memoryModel.resetInitOTKMode(this);
                if(error == 0){
                    indOTKModeIsEnabled.refresh(true);
                }else {
                    indOTKModeIsEnabled.refresh(false);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                indOTKModeIsEnabled.refresh(false);
            }



        }
    }

    private class ObmenCEBRutine extends  Thread {

        @Override
        public void run() {
            try {
               while(runObmen){
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

               }
                return;

            } catch (InterruptedException e) {
                e.printStackTrace();
                indOTKModeIsEnabled.refresh(false);
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
        packet = PacketHelper.addDataToPacket(packet, runPWM ? (byte) 0x07 : (byte) 0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);
        boolean[] arg1 = {
                true,
                true,
                true
        };

        packet = PacketHelper.addDataToPacket(packet, runPWM ? (byte) 0x07 : (byte) 0x00);
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


}
