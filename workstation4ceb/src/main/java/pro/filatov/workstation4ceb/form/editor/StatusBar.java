package pro.filatov.workstation4ceb.form.editor;

import pro.filatov.workstation4ceb.form.AppFrame;
import pro.filatov.workstation4ceb.model.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by yuri.filatov on 03.08.2016.
 */
public class StatusBar extends JPanel implements ILongProcessEventListener {

    private JButton buttonParse;
    private JButton buttonProgram;
    private JButton buttonParseAll;
    private JButton buttonCleanErrors;
    private JButton buttonGenMif;
    private JButton buttonResetProgramInit;
    private JButton buttonReOpenFTDI;
    private JLabel statusPointEditor = new JLabel();


    public StatusBar(){
        super();
        Action performParse= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getEditorModel().saveCurrentFile();
                Model.getParserModel().parseCurrentFile();
            }
        };
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        KeyStroke keyParse = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK);
        buttonParse = new JButton(performParse);

        buttonParse.setText("Parse");
        buttonParse.setToolTipText("Parse assembly to generate assembly");
        buttonParse.setActionCommand("parse");

        buttonParse.setFocusable(false);
        buttonParse.getActionMap().put("performParse", performParse);
        buttonParse.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyParse, "performParse");



        Action parseAll= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                Model.getParserModel().parseAllFiles();
                Model.getEditorModel().updateColors();
            }
        };
        KeyStroke keyProgram = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK);
        buttonParseAll = new JButton(parseAll);
        buttonParseAll.setText("ParseAll");
        buttonParseAll.setToolTipText("Parse all txt and assembly to generate assembly");
        buttonParseAll.setActionCommand("parseAll");

        buttonParseAll.setFocusable(false);
        buttonParseAll.getActionMap().put("parseAll", parseAll);
        buttonParseAll.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyProgram, "parseAll");





        Action performProgram = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //byte [] packet = { (byte)0x00 };
                //Model.getUartModel().doExchangePacket(packet);
                Model.getMemoryModel().writeSelectedFilesToFlash();
            }
        };


        buttonProgram = new JButton(performProgram);
        buttonProgram.setText("Program");

        buttonProgram.setToolTipText("Program selected files to hardware");
        buttonProgram.setActionCommand("program");

        buttonProgram.setFocusable(false);
        buttonProgram.getActionMap().put("performProgram", performProgram);



        Action performCleanErrors = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getEditorModel().cleanTextErrors();
            }
        };

        buttonCleanErrors = new JButton(performCleanErrors);
        buttonCleanErrors.setText("Clean");

        buttonCleanErrors.setToolTipText("Clean output in errors area");
        buttonCleanErrors.setFocusable(false);
        buttonCleanErrors.getActionMap().put("performCleanErrors", performCleanErrors);


        Action performGenMif= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getParserModel().setGenerateMifFiles(true);
                Model.getParserModel().parseAllFiles();
            }
        };

        buttonGenMif = new JButton(performGenMif);
        buttonGenMif.setText("Generate MIFs");

        buttonGenMif.setToolTipText("Generate MIF files for Quartus");
        buttonGenMif.setFocusable(false);
        buttonGenMif.getActionMap().put("performGenMif", performGenMif);


        Action performResetProgramInit = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().setAddress(8192);

                Model.getMemoryModel().resetProgramInit();


            }
        };

        buttonResetProgramInit = new JButton(performResetProgramInit);
        buttonResetProgramInit.setText("Res&Prog&Init");

        buttonResetProgramInit.setToolTipText("Reset -> Program -> Init");
        buttonResetProgramInit.setFocusable(false);
        buttonResetProgramInit.getActionMap().put("performResetProgramInit", performResetProgramInit);

        Action performReOpenFTDI = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getUartModel().reOpenFTDI();

            }
        };

        buttonReOpenFTDI = new JButton(performReOpenFTDI);
        buttonReOpenFTDI.setText("ReOpen FTDI");

        buttonReOpenFTDI.setToolTipText("ReOpen FTDI");
        buttonReOpenFTDI.setFocusable(false);
        buttonReOpenFTDI.getActionMap().put("performReOpenFTDI", performReOpenFTDI);






        add(buttonParse);
        add(buttonParseAll);
        add(buttonProgram);
        add(buttonCleanErrors);
        add(buttonGenMif);
        add(buttonResetProgramInit);
        add(buttonReOpenFTDI);

        Model.getParserModel().addLongProcessEventListener(this);
        Model.getUartModel().addLongProcessEventListener(this);
    }


    @Override
    public void updateStatusOfParsing() {
        if(Model.getParserModel().getParsing()){
            buttonParse.setEnabled(false);
            buttonParseAll.setEnabled(false);
        } else{
            buttonParse.setEnabled(true);
            buttonParseAll.setEnabled(true);
        }
    }

    @Override
    public void updateStatusOfProgramming() {
        if(Model.getUartModel().isProgramming()){
            buttonProgram.setEnabled(false);
            buttonResetProgramInit.setEnabled(false);
        } else{
            buttonProgram.setEnabled(true);
            buttonResetProgramInit.setEnabled(true);
        }

    }
}
