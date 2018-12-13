package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;
import pro.filatov.workstation4ceb.model.fpga.Terminal.VerticalSlider12bit;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 * Created by yuri.filatov on 09.09.2016.
 */
public class UstpModeFace  extends JPanel implements IModeFace {


    LeftRadioButton tpxInc, tpxDec, tpyInc, tpyDec, tpzInc, tpzDec;
    JCheckBox tpx, tpy, tpz;
    VerticalSlider12bit xSlider, ySlider, zSlider;
    JTextField xValue, yValue, zValue;

    TerminalModel terminalModel;

    public UstpModeFace(){

        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());
        helper.setWeights(0.5f, 0.005f).fillBoth();

        ButtonGroup groupTPX = new ButtonGroup();
        groupTPX.add(tpxInc = new LeftRadioButton("TPX+"));
        groupTPX.add(tpxDec= new LeftRadioButton("TPX-"));
        add(tpxInc,  helper.get());
        add(tpxDec,  helper.downRow().get());
        add(tpx = new JCheckBox("TPX"), helper.downRow().get());
       // tpxDec.setSelected(true);

        ButtonGroup groupTPY = new ButtonGroup();
        groupTPY.add(tpyInc = new LeftRadioButton("TPY+"));
        groupTPY.add(tpyDec= new LeftRadioButton("TPY-"));
        add(tpyInc,  helper.nextColumn().get());
        add(tpyDec,  helper.downRow().get());
        add(tpy = new JCheckBox("TPY"), helper.downRow().get());
       // tpyDec.setSelected(true);

        ButtonGroup groupTPZ = new ButtonGroup();
        groupTPZ.add(tpzInc = new LeftRadioButton("TPZ+"));
        groupTPZ.add(tpzDec= new LeftRadioButton("TPZ-"));
        add(tpzInc,  helper.nextColumn().get());
        add(tpzDec,  helper.downRow().get());
        add(tpz = new JCheckBox("TPZ"), helper.downRow().get());
        //tpzDec.setSelected(true);


        add(createTextField(xValue = new JTextField("0"), "X:"), helper.nextRow().get());

        xValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int v = Integer.parseInt(xValue.getText());
                if(v > 2047){
                    v = 2047;
                }else if(v < -2048){
                    v = -2048;
                }
               xValue.setText(String.valueOf(v));
                xSlider.setValue(v);
            }
        });


        add(createTextField(yValue = new JTextField("0"), "Y:"), helper.rightColumn().get());

        yValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int v = Integer.parseInt(yValue.getText());
                if(v > 2047){
                    v = 2047;
                }else if(v < -2048){
                    v = -2048;
                }
                yValue.setText(String.valueOf(v));
                ySlider.setValue(v);
            }
        });



        add(createTextField(zValue = new JTextField("0"), "Z:"),  helper.rightColumn().get());

        zValue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int v = Integer.parseInt(zValue.getText());
                if(v > 2047){
                    v = 2047;
                }else if(v < -2048){
                    v = -2048;
                }
                zValue.setText(String.valueOf(v));
                zSlider.setValue(v);
            }
        });



        add(xSlider = new VerticalSlider12bit(xValue), helper.nextRow().get());
        add(ySlider = new VerticalSlider12bit(yValue), helper.rightColumn().get());
        add(zSlider = new VerticalSlider12bit(zValue), helper.rightColumn().get());
        add(AppFrameHelper.createPanel("free"), helper.nextRow().setGridWidth(3).setWeights(0.5f, 0.9f).get());

        terminalModel = Model.getTerminalModel();

        terminalModel.addFace(CebExchangeMode.USTP_MODE, this);

    }


    @Override
   public  byte[] createRequest(){

        byte []packet= new byte[1];

        boolean [] arg2  = {
                tpx.isSelected() && tpxInc.isSelected(),
                tpx.isSelected() && tpxDec.isSelected(),
                false,//tpx.isSelected(),
                tpy.isSelected() && tpyInc.isSelected(),
                tpy.isSelected() && tpyDec.isSelected(),
                false,//tpy.isSelected(),
                tpx.isSelected() || tpy.isSelected() || tpy.isSelected(),
                false
        };

        byte byte2 = PacketHelper.bool2byte(arg2);
        packet[0] =  byte2;

        boolean [] arg3  = {
                tpz.isSelected() && tpzInc.isSelected(),
                tpz.isSelected() && tpzDec.isSelected(),
                false,//tpz.isSelected(),
                false,
                false,
                false,
                false,
                false
        };



        byte byte3 = PacketHelper.bool2byte(arg3);
        packet = PacketHelper.addDataToPacket(packet, byte3);

        Integer x = Integer.parseInt(xValue.getText());
        packet = PacketHelper.addDataToPacket(packet, x);

        Integer y = Integer.parseInt(yValue.getText());
        packet = PacketHelper.addDataToPacket(packet, y);

        Integer z = Integer.parseInt(zValue.getText());
        packet = PacketHelper.addDataToPacket(packet, z);

        return packet;

    }


    private JPanel createTextField(JTextField textField,String label){
        return AppFrameHelper.getTextFieldLabeled(textField, label, 60, 50);
    }


    @Override
    public void refreshDataOnFace() {

    }
}
