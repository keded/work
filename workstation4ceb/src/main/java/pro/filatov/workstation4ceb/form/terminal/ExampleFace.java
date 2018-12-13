package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
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
public class ExampleFace extends JPanel implements IModeFace {


    LeftRadioButton onSMRK, offSMRK, onSS, offSS, onSP, offSP, onTP, offTP, withCorrection, withoutCorrection;
    JTextField phiText, psiText, tethaText;
    LeftRadioButton tpxInc, tpxDec, tpyInc, tpyDec, tpzInc, tpzDec;
    JCheckBox tpx, tpy, tpz;
    JCheckBox ena_watch250K;
    Indicator onSmrkLump, onSsLump, readySpLump, errorSpSpeedLump, errorSpTimeLump, readyTpLump, errorTpTimeLump;
    JTextField alphaAngleText, betaAngleText, gammaAngleText, phiAngleText, psiAngleText, tethaAngleText;
    JTextField alphaSensorText, betaSensorText, gammaSensorText, phiSensorText, psiSensorText, tethaSensorText;
    JTextField statusText;
    ExchangeModel exchangeModel;
    TerminalModel terminalModel;
    Indicator errorSpeedIndicatorUmrk;
    Indicator errorSpeedIndicatorSS;

    Boolean on = true;
    public ExampleFace(){

        terminalModel = Model.getTerminalModel();
        exchangeModel = Model.getExchangeModel();
        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());

        helper.setWeight(0.5f);
        add(ena_watch250K = new JCheckBox("ena_watch"), helper.nextRow().setGridWidth(1).get());

        add(getTextFieldLabeled(alphaAngleText = new JTextField(""), "A:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(alphaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());



        add(getTextFieldLabeled(betaAngleText = new JTextField(""), "B:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(betaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());


        add(getTextFieldLabeled(gammaAngleText = new JTextField(""), "G:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(gammaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());


        add(getTextFieldLabeled(phiAngleText = new JTextField(""), "F:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(phiSensorText = new JTextField(""), "S:"), helper.rightColumn().get());


        add(getTextFieldLabeled(psiAngleText = new JTextField(""), "P:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(psiSensorText = new JTextField(""), "S:"), helper.rightColumn().get());

        add(getTextFieldLabeled(tethaAngleText = new JTextField(""), "T:"), helper.nextRow().setGridWidth(1).get());
        add(getTextFieldLabeled(tethaSensorText = new JTextField(""), "S:"), helper.rightColumn().get());


        helper.setWeights(0.5f, 0.005f).fillBoth();
        add(AppFrameHelper.createPanel("free"), helper.nextRow().setGridWidth(7).setWeights(0.5f, 0.9f).get());

        terminalModel.addFace(CebExchangeMode.EXAMPLE_FACE, this);
        exchangeModel.addCebModeEventListener(CebExchangeMode.EXAMPLE_FACE, this);


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
        return  AppFrameHelper.getTextFieldLabeled(textField, label,20, 40);
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


        this.repaint();
        this.revalidate();

        //this.repaint();
    }




    private String getSensor(byte low_byte, byte high_byte){
        return  PacketHelper.getSensor(low_byte, high_byte);
    }
}
