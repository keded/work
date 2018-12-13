package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.terminal.graph.GraphTextField;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yuri.filatov on 01.09.2016.
 */
public class MainModeFace extends JPanel implements IModeFace {


    LeftRadioButton onSMRK, offSMRK, onSS, offSS, onSP, offSP, onTP, offTP, withCorrection, withoutCorrection;
    JTextField phiText, psiText, tethaText;
    LeftRadioButton tpxInc, tpxDec, tpyInc, tpyDec, tpzInc, tpzDec;
    JCheckBox tpx, tpy, tpz;
    JCheckBox ena_watch250K;
    JButton sendPacketButton, refreshingDataButton;
    Indicator onSmrkLump, onSsLump, readySpLump, errorSpSpeedLump, errorSpTimeLump, readyTpLump, errorTpTimeLump;
    GraphTextField  phiAngleText, psiAngleText, tethaAngleText;
    GraphTextField alphaAngleText,betaAngleText, gammaAngleText, alphaSensorText, betaSensorText, gammaSensorText, phiSensorText, psiSensorText, tethaSensorText;
    JTextField statusText;
    ExchangeModel exchangeModel;
    TerminalModel terminalModel;
    Indicator errorSpeedIndicatorUmrk;
    Indicator errorSpeedIndicatorSS;

    Boolean on = true;
    public MainModeFace (){

        terminalModel = Model.getTerminalModel();
        exchangeModel = Model.getExchangeModel();
        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());
        helper.setWeights(0.5f, 0.005f).fillBoth();

        ButtonGroup groupSMRK = new ButtonGroup();
        groupSMRK.add(onSMRK = new LeftRadioButton("On SMRK:"));
        groupSMRK.add(offSMRK = new LeftRadioButton("Off SMRK:"));
        offSMRK.setSelected(true);
        add(onSMRK, helper.get());
        add(offSMRK, helper.downRow().get());
        ButtonGroup groupSS = new ButtonGroup();
        groupSS.add(onSS = new LeftRadioButton("On SS:"));
        groupSS.add(offSS = new LeftRadioButton("Off SS:"));
        //offSS.setSelected(true);
        add(onSS, helper.nextColumn().get());
        add(offSS, helper.downRow().get());

        ButtonGroup groupSP = new ButtonGroup();
        groupSP.add(onSP = new LeftRadioButton("On SP:"));
        groupSP.add(offSP = new LeftRadioButton("Off SP:"));
        offSP.setSelected(true);
        add(onSP, helper.nextColumn().get());
        add(offSP, helper.downRow().get());
        ButtonGroup groupTP = new ButtonGroup();
        groupTP.add(onTP = new LeftRadioButton("On TP:"));
        groupTP.add(offTP = new LeftRadioButton("Off TP:"));
        offTP.setSelected(true);
        add(onTP, helper.nextColumn().get());
        add(offTP, helper.downRow().get());

        ButtonGroup groupCorrection = new ButtonGroup();
        groupCorrection.add(withCorrection = new LeftRadioButton("With Correction:"));
        groupCorrection.add(withoutCorrection = new LeftRadioButton("Without Correction:"));
       // withoutCorrection.setSelected(true);
        add(withoutCorrection, helper.nextRow().setGridWidth(2).get());
        add(withCorrection, helper.rightColumn().rightColumn().setGridWidth(2).get());


        //add(new JPanel(), helper.rightColumn().setGridWidth(1).setWeight(Integer.MAX_VALUE).get());

        JLabel phiLabel = new JLabel("PHI:");
        add(phiLabel, helper.setPosition(4,0).setGridWidth(1).setWeight(0.5f).get());
        add(phiText = new JTextField("0000"), helper.rightColumn().setWeight(1).get());
        phiText.setMinimumSize(new Dimension(50,20));
        add(new JLabel("PSI:"), helper.setPosition(4,1).setWeight(0.5f).get());
        add(psiText = new JTextField("0000"), helper.rightColumn().setWeight(1).get());
        psiText.setMaximumSize(new Dimension(50,20));
        add(new JLabel("TET:"), helper.setPosition(4,2).setWeight(0.5f).get());
        add(tethaText = new JTextField("0000"), helper.rightColumn().setWeight(1).get());
        tethaText.setMaximumSize(new Dimension(50,20));
        add(new JPanel(),  helper.rightColumn().setWeight(Integer.MAX_VALUE).get());

