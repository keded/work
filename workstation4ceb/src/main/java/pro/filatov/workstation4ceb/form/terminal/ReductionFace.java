package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.AppFrame;
import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.terminal.ColorColumnRenderer;
import pro.filatov.workstation4ceb.form.terminal.GridBagHelper;
import pro.filatov.workstation4ceb.form.terminal.IModeFace;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by yuri.filatov on 16.09.2016.
 */
public class ReductionFace extends JPanel implements IModeFace {

    JCheckBox  powerALL;
    JCheckBox  powerF, powerP, powerT;
    JCheckBox modereduction, modespeed;

    JTextField phiTextField, psiTextField, thetaTextField;
    JTextField phiactTextField, psiactTextField, thetaactTextField;
    JTextField speedcurphiTF, speedcurpsiTF, speedcurthetaTF;
    JTextField speedmaxphiTF, speedmaxpsiTF, speedmaxthetaTF;
    JTextField deltaphiTF, deltapsiTF, deltathetaTF;
    JTextField npwmTextField, deadTextField, nmaxTextField;
    JTextField uqphiTF, uqpsiTF, uqthetaTF;
    JTextField flagsregTF;
    JTextField kafTF, kapTF, katTF, kofTF, kopTF, kotTF, dafTF, dapTF, datTF;
    Indicator flag0, flag1, flag2, flag3, flag4, flag5, flag6, flag7;

    private JTable table;

    ExchangeModel exchangeModel;
    TerminalModel terminalModel;
    Color currentColor;



    public ReductionFace() {

        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());
        helper.setWeights(0.33f, 0.01f).fillBoth();
        currentColor = this.getBackground ();

