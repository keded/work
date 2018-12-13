package pro.filatov.workstation4ceb.form.terminal;


import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.AppFrame;
import pro.filatov.workstation4ceb.form.AppFrameHelper;
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
public class PreciseReductionFace extends JPanel implements IModeFace {

    LeftRadioButton enableKPUtoSHIM, enableSTEP, enableConst;
    JTextField  uqTextField, udTextField, constInitAngle, deathTextField, nPwmTextField, stepTextField ;
    JTextField oscPotLen, coefFHVsmall, coefFHVLowRate;
    JTextField rateADC, oscZeroLen, coefFHVbig, dcCoef;
    JTextField phaseSpi;
    JTextField limitUqTextField, speedTethaTextField;
    JCheckBox powerKEYS, enableMatching, errorCorrection, enableCalcUqToShim, enableUMRK, enableSS, enaIntegrator;
    JCheckBox direct_go, is_filtering, scl_to_mosi;
    JButton savePhases;
    JTextField fhvTOdelitel, porogFHVgoOrTo;
    Indicator errorSpeedIndicator;

    Map<DataRamWord, JTextField> textFieldsPhases;

    LeftRadioButton runCapturingRadio, stopCapturingRadio, clearStorageRadio;
    JTextField captureDelayTextField;
    JCheckBox clearSpeedError;

    List<Integer> selectedPhases;
    List<LeftRadioButton> radioPhases;
    List<JCheckBox> checkboxChanels;
    JTextField directAngle, resultAngle, angle19bit,  sinGO, cosGO, sinTO, cosTO, fhvGO, fhvTO, ia, ib, calcUq, calcUd;
    List<LeftRadioButton> radioChannels;
    JTextField result_calc_uq;

    private JTable table;

    ExchangeModel exchangeModel;
    TerminalModel terminalModel;

    JTextField AngleF, AngleP, AngleT;
    JCheckBox EnaReduction;


    public PreciseReductionFace() {

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
        String [] phasesNames= {"A", "B", "G", "F", "P", "T"};
        checkboxChanels = new ArrayList<>();
        //ButtonGroup groupPhases = new ButtonGroup();
        for(int i=0; i<=5; i++){
            JCheckBox channel = AppFrameHelper.createLeftCheckBox(phasesNames[i] + ":");
            checkboxChanels.add(channel);
            channel.setMnemonic(i);
            //  channel.addActionL0istener(chooseChannelsActionListener);
            add(channel, helper.rightColumn().get());
        }
        helper.nextRow();
        powerKEYS = AppFrameHelper.createLeftCheckBox("Power keys:");
        add(powerKEYS, helper.setGridWidth(2). get());
        enableUMRK = AppFrameHelper.createLeftCheckBox("Enable UMRK:");
        add(enableUMRK, helper.rightColumn(2).setGridWidth(2). get());
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


        for(int i=0; i <= 5; i++){
            LeftRadioButton radioButton = new LeftRadioButton(ChannelForPhases.getChannel(i).getShortName()+":");
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
                Phase phase = Phase.getPhase (((LeftRadioButton)e.getSource()).getMnemonic());
                System.out.println("Selected phase " + phase.getNumber() + " "+ phase.name() + ", for channel " + String.valueOf(getSelectedChannel()));
                setPhaseForCurrentChannel(phase);
            }
        };

