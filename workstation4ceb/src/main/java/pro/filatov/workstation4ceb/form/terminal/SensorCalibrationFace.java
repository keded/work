package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.terminal.ColorColumnRenderer;
import pro.filatov.workstation4ceb.form.terminal.GridBagHelper;
import pro.filatov.workstation4ceb.form.terminal.IModeFace;
import pro.filatov.workstation4ceb.form.terminal.LeftRadioButton;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

/**
 * Created by yuri.filatov on 16.09.2016.
 */
public class SensorCalibrationFace extends JPanel implements IModeFace {


    LeftRadioButton powerA, powerB, powerG, powerF, powerP, powerT, run, stop;
    JTextField stepTextField, uqTextField, deathTextField, nPwmTextField;


    ButtonGroup powerGroup;
    private JTable table;

    ExchangeModel exchangeModel;
    TerminalModel terminalModel;




    public SensorCalibrationFace() {
        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());
        helper.setWeights(0.33f, 0.01f).fillBoth();

        stepTextField = new JTextField("10");
        add(AppFrameHelper.getTextFieldLabeled(stepTextField, "STEP:", 40, 40), helper.setGridWidth(2).get());

        uqTextField = new JTextField("300");
        add(AppFrameHelper.getTextFieldLabeled(uqTextField, "UQ:", 40, 40), helper.setGridWidth(2).rightColumn().rightColumn().get());

        powerGroup = new ButtonGroup();

        add(new JLabel("Power:"), helper.nextRow().setGridWidth(1).get());
        powerA = new LeftRadioButton("A:");
        add(powerA, helper.rightColumn().get());

        powerB = new LeftRadioButton("B:");
        add(powerB, helper.rightColumn().get());

        powerG = new LeftRadioButton("G:");
        add(powerG, helper.rightColumn().get());


        powerF = new LeftRadioButton("F:");
        add(powerF, helper.rightColumn().get());

        powerP = new LeftRadioButton("P:");
        add(powerP, helper.rightColumn().get());

        powerT = new LeftRadioButton("T:");
        add(powerT, helper.rightColumn().get());

        powerA.setSelected(true);
        powerGroup.add(powerA);
        powerGroup.add(powerB);
        powerGroup.add(powerG);
        powerGroup.add(powerF);
        powerGroup.add(powerP);
        powerGroup.add(powerT);


        ButtonGroup runGroup = new ButtonGroup();

        run = new LeftRadioButton("RUN:");
        add(run, helper.nextRow().get());


        stop = new LeftRadioButton("STOP:");
        add(stop, helper.rightColumn().get());

        stop.setSelected(true);
        runGroup.add(run);
        runGroup.add(stop);


        add(new JPanel(), helper.rightColumn().setWeight(Integer.MAX_VALUE).get());

        table = new JTable();
        table.setCellSelectionEnabled(true);
        JScrollPane scrollPaneTable = new JScrollPane(table);
        scrollPaneTable.setMinimumSize(new Dimension(500, 300));
        scrollPaneTable.setMaximumSize(new Dimension(1200, 600));
        createTable();
        ///add(scrollPaneTable, helper.nextRow().setGridWidth(6).)
        add(scrollPaneTable, helper.nextRow().setGridWidth(7).spanXRelative().setWeights(1f, 1f).get());


        exchangeModel = Model.getExchangeModel();
        terminalModel = Model.getTerminalModel();


        terminalModel.addFace(CebExchangeMode.SENSOR_CALIBRATION, this);
        exchangeModel.addCebModeEventListener(CebExchangeMode.SENSOR_CALIBRATION, this);

    }



    private void createTable(){
        String [] columnNames = {"Param", "Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        tableModel.addRow(createRowNameData("SIN TO"));
        tableModel.addRow(createRowNameData("COS TO"));
        tableModel.addRow(createRowNameData("SIN GO"));
        tableModel.addRow(createRowNameData("COS GO"));
        tableModel.addRow(createRowNameData("Current angle TO"));
        tableModel.addRow(createRowNameData("Current angle GO"));
        tableModel.addRow(createRowNameData("Increment angle"));
        tableModel.addRow(createRowNameData("Index last row table"));
        tableModel.addRow(createRowNameData("Value last row table"));
        tableModel.addRow(createRowNameData("Flag finishing calibration"));

        table.setModel(tableModel);



        table.getColumnModel().getColumn(0).setCellRenderer(new ColorColumnRenderer(new Color(226, 248, 245)));
        table.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnRenderer(new Color(139, 224, 211)));


    }

    private String[] createRowNameData(String name){
        String [] rowData  = new String[7];
        rowData[0] = name;
        return rowData;
    }


    @Override
    public byte[] createRequest() {
        byte []packet = new byte[1];

        boolean [] arg0  = {
                run.isSelected(),
                stop.isSelected()
        };
        packet[0] = PacketHelper.bool2byte(arg0);

        Integer ch = getChannel();
        packet = PacketHelper.addDataToPacket(packet, ch);

        Integer uq  = Integer.parseInt(uqTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, uq);

        Integer step = Integer.parseInt(stepTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, step);

        boolean [] argPower1  = {
                powerA.isSelected(),
                powerB.isSelected(),
                powerG.isSelected()
        };
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argPower1));
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);
        boolean [] argPower2  = {
                powerF.isSelected(),
                powerP.isSelected(),
                powerT.isSelected()
        };
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(argPower2 ));
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);

        return packet;
    }


    private Integer getChannel(){
        if(powerA.isSelected()){
            return 0;
        }else if(powerB.isSelected()){
            return 1;
        }else if(powerG.isSelected()){
            return 2;
        }else if(powerF.isSelected()){
            return 3;
        }else if(powerP.isSelected()){
            return 4;
        }else {
            return 5;
        }

    }

    @Override
    public void refreshDataOnFace() {

        byte []response = exchangeModel.getResponse();
        if(response.length == 0 | response.length < 65 ){//| response.length != 70
            //System.out.println("Length response from CEB less than 64! ");
            return;
        }

        byte []resp  =PacketHelper.extractCebPacket(response);
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        int index = 4;
        int init_row, init_col;
        Boolean isPart2 = ((int)resp [2] & 0x01 ) == 1;
        if(isPart2){
            init_row = 4;
            init_col = 4;
        }else {
            init_row = 0;
            init_col = 1;
        }

        try {
            Vector data = tableModel.getDataVector();
            for (int i = init_row; i <= data.size() - 1; i++) {
                Vector rowVector = (Vector) data.elementAt(i);
                for (int c = init_col; c <= 6; c++) {
                    String value = PacketHelper.getSensor(resp[index], resp[index + 1]);
                    rowVector.setElementAt(value, c);
                    index = index + 2;
                    if (index == 58| index > resp.length) {
                        return;
                    }
                }
                init_col = 1;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            this.revalidate();
            table.repaint();
        }


    }




}
