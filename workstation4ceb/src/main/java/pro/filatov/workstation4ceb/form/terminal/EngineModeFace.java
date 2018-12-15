package pro.filatov.workstation4ceb.form.terminal;


import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.terminal.graph.GraphTextField;
import pro.filatov.workstation4ceb.form.terminal.graph.HistoryTextField;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.*;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuri.filatov on 16.09.2016.
 */
public class EngineModeFace extends JPanel implements IModeFace {


    private JPanel grahButtonPanel;
    public JPanel getGrahButtonPanel() {
        return grahButtonPanel;
    }


    LeftRadioButton enableKPUtoSHIM, enableSTEP, enableConst;
    JTextField uqTextField, udTextField, constInitAngle, deathTextField, nPwmTextField, stepTextField;
    JTextField rel_go_textfield, rel_fhv_textfield, coefFHVLowRate;
    JTextField rateADC, rel_to__gi_textfield, rel_to_dk_textfield, dcCoef;
    JTextField phaseSpi;
    JTextField limitUqTextField;
    JCheckBox powerKEYS, enableMatching, errorCorrection, enableCalcUqToShim, enableUMRK, enableSS, enaIntegrator;
    JCheckBox direct_go, is_filtering, scl_to_mosi;
    JButton savePhases;
    JTextField fhvTOdelitel, porogFHVgoOrTo;
    Indicator errorSpeedIndicatorUmrk;
    Indicator errorSpeedIndicatorSS;
    Map<DataRamWord, JTextField> textFieldsPhases;
    

    VerticalSlider12bit phaseSlider;
    LeftRadioButton runCapturingRadio, stopCapturingRadio, clearStorageRadio;
    JTextField captureDelayTextField;
    JCheckBox clearSpeedError;

    List<Integer> selectedPhases;
    List<LeftRadioButton> radioPhases;
    List<JCheckBox> checkboxChanels;
    JTextField   angle19bit, fhvTO, prev_go, error_go, calcUq, calcUd;
    GraphTextField directAngle;
    GraphTextField sinGO, cosGO, sinTO, cosTO, fhvGO, resultAngle;
    List<LeftRadioButton> radioChannels;
    JTextField result_calc_uq;
    GraphTextField speedTethaTextField;
    private JTable table;

    ExchangeModel exchangeModel;
    TerminalModel terminalModel;


    public EngineModeFace() {

        grahButtonPanel = new JPanel();

        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());
        helper.setWeights(0.33f, 0.06f).fillBoth();

        // ActionListener chooseChannelsActionListener = new ActionListener() {
        // @Override
        //public void actionPerformed(ActionEvent e) {
        //   Phase phase = Phase.getPhase (((LeftRadioButton)e.getSource()).getMnemonic());
        //  System.out.println("Selected phase " + phase.getNumber() + " "+ phase.name() + ", for channel " + String.valueOf(getSelectedChannel()));
        // setPhaseForCurrentChannel(phase);
        //}
        //};
        add(new JLabel("Channels:"), helper.setGridWidth(1).nextRow().get());
        String[] phasesNames = {"A", "B", "G", "F", "P", "T"};
        checkboxChanels = new ArrayList<>();
        //ButtonGroup groupPhases = new ButtonGroup();
        for (int i = 0; i <= 5; i++) {
            JCheckBox channel = AppFrameHelper.createLeftCheckBox(phasesNames[i] + ":");
            checkboxChanels.add(channel);
            channel.setMnemonic(i);
            //  channel.addActionL0istener(chooseChannelsActionListener);
            add(channel, helper.rightColumn().get());
        }
        helper.nextRow();

        powerKEYS = AppFrameHelper.createLeftCheckBox("Power keys:");
        add(powerKEYS, helper.setGridWidth(2).get());
        enableUMRK = AppFrameHelper.createLeftCheckBox("Enable UMRK:");
        add(enableUMRK, helper.rightColumn(2).setGridWidth(2).get());
        enableSS = AppFrameHelper.createLeftCheckBox("Enable SS:");
        add(enableSS, helper.rightColumn(2).setGridWidth(2).get());
        enaIntegrator = AppFrameHelper.createLeftCheckBox("Integrator:");
        add(enaIntegrator, helper.rightColumn(2).setGridWidth(2).get());