        initPhases();
        radioPhases = new ArrayList<>();
        ButtonGroup groupPhases = new ButtonGroup();
        for(int i=0; i<=5; i++){
            LeftRadioButton phaseRadio = new LeftRadioButton(String.valueOf(i) + ":");
            radioPhases.add(phaseRadio);
            groupPhases.add(phaseRadio);
            phaseRadio.setMnemonic(i);
            phaseRadio.addActionListener(changePhaseActionListener);
            add(phaseRadio, helper.rightColumn().get());
            if(selectedPhases.get(0) == i){
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
        savePhases.setMargin(new Insets(0,0,0,0));
        savePhases.setMaximumSize(new Dimension(30, 10));
        savePhases.setAction(savePhasesAction);

        add(savePhases, helper.setGridWidth(2).nextRow().get());


        textFieldsPhases = new HashMap<>();
        for(int i=0; i<=1; i++){
            JTextField textPhaseConst = new JTextField(WorkstationConfig.getProperty(i == 0 ? ConfProp.PHASES1 : ConfProp.PHASES2 ));
            add(AppFrameHelper.getTextFieldLabeled(textPhaseConst, "PHASE1:", 60,40), helper.rightColumn().rightColumn().get());
            textFieldsPhases.put(DataRamWord.getDataRamWord(i), textPhaseConst);
        }


        nPwmTextField = new JTextField("1950");
        deathTextField = new JTextField("350");
        limitUqTextField = new JTextField("150");
        add(AppFrameHelper.getTextFieldLabeled(nPwmTextField, "N_PWM:", 60,40), helper.setGridWidth(2).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(deathTextField, "DEATH:", 60,40), helper.rightColumn().rightColumn().get());
        add(AppFrameHelper.getTextFieldLabeled(limitUqTextField, "LIMIT:", 60,40), helper.rightColumn(2).get());

        uqTextField = new JTextField("200");
        udTextField = new JTextField("0");

        add(AppFrameHelper.getTextFieldLabeled(uqTextField, "Uq:", 60,40), helper.setGridWidth(2).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(udTextField, "Ud:", 60,40), helper.rightColumn().rightColumn().get());

        oscPotLen = new JTextField("1070");
        oscZeroLen = new JTextField("30625");

        //coefFHVLowRate = new JTextField("400");


        add(AppFrameHelper.getTextFieldLabeled(oscPotLen, "TO SMALL:", 100,50), helper.setGridWidth(3).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(oscZeroLen, "TO BIG:",  100,50), helper.rightColumn(3).get());

        coefFHVsmall = new JTextField("1070");
        coefFHVbig = new JTextField("30625");

        add(AppFrameHelper.getTextFieldLabeled(coefFHVsmall, "FHV small:", 100,50), helper.setGridWidth(3).nextRow().get());
        add(AppFrameHelper.getTextFieldLabeled(coefFHVbig, "FHV big:",  100,50), helper.rightColumn(3).get());

        rateADC = new JTextField("39");

        phaseSpi = new JTextField("103");
        dcCoef = new JTextField("20000000");
        add(AppFrameHelper.getTextFieldLabeled(rateADC, "rate ADC:",  100,50), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(phaseSpi, "phase SPI:",  100,50), helper.rightColumn(2).get());
        add(AppFrameHelper.getTextFieldLabeled(dcCoef, "DC COEF:",  100,100), helper.rightColumn(2).get());



        //helper.nextRow();

        nPwmTextField.setBackground(Color.PINK);
        deathTextField.setBackground(Color.PINK);
        rateADC.setBackground(Color.PINK);
        phaseSpi.setBackground(Color.PINK);
        coefFHVbig.setBackground(Color.PINK);
        oscZeroLen.setBackground(Color.PINK);
        oscPotLen.setBackground(Color.PINK);
        coefFHVsmall.setBackground(Color.PINK);
        udTextField.setBackground(Color.PINK);
        nPwmTextField.setEnabled(false);
        deathTextField.setEnabled(false);
        rateADC.setEnabled(false);
        phaseSpi.setEnabled(false);
        coefFHVbig.setEnabled(false);
        oscZeroLen.setEnabled(false);
        oscPotLen.setEnabled(false);
        coefFHVsmall.setEnabled(false);
        udTextField.setEnabled(false);

        EnaReduction = AppFrameHelper.createLeftCheckBox("Enable Reduction");
        AngleF = new JTextField("0");
        AngleP = new JTextField("0");
        AngleT = new JTextField("0");
        EnaReduction.setBackground(Color.ORANGE);
        AngleF.setBackground(Color.ORANGE);
        AngleP.setBackground(Color.ORANGE);
        AngleT.setBackground(Color.ORANGE);
        add(EnaReduction, helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(AngleF, "AngleF:", 100, 100), helper.rightColumn(2).get());
        add(AppFrameHelper.getTextFieldLabeled(AngleP, "AngleP:", 100, 100), helper.rightColumn(2).get());
        add(AppFrameHelper.getTextFieldLabeled(AngleT, "AngleT:", 100, 100), helper.rightColumn(2).get());




        enableMatching = AppFrameHelper.createLeftCheckBox("EnaMatching:");
        errorCorrection = AppFrameHelper.createLeftCheckBox("ErrorCorrection:");
        direct_go = AppFrameHelper.createLeftCheckBox("direct_go:");
        is_filtering = AppFrameHelper.createLeftCheckBox("is_filter");
        is_filtering.setSelected(true);
        scl_to_mosi = AppFrameHelper.createLeftCheckBox("mosi_scl");
        clearSpeedError = AppFrameHelper.createLeftCheckBox("clearSpeedError");
        add(enableMatching, helper.nextRow().setGridWidth(2). get());
        add(errorCorrection, helper.rightColumn(2).get());
        add(direct_go, helper.rightColumn(2).get());
        add(is_filtering, helper.rightColumn(2).get());
        add(scl_to_mosi, helper.nextRow().get());
        add(clearSpeedError, helper.rightColumn(2).get());


        porogFHVgoOrTo = new JTextField("255");
        fhvTOdelitel = new JTextField("1024");
        add(AppFrameHelper.getTextFieldLabeled(porogFHVgoOrTo, "porog fhv:",  100,50), helper.rightColumn(2).get());
        add(AppFrameHelper.getTextFieldLabeled(fhvTOdelitel, "fhv to del:",  100,50), helper.rightColumn(2).get());

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

        enableCalcUqToShim =  AppFrameHelper.createLeftCheckBox("Enable Calc Uq:");
        enableKPUtoSHIM.setSelected(true);
        add(enableSTEP, helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(stepTextField , "Step:", 100,60), helper.rightColumn(2).setGridWidth(3).get());
        add(enableConst, helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(constInitAngle, "Angle:", 100,60), helper.rightColumn(2).setGridWidth(3).get());
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
        add(AppFrameHelper.getTextFieldLabeled(captureDelayTextField, "over cycle:",  100,50),helper.rightColumn().setGridWidth(2).get());

        sinGO = createNotEditableTextField();
        cosGO = createNotEditableTextField();
        sinTO = createNotEditableTextField();
        cosTO = createNotEditableTextField();

        angle19bit = createNotEditableTextField();


        resultAngle = createNotEditableTextField();
        directAngle = createNotEditableTextField();
        result_calc_uq = createNotEditableTextField();

        fhvGO= createNotEditableTextField();
        fhvTO= createNotEditableTextField();
        ia= createNotEditableTextField();
        ib= createNotEditableTextField();
        calcUq = createNotEditableTextField();
        calcUd = createNotEditableTextField();
        speedTethaTextField = createNotEditableTextField();

        add(AppFrameHelper.getTextFieldLabeled(sinGO, "SIN GO:", 60,40), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(cosGO, "COS GO:", 60,40), helper.rightColumn().rightColumn().setGridWidth(2).get());

        add(AppFrameHelper.getTextFieldLabeled(sinTO, "SIN TO:", 60,40), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(cosTO, "COS TO:", 60,40), helper.rightColumn().rightColumn().setGridWidth(2).get());

        add(AppFrameHelper.getTextFieldLabeled(fhvGO, "FHV GO:", 60,40), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(fhvTO, "FHV TO:", 60,40), helper.rightColumn().rightColumn().setGridWidth(2).get());

        add(AppFrameHelper.getTextFieldLabeled(ia, "Ia:", 60,40), helper.nextRow().setGridWidth(2).get());
        add(AppFrameHelper.getTextFieldLabeled(ib, "Ib:", 60,40), helper.rightColumn().rightColumn().setGridWidth(2).get());

        add(AppFrameHelper.getTextFieldLabeled(directAngle, "DIRECT_ANGLE:", 130,60), helper.nextRow().setGridWidth(3).get());
        add(AppFrameHelper.getTextFieldLabeled(resultAngle, "RESULT_ANGLE:", 130,60), helper.rightColumn(3).setGridWidth(3).get());

        add(AppFrameHelper.getTextFieldLabeled(calcUq, "calc Uq:", 130,60), helper.nextRow().setGridWidth(3).get());
        add(AppFrameHelper.getTextFieldLabeled(calcUd, "calc Ud", 130,60), helper.rightColumn(3).setGridWidth(3).get());

        add(AppFrameHelper.getTextFieldLabeled(angle19bit, "Angle 19bit:", 130,60), helper.nextRow().setGridWidth(3).get());
        add(AppFrameHelper.getTextFieldLabeled(result_calc_uq, "res calc Uq", 130,60), helper.rightColumn(3).setGridWidth(3).get());

        add(AppFrameHelper.getTextFieldLabeled(speedTethaTextField, "Speed:", 130,60), helper.nextRow().setGridWidth(3).get());


        add(AppFrameHelper.getLumpLabeled(errorSpeedIndicator= new Indicator(), ""), helper.rightColumn(3).setGridWidth(1).get());
        HistoryTextField historyTextField = new HistoryTextField("history", 80,80);
        add(historyTextField, helper.nextRow().setGridWidth(3).get());



        add(AppFrameHelper.createPanel("free"), helper.nextRow().setGridWidth(7).setWeights(0.5f, 0.9f).get());


        exchangeModel = Model.getExchangeModel();
        terminalModel = Model.getTerminalModel();
        terminalModel.addFace(CebExchangeMode.PRECISE_REDUCTION, this); // !!! LOL
        exchangeModel.addCebModeEventListener(CebExchangeMode.PRECISE_REDUCTION, this); // !!!

    }




    private JTextField createNotEditableTextField(){
        JTextField textField = new JTextField();
        textField.setEditable(false);
        return textField;
    }


    private String[] createRowNameData(String name){
        String [] rowData  = new String[7];
        rowData[0] = name;
        return rowData;
    }


    @Override
    public byte[] createRequest() {
        byte [] packet = new byte[0];

        packet = PacketHelper.addDataToPacket(packet, PacketHelper.i2b(0x5000));

        Integer nPwm   = Integer.parseInt(nPwmTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, nPwm);

        Integer death  = Integer.parseInt(deathTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, death);


        boolean [] arg0  = {
                checkboxChanels.get(0).isSelected(),
                checkboxChanels.get(1).isSelected(),
                checkboxChanels.get(2).isSelected()
        };
        packet = PacketHelper.addDataToPacket(packet, powerKEYS.isSelected() ? PacketHelper.bool2byte(arg0) : (byte)0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);
        boolean [] arg1  = {
                checkboxChanels.get(3).isSelected(),
                checkboxChanels.get(4).isSelected(),
                checkboxChanels.get(5).isSelected()
        };

        packet = PacketHelper.addDataToPacket(packet, powerKEYS.isSelected() ? PacketHelper.bool2byte(arg1) : (byte)0x00);
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);


        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(textFieldsPhases.get(DataRamWord.FIRST).getText(), 16));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(textFieldsPhases.get(DataRamWord.SECOND).getText(),16));

        packet = PacketHelper.addDataToPacket(packet,  getSelectedChannel().getNumber());

        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(uqTextField.getText()));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(udTextField.getText()));

        boolean [] argMatching = {  enableMatching.isSelected(), errorCorrection.isSelected(), direct_go.isSelected(), clearSpeedError.isSelected()};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argMatching));
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);

        byte modeKPUint;

        if(!enableKPUtoSHIM.isSelected()) {

            boolean[] modeKPU = {enableSTEP.isSelected(),
                    enableConst.isSelected()};
            modeKPUint = PacketHelper.bool2byte(modeKPU);
        }else{{
            modeKPUint = (byte)0x00;
        }}
        packet = PacketHelper.addDataToPacket(packet, modeKPUint);
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);

        Integer step = Integer.parseInt(stepTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, step);

        if(step < 0){
            byte b  = (byte)0xFF;
            packet = PacketHelper.addDataToPacket(packet, b);
            packet = PacketHelper.addDataToPacket(packet, b);
        } else {
            byte b  = (byte)0x00;
            packet = PacketHelper.addDataToPacket(packet, b);
            packet = PacketHelper.addDataToPacket(packet, b);
        }


        Integer porogFHVgoOrToInt = Integer.parseInt(porogFHVgoOrTo.getText());
        packet = PacketHelper.addDataToPacket(packet, porogFHVgoOrToInt );

        Integer fhvTOdelitelInt = Integer.parseInt(fhvTOdelitel.getText());
        packet = PacketHelper.addDataToPacket(packet, fhvTOdelitelInt);



        /*
        Integer init_angle = Integer.parseInt(constInitAngle.getText());
        packet = PacketHelper.addDataToPacket(packet, init_angle&0xFFFF);

        Integer high_bits = init_angle >> 16;
        packet = PacketHelper.addDataToPacket(packet, high_bits&0x00FF);
        */



        boolean [] argEnaCalcUqToShim = { enableCalcUqToShim.isSelected()};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argEnaCalcUqToShim));
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);


        Integer rateAdcInt = Integer.parseInt(rateADC.getText());
        if(is_filtering.isSelected()){
            rateAdcInt = rateAdcInt | 0x4000;
        }
        if(scl_to_mosi.isSelected()){
            rateAdcInt = rateAdcInt | 0x2000;
        }
        packet = PacketHelper.addDataToPacket(packet, rateAdcInt);

        boolean [] EnaReductionFlag = { EnaReduction.isSelected()};
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(EnaReductionFlag));
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(AngleF.getText()));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(AngleP.getText()));
        packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(AngleT.getText()));
        //packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_go_textfield.getText()));
        //packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_to__gi_textfield.getText()));
        //packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_fhv_textfield.getText()));
        //packet = PacketHelper.addDataToPacket(packet, Integer.parseInt(rel_to_dk_textfield.getText()));



        Integer i_coef1 = Integer.valueOf(dcCoef.getText().substring(4, 8), 16);
        packet = PacketHelper.addDataToPacket(packet, i_coef1);

        Integer i_coef2 = Integer.valueOf(dcCoef.getText().substring(0, 4), 16);
        packet = PacketHelper.addDataToPacket(packet, i_coef2);
        int bup;
        if (enableUMRK.isSelected() & !enableSS.isSelected())
        {
            bup =  0x01;
        } else if(enableSS.isSelected() & !enableUMRK.isSelected()){
            bup = 0x02;
        } else if(enableSS.isSelected() & enableUMRK.isSelected()) {
            bup = 0x03;
        } else {
            bup = 0x00;
        }

        if(enaIntegrator.isSelected()){
            bup = bup | 0x04;
        }
        packet = PacketHelper.addDataToPacket(packet, (byte)bup);
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);

        if(runCapturingRadio.isSelected()) {
            packet = PacketHelper.addDataToPacket(packet, (byte) 0x01);
        }else if(clearStorageRadio.isSelected()){
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



    private void initPhases(){
        selectedPhases = new ArrayList<>();
        for(ChannelForPhases channel : ChannelForPhases.values()){
            String strPhase = WorkstationConfig.getProperty(channel.getDataRamWord() ==  DataRamWord.FIRST ? ConfProp.PHASES1 : ConfProp.PHASES2 );
            if(strPhase  == null){
                strPhase = "000";
            }
            Integer intPhases = Integer.parseInt(strPhase, 16);
            selectedPhases.add((intPhases & channel.getPhaseMask() )>> channel.getShiftValue());
        }
    }





    private void setPhaseForCurrentChannel(Phase phase){
        ChannelForPhases channel = getSelectedChannel();
        DataRamWord numWordRam = channel.getDataRamWord();
        JTextField textFieldPhase = textFieldsPhases.get(numWordRam);
        Integer hexValue  = Integer.parseInt(textFieldPhase.getText(), 16);
        Integer newPhase = phase.getNumber();
        hexValue = ((hexValue & ~channel.getPhaseMask()) |  newPhase << channel.getShiftValue() )| 0x1000;
        textFieldPhase.setText(hexValue.toHexString(hexValue).substring(1));
        selectedPhases.set(channel.getNumber(), phase.getNumber());
    }

    private Integer getPhaseForChannel(Integer channel){
        return null;
    }

    private ChannelForPhases getSelectedChannel(){
        for(LeftRadioButton  radio : radioChannels){
            if(radio.isSelected()){
                return ChannelForPhases.getChannel(radio.getMnemonic());
            }
        }
        return null;
    }




    private void clearSelectPhase(){
        for(LeftRadioButton  radio : radioPhases){
            radio.setSelected(false);
        }
    }



    @Override
    public void refreshDataOnFace() {

        byte []response = exchangeModel.getResponse();
        if(response.length  < 10 ){
            System.out.println("Length response from CEB are SMALL ");
            return;
        }
        byte []resp  =PacketHelper.extractCebPacket(exchangeModel.getResponse());

        sinGO.setText(getSensor(resp[2], resp[3]));
        cosGO.setText(getSensor(resp[4], resp[5]));
        sinTO.setText(getSensor(resp[6], resp[7]));
        cosTO.setText(getSensor(resp[8], resp[9]));

        fhvGO.setText(getSensor(resp[10], resp[11]));
        fhvTO.setText(getSensor(resp[12], resp[13]));
        ia.setText(getSensor(resp[14], resp[15]));
        ib.setText(getSensor(resp[16], resp[17]));

        directAngle.setText(PacketHelper.getUnsignedWord16bit(resp[18], resp[19]));
        resultAngle.setText(PacketHelper.getUnsignedWord16bit(resp[20], resp[21]));
        calcUq.setText(getSensor(resp[22], resp[23]));
        calcUd.setText(getSensor(resp[24], resp[25]));



        angle19bit.setText(PacketHelper.getAngle19(resp[26], resp[27], resp[28]));

        result_calc_uq.setText(getSensor(resp[30], resp[31]));

        speedTethaTextField.setText(getSensor(resp[32], resp[33]));
        byte modes = resp[2];

        errorSpeedIndicator.refresh(PacketHelper.getBitFromByte(resp[24], 0));


        this.repaint();
        this.revalidate();
    }

    private String getSensor(byte low_byte, byte high_byte){
        return  PacketHelper.getSensor(low_byte, high_byte);
    }


}