        ButtonGroup groupTPX = new ButtonGroup();
        groupTPX.add(tpxInc = new LeftRadioButton("TPX+"));
        groupTPX.add(tpxDec= new LeftRadioButton("TPX-"));
        add(tpxInc,  helper.setPosition(0, 3).setWeight(0.5f).get());
        add(tpxDec,  helper.downRow().get());
        add(tpx = new JCheckBox("TPX"), helper.downRow().get());
       // tpxDec.setSelected(true);

        ButtonGroup groupTPY = new ButtonGroup();
        groupTPY.add(tpyInc = new LeftRadioButton("TPY+"));
        groupTPY.add(tpyDec= new LeftRadioButton("TPY-"));
        add(tpyInc,  helper.setPosition(1, 3).setWeight(0.5f).get());
        add(tpyDec,  helper.downRow().get());
        add(tpy = new JCheckBox("TPY"), helper.downRow().get());
      //  tpyDec.setSelected(true);

        ButtonGroup groupTPZ = new ButtonGroup();
        groupTPZ.add(tpzInc = new LeftRadioButton("TPZ+"));
        groupTPZ.add(tpzDec= new LeftRadioButton("TPZ-"));
        add(tpzInc,  helper.setPosition(2, 3).setWeight(0.5f).get());
        add(tpzDec,  helper.downRow().get());
        add(tpz = new JCheckBox("TPZ"), helper.downRow().get());

        add(ena_watch250K = new JCheckBox("ena_watch"), helper.nextRow().setGridWidth(1).get());
        //


        //add(getTextFieldLabeled(alphaAngleText = new JTextField(""), "A:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(alphaAngleText = new GraphTextField("A:", new Color(255, 0, 0)), " A:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(alphaSensorText = new GraphTextField("SA:", new Color(255, 0, 0)), " SA:"), helper.rightColumn().get());
     //   add(getTextFieldLabeled(alphaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());
        add(getLumpLabeled(onSmrkLump = new Indicator(), "SMRK On"), helper.rightColumn().setGridWidth(2).get());



       // add(getTextFieldLabeled(betaAngleText = new JTextField(""), "B:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(betaAngleText = new GraphTextField("B:", new Color(58, 0, 255)), " B:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(betaSensorText = new GraphTextField("SB:", new Color(58, 0, 255)), " SB:"), helper.rightColumn().get());
       // add(getTextFieldLabeled(betaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());
        add(getLumpLabeled(onSsLump = new Indicator(), "SS On"), helper.rightColumn().setGridWidth(2).get());



     //   add(getTextFieldLabeled(gammaAngleText = new JTextField(""), "G:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(gammaAngleText = new GraphTextField("G:", new Color(88, 99, 0)), " G:"),  helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(gammaSensorText = new GraphTextField("SG:", new Color(88, 99, 0)), " SG:"), helper.rightColumn().get());
//        add(getTextFieldLabeled(gammaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());
        add(getLumpLabeled(readySpLump = new Indicator(), "Ready SP"), helper.rightColumn().setGridWidth(2).get());


       // add(getTextFieldLabeled(phiAngleText = new JTextField(""), "F:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(phiAngleText = new GraphTextField("F:", new Color(119, 61, 2)), " F:"),helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(phiSensorText = new GraphTextField("SF:", new Color(119, 61, 2)), " SF:"), helper.rightColumn().get());
       // add(getTextFieldLabeled(phiSensorText = new JTextField(""), "S:"), helper.rightColumn().get());
        add(getLumpLabeled(errorSpSpeedLump = new Indicator(), "Error for speed SP"), helper.rightColumn().setGridWidth(2).get());



        //add(getTextFieldLabeled(psiAngleText = new JTextField(""), "P:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(psiAngleText = new GraphTextField("P:", new Color(109, 10, 102)), " P:"),  helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(psiSensorText = new GraphTextField("SP:", new Color(98, 2, 90)), " SP:"), helper.rightColumn().get());
       // add(getTextFieldLabeled(psiSensorText = new JTextField(""), "S:"), helper.rightColumn().get());
        add(getLumpLabeled(errorSpTimeLump = new Indicator(), "Error for time SP"), helper.rightColumn().setGridWidth(2).get());