        modereduction = AppFrameHelper.createLeftCheckBox("Reduction");
        modereduction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modespeed.setSelected(false);
                modespeed.setEnabled(!modereduction.isSelected());
                speedmaxphiTF.setEnabled(!modereduction.isSelected());
                speedmaxpsiTF.setEnabled(!modereduction.isSelected());
                speedmaxthetaTF.setEnabled(!modereduction.isSelected());
                powerALL.setSelected(false);
                powerALL.setBackground(currentColor);
                powerALL.setEnabled(modespeed.isSelected()|modereduction.isSelected());
            }
        });
        add(modereduction, helper.nextRow().get());

        modespeed = AppFrameHelper.createLeftCheckBox("Speed");
        modespeed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modereduction.setSelected(false);
                modereduction.setEnabled(!modespeed.isSelected());
                phiTextField.setEnabled(!modespeed.isSelected());
                psiTextField.setEnabled(!modespeed.isSelected());
                thetaTextField.setEnabled(!modespeed.isSelected());
                powerALL.setSelected(false);
                powerALL.setBackground(currentColor);
                powerALL.setEnabled(modespeed.isSelected()|modereduction.isSelected());
            }
        });
        add(modespeed, helper.rightColumn().get());

        powerALL = AppFrameHelper.createLeftCheckBox("Power");
        powerALL.setEnabled(modespeed.isSelected()|modereduction.isSelected());
        powerALL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                powerALL.setBackground(powerALL.isSelected()?Color.RED:currentColor);
            }
        });
        add(powerALL, helper.nextRow().get());


        npwmTextField = new JTextField("1950");
        npwmTextField.setBackground(Color.CYAN);
        npwmTextField.setEditable(true);
        add(AppFrameHelper.getTextFieldLabeled( npwmTextField, "Npwm:", 80, 60), helper.nextRow().get());

        deadTextField = new JTextField("50");
        deadTextField.setBackground(Color.CYAN);
        deadTextField.setEditable(true);
        add(AppFrameHelper.getTextFieldLabeled( deadTextField, "Dead-time:", 80, 60), helper.nextRow().get());

        nmaxTextField = new JTextField("1200");
        nmaxTextField.setBackground(Color.CYAN);
        add(AppFrameHelper.getTextFieldLabeled( nmaxTextField, "Nmax:", 80, 60), helper.nextRow().get());


        powerF = AppFrameHelper.createLeftCheckBox("___________PHI:");
        add(powerF, helper.nextRow().get());

        speedmaxphiTF = new JTextField("300");
        add(AppFrameHelper.getTextFieldLabeled(speedmaxphiTF, "SpeedMax:",  80, 50), helper.rightColumn().get());

        speedcurphiTF = new JTextField("0");
        speedcurphiTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(speedcurphiTF, "SpeedAct:",  80, 50), helper.rightColumn().get());

        phiTextField = new JTextField("0");
        add(AppFrameHelper.getTextFieldLabeled(phiTextField, "Target:",  80, 50), helper.rightColumn().get());

        phiactTextField = new JTextField("0");
        phiactTextField.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(phiactTextField, "Actual:", 80, 50), helper.rightColumn().get());

        deltaphiTF = new JTextField("0");
        deltaphiTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled( deltaphiTF, "Delta:", 80, 50), helper.rightColumn().get());

        uqphiTF = new JTextField("0");
        uqphiTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(uqphiTF, "Uq:", 80, 50), helper.rightColumn().get());


        powerP = AppFrameHelper.createLeftCheckBox("___________PSI:");
        add(powerP, helper.nextRow().get());

        speedmaxpsiTF = new JTextField("300");
        add(AppFrameHelper.getTextFieldLabeled(speedmaxpsiTF, "SpeedMax:",  80, 50), helper.rightColumn().get());

        speedcurpsiTF = new JTextField("0");
        speedcurpsiTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(speedcurpsiTF, "SpeedAct:",  80, 50), helper.rightColumn().get());

        psiTextField = new JTextField("0");
        add(AppFrameHelper.getTextFieldLabeled(psiTextField, "Target:", 80, 50), helper.rightColumn().get());

        psiactTextField = new JTextField("0");
        psiactTextField.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(psiactTextField, "Actual:", 80, 50), helper.rightColumn().get());

        deltapsiTF = new JTextField("0");
        deltapsiTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled( deltapsiTF, "Delta:", 80, 50), helper.rightColumn().get());

        uqpsiTF = new JTextField("0");
        uqpsiTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(uqpsiTF, "Uq:", 80, 50), helper.rightColumn().get());


        powerT = AppFrameHelper.createLeftCheckBox("________THETA:");
        add(powerT, helper.nextRow().get());

        speedmaxthetaTF = new JTextField("300");
        add(AppFrameHelper.getTextFieldLabeled(speedmaxthetaTF, "SpeedMax:",  80, 50), helper.rightColumn().get());

        speedcurthetaTF = new JTextField("0");
        speedcurthetaTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(speedcurthetaTF, "SpeedAct:",  80, 50), helper.rightColumn().get());

        thetaTextField = new JTextField("0");
        add(AppFrameHelper.getTextFieldLabeled(thetaTextField, "Target:", 80, 50), helper.rightColumn().get());

        thetaactTextField = new JTextField("0");
        thetaactTextField.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(thetaactTextField, "Actual:", 80, 50), helper.rightColumn().get());

        deltathetaTF = new JTextField("0");
        deltathetaTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled( deltathetaTF, "Delta:", 80, 50), helper.rightColumn().get());

        uqthetaTF = new JTextField("0");
        uqthetaTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled(uqthetaTF, "Uq:", 80, 50), helper.rightColumn().get());


        flagsregTF = new JTextField("0");
        flagsregTF.setEditable(false);
        add(AppFrameHelper.getTextFieldLabeled( flagsregTF, "FLAGS_debug:", 80, 60), helper.nextRow().get());

        //helper.nextRow();

        add(getLumpLabeled(flag0 = new Indicator(), "SPEED_ERROR"), helper.nextRow().get());
        add(getLumpLabeled(flag1 = new Indicator(), "TIME_ERROR"), helper.nextRow().get());
        add(getLumpLabeled(flag2 = new Indicator(), "FATAL_ERROR"), helper.nextRow().get());
        add(getLumpLabeled(flag3 = new Indicator(), "READY"), helper.nextRow().get());

        kafTF = new JTextField("21954");
        add(AppFrameHelper.getTextFieldLabeled(kafTF, "kAlphaPHI:", 80, 60), helper.nextRow().get());
        kapTF = new JTextField("21954");
        add(AppFrameHelper.getTextFieldLabeled(kapTF, "kAlphaPSI:", 80, 60), helper.rightColumn().get());
        katTF = new JTextField("21954");
        add(AppFrameHelper.getTextFieldLabeled(katTF, "kAlphaTHETA:", 80, 60), helper.rightColumn().get());

        kofTF = new JTextField("6226");
        add(AppFrameHelper.getTextFieldLabeled(kofTF, "kOmegaPHI:", 80, 60), helper.nextRow().get());
        kopTF = new JTextField("6226");
        add(AppFrameHelper.getTextFieldLabeled(kopTF, "kOmegaPSI:", 80, 60), helper.rightColumn().get());
        kotTF = new JTextField("4587");
        add(AppFrameHelper.getTextFieldLabeled(kotTF, "kOmegaTHETA:", 80, 60), helper.rightColumn().get());

        dafTF = new JTextField("364");
        add(AppFrameHelper.getTextFieldLabeled(dafTF, "deltaPHI:", 80, 60), helper.nextRow().get());
        dapTF = new JTextField("364");
        add(AppFrameHelper.getTextFieldLabeled(dapTF, "deltaPSI:", 80, 60), helper.rightColumn().get());
        datTF = new JTextField("364");
        add(AppFrameHelper.getTextFieldLabeled(datTF, "deltaTHETA:", 80, 60), helper.rightColumn().get());

        add(AppFrameHelper.createPanelWithoutLabel("free"), helper.nextRow().setGridWidth(9).setWeights(0.5f, 0.9f).get());

        exchangeModel = Model.getExchangeModel();
        terminalModel = Model.getTerminalModel();


        terminalModel.addFace(CebExchangeMode.REDUCTION, this);
        exchangeModel.addCebModeEventListener(CebExchangeMode.REDUCTION, this);

    }


    private String[] createRowNameData(String name){
        String [] rowData  = new String[7];
        rowData[0] = name;
        return rowData;
    }

    private JPanel getLumpLabeled(Indicator lump, String label){
        JPanel  panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel comment= new JLabel(label);
        panel.add(lump);
        panel.add(comment);
        return panel;
    }

    @Override
    public byte[] createRequest() {
        byte []packet= new byte[1];

        boolean [] arg0  = {
                ( powerF.isSelected() & powerALL.isSelected() ),
                ( powerP.isSelected() & powerALL.isSelected() ),
                ( powerT.isSelected() & powerALL.isSelected() )
        };
        packet[0] = PacketHelper.bool2byte(arg0);
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);

        boolean [] powerALLLetter = { powerALL.isSelected()};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(powerALLLetter));
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);

        Integer speedmaxPetr = 15000;
        packet = PacketHelper.addDataToPacket(packet, speedmaxPetr);

        boolean [] modered  = {
                modereduction.isSelected(),
                modespeed.isSelected()
        };
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(modered));
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);

        Integer reserv1 = 0;
        packet = PacketHelper.addDataToPacket(packet, reserv1);

        Integer npwm = Integer.parseInt(npwmTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, npwm);

        Integer dead  = Integer.parseInt(deadTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, dead);

        Integer phi  = Integer.parseInt(phiTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, phi);
        Integer psi  = Integer.parseInt(psiTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, psi);
        Integer theta  = Integer.parseInt(thetaTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, theta);
        Integer nmax = Integer.parseInt(nmaxTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, nmax);

        Integer speedmaxphi = Integer.parseInt(speedmaxphiTF.getText());
        packet = PacketHelper.addDataToPacket(packet, speedmaxphi);
        Integer speedmaxpsi = Integer.parseInt(speedmaxpsiTF.getText());
        packet = PacketHelper.addDataToPacket(packet, speedmaxpsi);
        Integer speedmaxtheta = Integer.parseInt(speedmaxthetaTF.getText());
        packet = PacketHelper.addDataToPacket(packet, speedmaxtheta);

        Integer kaf = Integer.parseInt(kafTF.getText());
        packet = PacketHelper.addDataToPacket(packet, kaf);
        Integer kap = Integer.parseInt(kapTF.getText());
        packet = PacketHelper.addDataToPacket(packet, kap);
        Integer kat = Integer.parseInt(katTF.getText());
        packet = PacketHelper.addDataToPacket(packet, kat);

        Integer kof = Integer.parseInt(kofTF.getText());
        packet = PacketHelper.addDataToPacket(packet, kof);
        Integer kop = Integer.parseInt(kopTF.getText());
        packet = PacketHelper.addDataToPacket(packet, kop);
        Integer kot = Integer.parseInt(kotTF.getText());
        packet = PacketHelper.addDataToPacket(packet, kot);

        Integer daf = Integer.parseInt(dafTF.getText());
        packet = PacketHelper.addDataToPacket(packet, daf);
        Integer dap = Integer.parseInt(dapTF.getText());
        packet = PacketHelper.addDataToPacket(packet, dap);
        Integer dat = Integer.parseInt(datTF.getText());
        packet = PacketHelper.addDataToPacket(packet, dat);

        return packet;
    }


    @Override

    public void refreshDataOnFace() {
        byte []response = exchangeModel.getResponse();
        if(response.length == 0 | response.length < 65 ){//| response.length != 70
            //System.out.println("Length response from CEB less than 64! ");
            return;
        }
        byte []resp  =PacketHelper.extractCebPacket(response);
        byte modes = resp[2];

        
        flag0.refresh(PacketHelper.getBitFromByte(modes, 1)); // SPEED_ERROR
        flag1.refresh(PacketHelper.getBitFromByte(modes, 2)); // TIME_ERROR
        flag2.refresh(PacketHelper.getBitFromByte(modes, 3)); // FATAL_ERROR
        flag3.refresh(PacketHelper.getBitFromByte(modes, 0)); // READY

        phiactTextField.setText(PacketHelper.getUnsignedWord16bit(resp[10], resp[11]));
        psiactTextField.setText(PacketHelper.getUnsignedWord16bit(resp[12], resp[13]));
        thetaactTextField.setText(PacketHelper.getUnsignedWord16bit(resp[14], resp[15]));

        deltaphiTF.setText(PacketHelper.getSignedValue16bit(resp[4], resp[5]));
        deltapsiTF.setText(PacketHelper.getSignedValue16bit(resp[6], resp[7]));
        deltathetaTF.setText(PacketHelper.getSignedValue16bit(resp[8], resp[9]));

        uqphiTF.setText(PacketHelper.getSignedValue16bit(resp[22], resp[23]));
        uqpsiTF.setText(PacketHelper.getSignedValue16bit(resp[24], resp[25]));
        uqthetaTF.setText(PacketHelper.getSignedValue16bit(resp[26], resp[27]));

        flagsregTF.setText(PacketHelper.getUnsignedWord16bit(resp[2], resp[3]));

        speedcurphiTF.setText(PacketHelper.getSignedValue16bit(resp[28], resp[29]));
        speedcurpsiTF.setText(PacketHelper.getSignedValue16bit(resp[30], resp[31]));
        speedcurthetaTF.setText(PacketHelper.getSignedValue16bit(resp[32], resp[33]));

        if (resp[16] == 0) {phiactTextField.setBackground(currentColor);}
            else phiactTextField.setBackground(Color.GREEN);
        if (resp[18] == 0) {psiactTextField.setBackground(currentColor);}
            else psiactTextField.setBackground(Color.GREEN);
        if (resp[20] == 0) {thetaactTextField.setBackground(currentColor);}
            else thetaactTextField.setBackground(Color.GREEN);
        this.repaint();
        this.revalidate();
    }

        private String getSensor(byte low_byte, byte high_byte){
        return  PacketHelper.getSensor(low_byte, high_byte);
    }



}