        radioChannels = new ArrayList<>();
        add(new JLabel("ChannelForPhases:"), helper.setGridWidth(1).nextRow().get());
        ButtonGroup groupChannels = new ButtonGroup();

        ActionListener chClickListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LeftRadioButton radio = (LeftRadioButton) e.getSource();
                int ch = radio.getMnemonic();
                System.out.println("Selected channel " + String.valueOf(ChannelForPhases.getChannel(ch).getLongName()));
                clearSelectPhase();
                radioPhases.get(selectedPhases.get(ch)).setSelected(true);

            }
        };


        for (int i = 0; i <= 5; i++) {
            LeftRadioButton radioButton = new LeftRadioButton(ChannelForPhases.getChannel(i).getShortName() + ":");
            radioButton.setMnemonic(i);
            groupChannels.add(radioButton);
            radioButton.addActionListener(chClickListener);
            add(radioButton, helper.rightColumn().get());
            radioChannels.add(radioButton);
        }

        radioChannels.get(0).setSelected(true);

        add(new JLabel("Phase:"), helper.nextRow().get());

        ActionListener changePhaseActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Phase phase = Phase.getPhase(((LeftRadioButton) e.getSource()).getMnemonic());
                System.out.println("Selected phase " + phase.getNumber() + " " + phase.name() + ", for channel " + String.valueOf(getSelectedChannel()));
                setPhaseForCurrentChannel(phase);
            }
        };

        initPhases();
        radioPhases = new ArrayList<>();
        ButtonGroup groupPhases = new ButtonGroup();
        for (int i = 0; i <= 5; i++) {
            LeftRadioButton phaseRadio = new LeftRadioButton(String.valueOf(i) + ":");
            radioPhases.add(phaseRadio);
            groupPhases.add(phaseRadio);
            phaseRadio.setMnemonic(i);
            phaseRadio.addActionListener(changePhaseActionListener);
            add(phaseRadio, helper.rightColumn().get());
            if (selectedPhases.get(0) == i) {
                phaseRadio.setSelected(true);
            }
        }


        //helper.nextRow();


        Action savePhasesAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                WorkstationConfig.setProperty(ConfProp.PHASES1, textFieldsPhases.get(DataRamWord.FIRST).getText());
                WorkstationConfig.setProperty(ConfProp.PHASES2, textFieldsPhases.get(DataRamWord.SECOND).getText());
            }
        };

        savePhases = new JButton(savePhasesAction);
        savePhases.setText("Save Phase");
        savePhases.setMargin(new Insets(0, 0, 0, 0));
        savePhases.setMaximumSize(new Dimension(30, 10));
        savePhases.setAction(savePhasesAction);

        add(savePhases, helper.setGridWidth(2).nextRow().get());


        textFieldsPhases = new HashMap<>();
        for (int i = 0; i <= 1; i++) {
            JTextField textPhaseConst = new JTextField(WorkstationConfig.getProperty(i == 0 ? ConfProp.PHASES1 : ConfProp.PHASES2));
            add(AppFrameHelper.getTextFieldLabeled(textPhaseConst, "PHASE1:", 60, 40), helper.rightColumn().rightColumn().get());
            textFieldsPhases.put(DataRamWord.getDataRamWord(i), textPhaseConst);
        }


        nPwmTextField = new JTextField("1950");
        deathTextField = new JTextField("350");
        limitUqTextField = new JTextField("150");
        add(AppFrameHelper.getTextFieldLabeled(nPwmTextField, "N_PWM:", 60, 40), helper.setGridWidth(2).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(deathTextField, "DEATH:", 60, 40), helper.rightColumn().rightColumn().get());
        add(AppFrameHelper.getTextFieldLabeled(limitUqTextField, "LIMIT:", 60, 40), helper.rightColumn(2).get());

        uqTextField = new JTextField("500");
        udTextField = new JTextField("0");

        add(AppFrameHelper.getTextFieldLabeled(uqTextField, "Uq:", 60, 40), helper.setGridWidth(2).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(udTextField, "Ud:", 60, 40), helper.rightColumn().rightColumn().get());
        add(phaseSlider = new VerticalSlider12bit(udTextField, JSlider.HORIZONTAL, 0,200,0), helper.rightColumn().setGridWidth(5).get());
        rel_go_textfield = new JTextField("1");
        rel_to__gi_textfield = new JTextField("1");

        //coefFHVLowRate = new JTextField("400");


        add(AppFrameHelper.getTextFieldLabeled(rel_go_textfield, "REL_GO:", 100, 50), helper.setGridWidth(3).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(rel_to__gi_textfield, "REL_TO_GI:", 100, 50), helper.rightColumn(3).get());

        rel_fhv_textfield = new JTextField("5");
        rel_to_dk_textfield = new JTextField("1");

        add(AppFrameHelper.getTextFieldLabeled(rel_fhv_textfield, "REL_FHV:", 100, 50), helper.setGridWidth(3).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(rel_to_dk_textfield, "REL_TO_DK:", 100, 50), helper.rightColumn(3).get());

        rateADC = new JTextField("18");

        phaseSpi = new JTextField("0006");
        dcCoef = new JTextField("20000000");
        add(AppFrameHelper.getTextFieldLabeled(rateADC, "rate ADC:", 100, 50), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(phaseSpi, "phase SPI:", 100, 50), helper.rightColumn(2).get());
        add(AppFrameHelper.getTextFieldLabeled(dcCoef, "DC COEF:", 100, 50), helper.rightColumn(2).get());


        enableMatching = AppFrameHelper.createLeftCheckBox("EnaMatching:");
        errorCorrection = AppFrameHelper.createLeftCheckBox("ErrorCorrection:");
        direct_go = AppFrameHelper.createLeftCheckBox("direct_go:");
        is_filtering = AppFrameHelper.createLeftCheckBox("is_filter");
        is_filtering.setSelected(true);
        scl_to_mosi = AppFrameHelper.createLeftCheckBox("mosi_scl");
        clearSpeedError = AppFrameHelper.createLeftCheckBox("clearSpeedError");
        clearSpeedError.setSelected(true);
        add(enableMatching, helper.nextRow().setGridWidth(2).get());
        add(errorCorrection, helper.rightColumn(2).get());
        add(direct_go, helper.rightColumn(2).get());
        add(is_filtering, helper.rightColumn(2).get());
        add(scl_to_mosi, helper.nextRow().get());
        add(clearSpeedError, helper.rightColumn(2).get());


        porogFHVgoOrTo = new JTextField("127");
        fhvTOdelitel = new JTextField("0");
        add(AppFrameHelper.getTextFieldLabeled(porogFHVgoOrTo, "porog fhv:", 100, 50), helper.rightColumn(2).get());
        add(AppFrameHelper.getTextFieldLabeled(fhvTOdelitel, "fhv to del:", 100, 50), helper.rightColumn(2).get());

        ButtonGroup groupModeKPU = new ButtonGroup();

        enableKPUtoSHIM = new LeftRadioButton("EnaKPUtoSHIM:");
        enableSTEP = new LeftRadioButton("Step Mode:");
        enableConst = new LeftRadioButton("Const Mode:");


        enableConst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepTextField.setEnabled(false);
                constInitAngle.setEnabled(true);
            }
        });

        enableSTEP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepTextField.setEnabled(true);
                constInitAngle.setEnabled(false);
            }
        });

        enableKPUtoSHIM.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepTextField.setEnabled(false);
                constInitAngle.setEnabled(false);
            }
        });


        groupModeKPU.add(enableKPUtoSHIM);
        groupModeKPU.add(enableSTEP);
        groupModeKPU.add(enableConst);

        stepTextField = new JTextField("2");
        constInitAngle = new JTextField("0");

        enableCalcUqToShim = AppFrameHelper.createLeftCheckBox("Enable Calc Uq:");
        enableKPUtoSHIM.setSelected(true);
        add(enableSTEP, helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(stepTextField, "Step:", 100, 60), helper.rightColumn(2).setGridWidth(3).get());
        add(enableConst, helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(constInitAngle, "Angle:", 100, 60), helper.rightColumn(2).setGridWidth(3).get());
        add(enableKPUtoSHIM, helper.nextRow().setGridWidth(2).get());
        constInitAngle.setEnabled(false);
        add(enableCalcUqToShim, helper.nextRow().setGridWidth(2).get());


        ButtonGroup captureButtonGroup = new ButtonGroup();

        stopCapturingRadio = new LeftRadioButton("Stop:");
        stopCapturingRadio.setSelected(true);
        runCapturingRadio = new LeftRadioButton("Run");
        clearStorageRadio = new LeftRadioButton("Clear:");

        captureButtonGroup.add(stopCapturingRadio);
        captureButtonGroup.add(runCapturingRadio);
        captureButtonGroup.add(clearStorageRadio);

        captureDelayTextField = new JTextField("5");


        add(stopCapturingRadio, helper.nextRow().setGridWidth(1).get());
        add(runCapturingRadio, helper.rightColumn().get());
        add(clearStorageRadio, helper.rightColumn().get());
        add(AppFrameHelper.getTextFieldLabeled(captureDelayTextField, "over cycle:", 100, 50), helper.rightColumn().setGridWidth(2).get());


        angle19bit = createNotEditableTextField();



        //directAngle = createNotEditableTextField();
        result_calc_uq = createNotEditableTextField();


        fhvTO = createNotEditableTextField();
        prev_go = createNotEditableTextField();
        error_go = createNotEditableTextField();
        calcUq = createNotEditableTextField();
        calcUd = createNotEditableTextField();
       // speedTethaTextField = createNotEditableTextField();

        grahButtonPanel.add(getTextFieldLabeled(sinGO = new GraphTextField("SIN GO:", new Color(255, 0, 0)), " Sin GO:"));
        add(getTextFieldLabeled(sinGO = new GraphTextField("SIN GO:", new Color(255, 0, 0)), " SIN GO:"), helper.nextRow().setGridWidth(1).get());
        grahButtonPanel.add(getTextFieldLabeled(cosGO = new GraphTextField("COS GO:", new Color(64, 74, 255)), " Cos GO:"));
        add(getTextFieldLabeled(cosGO = new GraphTextField("COS GO:", new Color(64, 74, 255)), " COS GO:"), helper.rightColumn().rightColumn().setGridWidth(2).get());

        grahButtonPanel.add(getTextFieldLabeled(sinTO = new GraphTextField("SIN TO:", new Color(18, 255, 16)), "Sin TO:"));
        add(getTextFieldLabeled(sinTO = new GraphTextField("SIN TO:", new Color(18, 255, 16)), "SIN TO:"), helper.nextRow().setGridWidth(2).get());
        grahButtonPanel.add(getTextFieldLabeled(cosTO = new GraphTextField("COS TO:", new Color(255, 55, 209)), " Cos TO:"));
        add(getTextFieldLabeled(cosTO = new GraphTextField("COS TO:", new Color(255, 55, 209)), " COS TO:"), helper.rightColumn().rightColumn().setGridWidth(2).get());

        grahButtonPanel.add(getTextFieldLabeled(fhvGO = new GraphTextField("TSY:", new Color(255, 137, 0)), "TSY:"));
        add(getTextFieldLabeled(fhvGO = new GraphTextField("FHV GO:", new Color(255, 137, 0)), "FHV GO:"), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(fhvTO, "FHV TO:", 60, 40), helper.rightColumn().rightColumn().setGridWidth(2).get());

        add(AppFrameHelper.getTextFieldLabeled(prev_go, "prev_go:", 60, 40), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(error_go, "error_go:", 60, 40), helper.rightColumn().rightColumn().setGridWidth(2).get());

        add(AppFrameHelper.getTextFieldLabeled(calcUq, "correct:", 60, 40), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(calcUd, "calc Ud", 60, 40), helper.rightColumn().rightColumn().setGridWidth(2).get());


      //  add(AppFrameHelper.getTextFieldLabeled(directAngle, "DIRECT_ANGLE:", 130, 60), helper.nextRow().setGridWidth(3).get());
        add(getTextFieldLabeled(directAngle = new GraphTextField("DIRECT_GO:", new Color(255, 0, 0)), " DIRECT_GO:"),helper.nextRow().setGridWidth(3).get());
        add(getTextFieldLabeled(resultAngle = new GraphTextField("RESULT_ANGLE:", new Color(255, 93, 0)), "RESULT_ANGLE:"), helper.rightColumn(3).setGridWidth(3).get());

        add(AppFrameHelper.getTextFieldLabeled(angle19bit, "Angle 19bit:", 130, 60), helper.nextRow().setGridWidth(3).get());
        add(AppFrameHelper.getTextFieldLabeled(result_calc_uq, "res calc Uq", 130, 60), helper.rightColumn(3).setGridWidth(3).get());

       // add(AppFrameHelper.getTextFieldLabeled(speedTethaTextField, "Speed:", 130, 60), helper.nextRow().setGridWidth(3).get());
        add(getTextFieldLabeled(speedTethaTextField = new GraphTextField("Speed::", new Color(0, 72, 35)), " Speed::"), helper.nextRow().setGridWidth(3).get());

        add(AppFrameHelper.getLumpLabeled(errorSpeedIndicatorUmrk = new Indicator(), ""), helper.rightColumn(3).setGridWidth(1).get());
        add(AppFrameHelper.getLumpLabeled(errorSpeedIndicatorSS = new Indicator(), ""), helper.rightColumn(3).setGridWidth(1).get());
        HistoryTextField historyTextField = new HistoryTextField("history", 80, 80);
        add(historyTextField, helper.nextRow().setGridWidth(3).get());


        add(AppFrameHelper.createPanel("free"), helper.nextRow().setGridWidth(7).setWeights(0.5f, 0.9f).get());


        exchangeModel = Model.getExchangeModel();
        terminalModel = Model.getTerminalModel();
        terminalModel.addFace(CebExchangeMode.ENGINE_MODE, this);
        exchangeModel.addCebModeEventListener(CebExchangeMode.ENGINE_MODE, this);

    }


    private JTextField createNotEditableTextField() {
        JTextField textField = new JTextField();
        textField.setEditable(false);
        return textField;
    }


    private String[] createRowNameData(String name) {
        String[] rowData = new String[7];
        rowData[0] = name;
        return rowData;
    }

    private JPanel getTextFieldLabeled(JTextField textField, String label) {
        return AppFrameHelper.getTextFieldLabeled(textField, label, 100, 40);
    }

    @Override
    public byte[] createRequest() {
        byte[] packet = new byte[0];

        packet = PacketHelper.addDataToPacket(packet, PacketHelper.i2b(0x5000));

        Integer nPwm = Integer.parseInt(nPwmTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, nPwm);

        Integer death = Integer.parseInt(deathTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, death);


        boolean[] arg0 = {
                checkboxChanels.get(0).isSelected(),
                checkboxChanels.get(1).isSelected(),
                checkboxChanels.get(2).isSelected()
        };
        packet = PacketHelper.addDataToPacket(packet, powerKEYS.isSelected() ? PacketHelper.bool2byte(arg0) : (byte) 0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);
        boolean[] arg1 = {
                checkboxChanels.get(3).isSelected(),
                checkboxChanels.get(4).isSelected(),
                checkboxChanels.get(5).isSelected()
        };

        packet = PacketHelper.addDataToPacket(packet, powerKEYS.isSelected() ? PacketHelper.bool2byte(arg1) : (byte) 0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);


        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(textFieldsPhases.get(DataRamWord.FIRST).getText(), 16));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(textFieldsPhases.get(DataRamWord.SECOND).getText(), 16));

        packet = PacketHelper.addDataToPacket(packet, getSelectedChannel().getNumber());

        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(uqTextField.getText()));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(udTextField.getText()));

        boolean[] argMatching = {enableMatching.isSelected(), errorCorrection.isSelected(), direct_go.isSelected(), clearSpeedError.isSelected()};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argMatching));
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        byte modeKPUint;

        if (!enableKPUtoSHIM.isSelected()) {

            boolean[] modeKPU = {enableSTEP.isSelected(),
                    enableConst.isSelected()};
            modeKPUint = PacketHelper.bool2byte(modeKPU);
        } else {
            {
                modeKPUint = (byte) 0x00;
            }
        }
        packet = PacketHelper.addDataToPacket(packet, modeKPUint);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        Integer step = Integer.parseInt(stepTextField.getText());
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


        Integer porogFHVgoOrToInt = Integer.parseInt(porogFHVgoOrTo.getText());
        packet = PacketHelper.addDataToPacket(packet, porogFHVgoOrToInt);

        Integer fhvTOdelitelInt = Integer.parseInt(fhvTOdelitel.getText());
        packet = PacketHelper.addDataToPacket(packet, fhvTOdelitelInt);



        /*
        Integer init_angle = Integer.parseInt(constInitAngle.getText());
        packet = PacketHelper.addDataToPacket(packet, init_angle&0xFFFF);

        Integer high_bits = init_angle >> 16;
        packet = PacketHelper.addDataToPacket(packet, high_bits&0x00FF);
        */


        boolean[] argEnaCalcUqToShim = {enableCalcUqToShim.isSelected()};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argEnaCalcUqToShim));
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);


        Integer rateAdcInt = Integer.parseInt(rateADC.getText());
        if (is_filtering.isSelected()) {
            rateAdcInt = rateAdcInt | 0x4000;
        }
        if (scl_to_mosi.isSelected()) {
            rateAdcInt = rateAdcInt | 0x2000;
        }
        packet = PacketHelper.addDataToPacket(packet, rateAdcInt);
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_go_textfield.getText()));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_to__gi_textfield.getText()));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_to_dk_textfield.getText()));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_fhv_textfield.getText(), 16));

        Integer i_coef1 = Integer.valueOf(dcCoef.getText().substring(4, 8), 16);
        packet = PacketHelper.addDataToPacket(packet, i_coef1);

        Integer i_coef2 = Integer.valueOf(dcCoef.getText().substring(0, 4), 16);
        packet = PacketHelper.addDataToPacket(packet, i_coef2);
        int bup;
        if (enableUMRK.isSelected() & !enableSS.isSelected()) {
            bup = 0x01;
        } else if (enableSS.isSelected() & !enableUMRK.isSelected()) {
            bup = 0x02;
        } else if (enableSS.isSelected() & enableUMRK.isSelected()) {
            bup = 0x03;
        } else {
            bup = 0x00;
        }

        if (enaIntegrator.isSelected()) {
            bup = bup | 0x04;
        }
        packet = PacketHelper.addDataToPacket(packet, (byte) bup);
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        if (runCapturingRadio.isSelected()) {
            packet = PacketHelper.addDataToPacket(packet, (byte) 0x01);
        } else if (clearStorageRadio.isSelected()) {
            packet = PacketHelper.addDataToPacket(packet, (byte) 0x02);
        } else {
            packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);
        }
        packet = PacketHelper.addDataToPacket(packet, (byte) 0x00);

        Integer capt_delay_i = Integer.valueOf(captureDelayTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, capt_delay_i);

        Integer limit = Integer.valueOf(limitUqTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, limit);

        Integer phaseSpiInt = Integer.parseInt(phaseSpi.getText(), 16);
        packet = PacketHelper.addDataToPacket(packet, phaseSpiInt);

        return packet;
    }


    private void initPhases() {
        selectedPhases = new ArrayList<>();
        for (ChannelForPhases channel : ChannelForPhases.values()) {
            String strPhase = WorkstationConfig.getProperty(channel.getDataRamWord() == DataRamWord.FIRST ? ConfProp.PHASES1 : ConfProp.PHASES2);
            if (strPhase == null) {
                strPhase = "000";
            }
            Integer intPhases = Integer.parseInt(strPhase, 16);
            selectedPhases.add((intPhases & channel.getPhaseMask()) >> channel.getShiftValue());
        }
    }


    private void setPhaseForCurrentChannel(Phase phase) {
        ChannelForPhases channel = getSelectedChannel();
        DataRamWord numWordRam = channel.getDataRamWord();
        JTextField textFieldPhase = textFieldsPhases.get(numWordRam);
        Integer hexValue = Integer.parseInt(textFieldPhase.getText(), 16);
        Integer newPhase = phase.getNumber();
        hexValue = ((hexValue & ~channel.getPhaseMask()) | newPhase << channel.getShiftValue()) | 0x1000;
        textFieldPhase.setText(hexValue.toHexString(hexValue).substring(1));
        selectedPhases.set(channel.getNumber(), phase.getNumber());
    }

    private Integer getPhaseForChannel(Integer channel) {
        return null;
    }

    private ChannelForPhases getSelectedChannel() {
        for (LeftRadioButton radio : radioChannels) {
            if (radio.isSelected()) {
                return ChannelForPhases.getChannel(radio.getMnemonic());
            }
        }
        return null;
    }


    private void clearSelectPhase() {
        for (LeftRadioButton radio : radioPhases) {
            radio.setSelected(false);
        }
    }


    @Override
    public void refreshDataOnFace() {

        byte[] response = exchangeModel.getResponse();
        if (response.length < 10) {
            System.out.println("Length response from CEB are SMALL ");
            return;
        }
        byte[] resp = PacketHelper.extractCebPacket(exchangeModel.getResponse());

        sinGO.setText(getSensor(resp[2], resp[3]));
        cosGO.setText(getSensor(resp[4], resp[5]));
        sinTO.setText(getSensor(resp[6], resp[7]));
        cosTO.setText(getSensor(resp[8], resp[9]));

        fhvGO.setText(getSensor(resp[10], resp[11]));
        fhvTO.setText(getSensor(resp[12], resp[13]));
        prev_go.setText(PacketHelper.getUnsignedWord12bit(resp[14], resp[15]));
        error_go.setText(getSensor(resp[16], resp[17]));

        directAngle.setText(PacketHelper.getUnsignedWord16bit(resp[18], resp[19]));
        resultAngle.setText(PacketHelper.getUnsignedWord16bit(resp[20], resp[21]));
        calcUd.setText(PacketHelper.getUnsignedWord16bit(resp[22], resp[23]));
        //calcUd.setText(PacketHelper.getUnsignedWord12bit(resp[24], resp[25]));


        angle19bit.setText(PacketHelper.getAngle19(resp[26], resp[27],resp[28] ));
        // 
        result_calc_uq.setText(PacketHelper.getUnsignedWord16bit(resp[30], resp[31]));

        speedTethaTextField.setText(getSensor(resp[32], resp[33]));
        byte modes = resp[2];

        errorSpeedIndicatorUmrk.refresh(PacketHelper.getBitFromByte(resp[24], 0));
        errorSpeedIndicatorSS.refresh(PacketHelper.getBitFromByte(resp[24], 1));


        if (Model.flagQueue) {
            synchronized (Model.pointData) {
                try {
                    Model.pointData.wait();
                    Model.pointData.addPointPackage();
                    sinGO.addPoint(getSensorDouble(resp[2], resp[3]));
                    cosGO.addPoint(getSensorDouble(resp[4], resp[5]));
                    sinTO.addPoint(getSensorDouble(resp[6], resp[7]));
                    cosTO.addPoint(getSensorDouble(resp[8], resp[9]));
                    fhvGO.addPoint(getSensorDouble(resp[10], resp[11]));
                    resultAngle.addPoint(Double.parseDouble(PacketHelper.getUnsignedWord16bit(resp[20], resp[21])));
                    speedTethaTextField.addPoint(getSensorDouble(resp[32], resp[33]));
                    directAngle.addPoint(Double.parseDouble(PacketHelper.getUnsignedWord16bit(resp[18], resp[19])));

                    // recWord3.addPoint(getSensorDouble(resp[4], resp[5]));
                } catch (InterruptedException ex) {
                    System.err.println("own:: Interrupted: " + ex.getMessage());
                }
            }
        }


        this.repaint();
        this.revalidate();
    }

    private String getSensor(byte low_byte, byte high_byte) {
        return PacketHelper.getSensor(low_byte, high_byte);
    }

    private Double getSensorDouble(byte low_byte, byte high_byte) {
        return PacketHelper.getSensorDouble(low_byte, high_byte);
    }


}