        //add(getTextFieldLabeled(tethaAngleText = new JTextField(""), "T:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(tethaAngleText = new GraphTextField("T:", new Color(0, 126, 63)), " T:"),  helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(tethaSensorText = new GraphTextField("ST:", new Color(0, 126, 63)), " ST:"), helper.rightColumn().get());
        //add(getTextFieldLabeled(tethaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());
        add(getLumpLabeled(readyTpLump = new Indicator(), "Ready TP"), helper.rightColumn().setGridWidth(2).get());

        add(new JLabel( "Status:"), helper.nextRow().setGridWidth(1).get());
        add(statusText = new JTextField(""), helper.rightColumn().get());
        add(getLumpLabeled(errorTpTimeLump = new Indicator(), "Error for time TP"), helper.rightColumn().setGridWidth(2).get());


        add(AppFrameHelper.getLumpLabeled(errorSpeedIndicatorUmrk = new Indicator(), "Error umrk"), helper.nextRow().setGridWidth(1).get());
        add(AppFrameHelper.getLumpLabeled(errorSpeedIndicatorSS= new Indicator(), "Error ss"), helper.nextRow().setGridWidth(1).get());



        add(AppFrameHelper.createPanel("free"), helper.nextRow().setGridWidth(7).setWeights(0.5f, 0.9f).get());

