package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.terminal.graph.GraphFrame;
import pro.filatov.workstation4ceb.form.terminal.graph.IEnableGraphListener;
import pro.filatov.workstation4ceb.form.terminal.graph.PlotFrame;
import pro.filatov.workstation4ceb.form.terminal.graph.PlotFrame2;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by yuri.filatov on 09.09.2016.
 */
public class ModeControlPanel extends JPanel  implements TerminalModelEventListener, IEnableGraphListener{



    JButton enableModeButton, resetCebButton,  sendPacketButton, refreshingDataButton;
    ExchangeModel exchangeModel;
    TerminalModel terminalModel;
    JButton createRamDataFromRequest;
    JButton createHexToImitRequest;
    JToggleButton graphButton;
    JToggleButton graphButton2;
    PlotFrame plotDialog;
    PlotFrame2 plotDialog2;
    JButton initBlockButton;
    JButton   buttonReOpenFTDI;
    GraphFrame graphFrame;

    public ModeControlPanel(){

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        exchangeModel= Model.getExchangeModel();
        terminalModel = Model.getTerminalModel();



        Action enableModeAction= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                enableModeButton.setEnabled(false);
                sendPacketButton.setEnabled(true);
                refreshingDataButton.setEnabled(true);
                terminalModel.setEnableMode(true);
            }
        };
        enableModeButton = new JButton(enableModeAction);
        enableModeButton.setText("Enable " + terminalModel.getCurrentExchangeMode().getName());
        enableModeButton.setToolTipText("Jump to mode on target device");
        enableModeButton.setMargin(new Insets(0,0,0,0));
        //enableModeButton.setSize(new Dimension(90, 15));

     //   enableModeButton.setEnabled(false);

        Action resetCebAction= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().resetCeb();
                enableModeButton.setEnabled(true);
                //sendPacketButton.setEnabled(false);
                //refreshingDataButton.setEnabled(false);
               // terminalModel.setEnableMode(false);
            }
        };
        resetCebButton = new JButton(resetCebAction);
        resetCebButton.setText("Reset Ceb");
        resetCebButton.setToolTipText("Reset ceb for loading BIOS");
        resetCebButton.setMargin(new Insets(0,0,0,0));




        Action initAction= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().startInitBlock();
            }
        };
        initBlockButton = new JButton(initAction);
        initBlockButton.setText("Init");
        initBlockButton.setToolTipText("");
        initBlockButton.setMargin(new Insets(0,0,0,0));


        Action sendPacketAction= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                terminalModel.setRunCurrentMode(true);
                exchangeModel.justDoIt();
            }
        };
        sendPacketButton = new JButton(sendPacketAction);
        sendPacketButton.setText("Send Packet");
        sendPacketButton.setToolTipText("Send packet in Main Mode");
        sendPacketButton.setActionCommand("sendPacket");
        sendPacketButton.setMargin(new Insets(0,0,0,0));
        sendPacketButton.setEnabled(false);


        Action refreshingDataAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(!terminalModel.getRepeatingCebExchange()) {
                    terminalModel.setRunCurrentMode(true);
                    sendPacketButton.setEnabled(false);
                    refreshingDataButton.setText("STOP");
                    terminalModel.setRepeatingCebExchange(true);
                    exchangeModel.justDoIt();
                } else{
                    sendPacketButton.setEnabled(true);
                    refreshingDataButton.setText("Refreshing Data");
                    terminalModel.setRepeatingCebExchange(false);
                }
            }
        };
        refreshingDataButton = new JButton(refreshingDataAction);
        refreshingDataButton.setText("Refreshing Data");
        refreshingDataButton.setToolTipText("Сyclic sending data to ceb");
        refreshingDataButton.setMargin(new Insets(0,0,0,0));
        refreshingDataButton.setEnabled(false);


        Action createHexToImitRequestAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                terminalModel.createHexToImitRequest();
            }
        };
        createHexToImitRequest = new JButton(createHexToImitRequestAction);
        createHexToImitRequest.setText("Create imit");
        createHexToImitRequest.setToolTipText("Create hix file with data to box imitator");
        createHexToImitRequest.setMargin(new Insets(0,0,0,0));
        createHexToImitRequest.setEnabled(true);

        Action createRamDataFromRequestAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                terminalModel.createLinkDataFromRequest();
            }
        };
        createRamDataFromRequest = new JButton(createRamDataFromRequestAction);
        createRamDataFromRequest.setText("Create ram");
        createRamDataFromRequest.setToolTipText("Modify ceb_link_ao_ram.txt");
        createRamDataFromRequest.setMargin(new Insets(0,0,0,0));
        createRamDataFromRequest.setEnabled(true);


        graphButton = new JToggleButton("Graph");
        graphButton.setMargin(new Insets(0,0,0,0));
        graphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(graphButton.isSelected()) {
                    if (!Model.flagQueue){
                        //creating and showing this application's GUI.
                        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Model.flagQueue = true;
                                graphFrame = new GraphFrame(Model.pointData, 5000, 10);
                            }
                        });
                    }
                }

            }
        });


        sendPacketButton.setEnabled(true);
        refreshingDataButton.setEnabled(true);
        terminalModel.setEnableMode(true);
        Action performReOpenFTDI = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getUartModel().reOpenFTDI();

            }
        };

        buttonReOpenFTDI = new JButton(performReOpenFTDI);
        buttonReOpenFTDI.setText("ReOpen FTDI");
        buttonReOpenFTDI.setMargin(new Insets(0,0,0,0));
        buttonReOpenFTDI.setToolTipText("ReOpen FTDI");
        buttonReOpenFTDI.setFocusable(false);
        buttonReOpenFTDI.getActionMap().put("performReOpenFTDI", performReOpenFTDI);




        terminalModel.setTerminalModelEventListener(this);
        terminalModel.setGraphListener(this);
       // add(enableModeButton);
        add(resetCebButton);
        add(initBlockButton);
        add(sendPacketButton);
        add(refreshingDataButton);
        add(createHexToImitRequest);
        add(createRamDataFromRequest);
        add( graphButton);
        add(buttonReOpenFTDI);

    }


    @Override
    public void updateEnableButton() {
        if(enableModeButton.isEnabled()){
            enableModeButton.setText("Enable "+ terminalModel.getCurrentExchangeMode().getName());
        }

    }


    @Override
    public void initButtonGraph() {
        graphButton.setSelected(false);
    }
}
