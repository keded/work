package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.BoxExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.ImitatorModel;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by yuri.filatov on 02.09.2016.
 */
public class ImitatorFace extends JPanel implements IImitatorFace {


    LeftRadioButton a,b,g,f,p,t;
    JTextField angle, sinGo, cosGo, sinTo, cosTo, fhvGo, fhvTo;
    JButton sendDataButton, refreshingDataButton;
    JCheckBox useExcel;
    JTextField pathExcel;
    JTextField factor;
    ExchangeModel exchangeModel;
    ImitatorModel imitatorModel;
    public ImitatorFace(){

        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());
        helper.setWeights(0.5f, 0.005f).fillBoth();
        exchangeModel = Model.getExchangeModel();
        imitatorModel = Model.getImitatorModel();


        ButtonGroup groupLetter = new ButtonGroup();


        Action changeLetterAction= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                storeValuesToModel();
                JRadioButton sel = (JRadioButton)e.getSource();
                Model.getImitatorModel().setCurrentLetter(CurrentLetter.valueOf(sel.getName()));
                getValuesFromModel();
            }
        };


        groupLetter.add(a = new LeftRadioButton("A:"));
        a.setName("A");
        a.addActionListener(changeLetterAction);
        add(a, helper.get());
        groupLetter.add(b = new LeftRadioButton("B:"));
        b.setName("B");
        b.addActionListener(changeLetterAction);
        add(b, helper.rightColumn().get());
        groupLetter.add(g = new LeftRadioButton("G:"));
        g.setName("G");
        g.addActionListener(changeLetterAction);
        add(g, helper.rightColumn().get());
        groupLetter.add(f = new LeftRadioButton("F:"));
        f.setName("F");
        f.addActionListener(changeLetterAction);
        add(f, helper.rightColumn().get());
        groupLetter.add(p = new LeftRadioButton("P:"));
        p.setName("P");
        p.addActionListener(changeLetterAction);
        add(p, helper.rightColumn().get());
        groupLetter.add(t = new LeftRadioButton("T:"));
        t.setName("T");
        t.addActionListener(changeLetterAction);
        add(t, helper.rightColumn().get());

        a.setSelected(true);

        LetterValues letterValues = Model.getImitatorModel().getCurrentLettersValues();


        add(createTextField(angle = new JTextField(letterValues.getAngle()), "Angle:"), helper.nextRow().setGridWidth(3).get());
        add(new JLabel("(click enter)"), helper.rightColumn().rightColumn().rightColumn().get());
        add(createTextField(sinGo = new JTextField(letterValues.getSin_go()), "SIN GO:"), helper.nextRow().get());
        add(createTextField(cosGo = new JTextField(letterValues.getCos_go()), "COS GO:"), helper.nextRow().get());
        add(createTextField(sinTo = new JTextField(letterValues.getSin_to()), "SIN TO:"), helper.nextRow().get());
        add(createTextField(cosTo = new JTextField(letterValues.getCos_to()), "COS TO:"), helper.nextRow().get());
        add(createTextField(fhvGo = new JTextField(letterValues.getFhv_go()), "FHV GO:"), helper.nextRow().get());
        add(createTextField(fhvTo = new JTextField(letterValues.getFhv_to()), "FHV TO:"), helper.nextRow().get());




        angle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = ((JTextField)e.getSource()).getText();
                Integer angleInt = Integer.parseInt(text);
                Integer angleToInt = angleInt & 0xFFF;
                Integer angleGoInt = angleInt >> 7;
                Long sinToL = Math.round(Math.sin(new Double (angleToInt)*2*Math.PI / 4096)*2047);
                Long cosToL =  Math.round(Math.cos(new Double (angleToInt)*2*Math.PI / 4096)*2047);
                Long sinGoL = Math.round(Math.sin(new Double (angleGoInt)*2*Math.PI / 4096)*2047);
                Long cosGoL =  Math.round(Math.cos(new Double (angleGoInt)*2*Math.PI / 4096)*2047);
                System.out.println("Calc from angle => sin_go:"+ sinGoL.toString()+" cos_go:"+cosGoL.toString() + " sin_to:" + sinToL.toString() + " cos_to:" + cosToL.toString());

                sinGo.setText(sinGoL.toString());
                cosGo.setText(cosGoL.toString());
                sinTo.setText(sinToL.toString());
                cosTo.setText(cosToL.toString());
                storeValuesToModel();
            }
        });


        Action checkUseExcelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(useExcel.isSelected()){
                    enableManualForm(false);
                    pathExcel.setEnabled(true);
                }else{
                    enableManualForm(true);
                    pathExcel.setEnabled(false);
                }
                storeValuesToModel();
            }
        };
        useExcel = new JCheckBox("Use Excel");
        useExcel.setName("Use Excel");
        useExcel.setAction(checkUseExcelAction);
        add(useExcel, helper.nextRow().setGridWidth(1).get());

        add(new JLabel("Use Excel:"), helper.rightColumn().setGridWidth(2).get());
        String pathReadExcel = WorkstationConfig.getProperty(ConfProp.PATH_READ_EXCEL);
        pathExcel = new JTextField(pathReadExcel != null ? pathReadExcel : "");
        add(pathExcel, helper.rightColumn().rightColumn().setGridWidth(3).get());
        pathExcel.setEnabled(false);

        add(createTextField(factor = new JTextField(Model.getImitatorModel().getFactor()), "Factor:"), helper.nextRow().get());



        Action sendDataAction= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                storeValuesToModel();
                imitatorModel.setBoxExchangeMode(BoxExchangeMode.DATA_TO_BOX);
                Model.getExchangeModel().justDoIt();

            }
        };
        sendDataButton = new JButton(sendDataAction);
        sendDataButton.setText("Send Packet");
        sendDataButton.setToolTipText("Send packet in Main Mode");
        sendDataButton.setActionCommand("sendPacket");
        sendDataButton.getActionMap().put("sendDataAction", sendDataAction);
        sendDataButton.setMargin(new Insets(0,0,0,0));
        add(sendDataButton, helper.nextRow().setGridWidth(3).get());


        Action refreshingDataAction= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                storeValuesToModel();
                if(!imitatorModel.getRepeatingBoxExchange()) {
                    imitatorModel.setBoxExchangeMode(BoxExchangeMode.DATA_TO_BOX);
                    imitatorModel.setRepeatingBoxExchange(true);
                    refreshingDataButton.setText("STOP");
                    sendDataButton.setEnabled(false);
                    exchangeModel.justDoIt();
                }else{
                    imitatorModel.setBoxExchangeMode(BoxExchangeMode.NOT_WORK);
                    sendDataButton.setEnabled(true);
                    refreshingDataButton.setText("Refreshing Data");
                    imitatorModel.setRepeatingBoxExchange(false);
                }
            }
        };

        refreshingDataButton = new JButton(refreshingDataAction);
        refreshingDataButton.setText("Refreshing Data");
        refreshingDataButton.setToolTipText("Cyclic exchange information with CEB");
        refreshingDataButton.setActionCommand("refreshingData");
        refreshingDataButton.getActionMap().put("refreshingDataAction",refreshingDataAction);
        refreshingDataButton.setPreferredSize(new Dimension(60, 20));
        refreshingDataButton.setMargin(new Insets(0,0,0,0));
        add(refreshingDataButton, helper.rightColumn().rightColumn().rightColumn().setGridWidth(3).get());


        add(AppFrameHelper.createPanel("free"), helper.nextRow().setGridWidth(6).setWeights(0.5f, 0.9f).get());
        imitatorModel.setImitatorFace(this);


    }


    private JPanel createTextField(JTextField textField,String label){
        return AppFrameHelper.getTextFieldLabeled(textField, label, 60, 50);
    }

    @Override
    public void storeValuesToModel(){
        LetterValues values = Model.getImitatorModel().getCurrentLettersValues();
        values.setAngle(angle.getText());
        values.setSin_go(sinGo.getText());
        values.setCos_go(cosGo.getText());
        values.setSin_to(sinTo.getText());
        values.setCos_to(cosTo.getText());
        values.setFhv_go(fhvGo.getText());
        values.setFhv_to(fhvTo.getText());
        Model.getImitatorModel().setFactor(factor.getText());
        Model.getImitatorModel().setPathReadExcel(pathExcel.getText());
        Model.getImitatorModel().setUseExcel(useExcel.isSelected());
    }

    private void getValuesFromModel(){
        LetterValues values = Model.getImitatorModel().getCurrentLettersValues();
        angle.setText(values.getAngle());
        sinGo.setText(values.getSin_go());
        cosGo.setText(values.getCos_go());
        sinTo.setText(values.getSin_to());
        cosTo.setText(values.getCos_to());
        fhvGo.setText(values.getFhv_go());
        fhvTo.setText(values.getFhv_to());
    }

    private void enableManualForm(Boolean enable){
        a.setEnabled(enable);
        b.setEnabled(enable);
        g.setEnabled(enable);
        f.setEnabled(enable);
        p.setEnabled(enable);
        t.setEnabled(enable);
        angle.setEnabled(enable);
        sinGo.setEnabled(enable);
        cosGo.setEnabled(enable);
        sinTo.setEnabled(enable);
        cosTo.setEnabled(enable);
        fhvGo.setEnabled(enable);
        fhvTo.setEnabled(enable);

    }




}