        terminalModel.addFace(CebExchangeMode.MAIN_MODE, this);
        exchangeModel.addCebModeEventListener(CebExchangeMode.MAIN_MODE, this);


    }


    private JPanel getLumpLabeled(Indicator lump, String label){
        JPanel  panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel comment= new JLabel(label);
        panel.add(lump);
        panel.add(comment);
        return panel;
    }

    private JPanel getTextFieldLabeled(JTextField textField,String label){
        return  AppFrameHelper.getTextFieldLabeled(textField, label,30, 50);
    }

    @Override
    public byte[] createRequest(){



        byte []packet= new byte[2];



        boolean [] arg0  = {
                onSMRK.isSelected(),
                false,//offSMRK.isSelected(),
                onSS.isSelected(),
                onSP.isSelected() ? false : offSS.isSelected(),
                onSP.isSelected(),
                false,//offSP.isSelected(),
                onTP.isSelected(),
                offTP.isSelected()
           };
            packet[0] = PacketHelper.bool2byte(arg0);
     //  packet[0] = (byte)0x80;
     //   packet[1] = (byte)0x14;

        boolean [] arg1  = {
                withCorrection.isSelected(),
                withoutCorrection.isSelected(),
                false,
                false,
                ena_watch250K.isSelected(),
                false,
                false,
                false
        };
         packet[1] = PacketHelper.bool2byte(arg1);



        Integer phiUst = Integer.parseInt(phiText.getText(), 16);
        packet = PacketHelper.addDataToPacket(packet, phiUst);

        Integer psiUst = Integer.parseInt(psiText.getText(), 16);
        packet = PacketHelper.addDataToPacket(packet, psiUst);

        Integer tethaUst = Integer.parseInt(tethaText.getText(), 16);
        packet = PacketHelper.addDataToPacket(packet, tethaUst);

        boolean [] arg2  = {
                tpxInc.isSelected(),
                tpxDec.isSelected(),
                tpx.isSelected(),
                tpyInc.isSelected(),
                tpyDec.isSelected(),
                tpy.isSelected(),
                false,
                false
        };

        byte byte2 = PacketHelper.bool2byte(arg2);
        packet = PacketHelper.addDataToPacket(packet, byte2);

        boolean [] arg3  = {
                tpzInc.isSelected(),
                tpzDec.isSelected(),
                tpz.isSelected(),
                false,
                false,
                false,
                false,
                false
        };

        byte byte3 = PacketHelper.bool2byte(arg3);
        packet = PacketHelper.addDataToPacket(packet, byte3);

        return packet;

    }



    @Override
    public void refreshDataOnFace() {
        byte []response = exchangeModel.getResponse();
        //if(response.length != 50){
        //    System.out.println("Length response from CEB not equal 43! ");
       //     return;
       // }
        byte []resp  =PacketHelper.extractCebPacket(exchangeModel.getResponse());




        alphaSensorText.setText(getSensor(resp[4], resp[5]));
        betaSensorText.setText(getSensor(resp[6], resp[7]));
        gammaSensorText.setText(getSensor(resp[8], resp[9]));
        phiSensorText.setText(getSensor(resp[10], resp[11]));
        psiSensorText.setText(getSensor(resp[12], resp[13]));
        tethaSensorText.setText(getSensor(resp[14], resp[15]));


        alphaAngleText.setText(getAngle19(resp[18], resp[19], resp[20]));
        betaAngleText.setText(getAngle19(resp[22], resp[23], resp[24]));
        gammaAngleText.setText(getAngle19(resp[26], resp[27], resp[28]));
        phiAngleText.setText(PacketHelper.getAngle22  (resp[30], resp[31], resp[32]));
        psiAngleText.setText(PacketHelper.getAngle22  (resp[34], resp[35], resp[36]));
        tethaAngleText.setText(PacketHelper.getAngle22(resp[38], resp[39], resp[40]));


        byte modes = resp[2];
        byte modes2 = resp[3];
        onSmrkLump.refresh(PacketHelper.getBitFromByte(modes, 0));
        onSsLump.refresh(PacketHelper.getBitFromByte(modes,1));
        readySpLump.refresh(PacketHelper.getBitFromByte(modes,2));

        errorSpSpeedLump.refresh(PacketHelper.getBitFromByte(modes,3));
        errorSpTimeLump.refresh(PacketHelper.getBitFromByte(modes,4));
        readyTpLump.refresh(PacketHelper.getBitFromByte(modes,5));
        errorTpTimeLump.refresh(PacketHelper.getBitFromByte(modes,6));
        errorSpeedIndicatorUmrk.refresh(!PacketHelper.getBitFromByte(modes2, 0));
        errorSpeedIndicatorSS.refresh(!PacketHelper.getBitFromByte(modes2, 1));

        if (Model.flagQueue) {
            synchronized (Model.pointData) {
                try {
                    Model.pointData.wait();
                    Model.pointData.addPointPackage();
                    alphaSensorText.addPoint(getSensorDouble(resp[4], resp[5]));
                    betaSensorText.addPoint(getSensorDouble(resp[6], resp[7]));
                    gammaSensorText.addPoint(getSensorDouble(resp[8], resp[9]));
                    phiSensorText.addPoint(getSensorDouble(resp[10], resp[11]));
                    psiSensorText.addPoint(getSensorDouble(resp[12], resp[13]));
                    tethaSensorText.addPoint(getSensorDouble(resp[14], resp[15]));
                    alphaAngleText.addPoint(Double.parseDouble(getAngle19int(resp[18], resp[19], resp[20])));
                    betaAngleText.addPoint(Double.parseDouble(getAngle19int(resp[22], resp[23], resp[24])));
                    gammaAngleText.addPoint(Double.parseDouble(getAngle19int(resp[26], resp[27], resp[28])));
                    phiAngleText.addPoint(Double.parseDouble(getAngle19int  (resp[30], resp[31], resp[32])));
                    psiAngleText.addPoint(Double.parseDouble(getAngle19int  (resp[34], resp[35], resp[36])));
                    tethaAngleText.addPoint(Double.parseDouble(getAngle19int(resp[38], resp[39], resp[40])));
                    // recWord3.addPoint(getSensorDouble(resp[4], resp[5]));
                } catch (InterruptedException ex) {
                    System.err.println("own:: Interrupted: " + ex.getMessage());
                }
            }
        }



        this.repaint();
        this.revalidate();

        //this.repaint();
    }

    public static String getAngle19(byte low_byte, byte high_byte, byte high_bit6){
        Integer angle = (((int)high_bit6) << 16) & 0x070000| (((int)high_byte) << 8 ) & 0xFF00|((int) low_byte & 0xFF);
        return Integer.toHexString((angle) | 0x100000).substring(1);
    }

    public static String getAngle19int(byte low_byte, byte high_byte, byte high_bit6){
        Integer angle = (((int)high_bit6) << 16) & 0x070000| (((int)high_byte) << 8 ) & 0xFF00|((int) low_byte & 0xFF);
        return Integer.toString((angle) | 0x100000).substring(1);
    }


    private Double getSensorDouble(byte low_byte, byte high_byte) {
        return PacketHelper.getSensorDouble(low_byte, high_byte);
    }

    private String getSensor(byte low_byte, byte high_byte){
        return  PacketHelper.getSensor(low_byte, high_byte);
    }
}
