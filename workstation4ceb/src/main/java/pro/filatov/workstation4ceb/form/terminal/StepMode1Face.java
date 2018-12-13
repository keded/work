package pro.filatov.workstation4ceb.form.terminal;

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
import java.util.Vector;

/**
 * Created by yuri.filatov on 16.09.2016.
 */
public class StepMode1Face extends JPanel implements IModeFace {

    JCheckBox powerA, powerB, powerG, powerF, powerP, powerT;
    JTextField stepTextField, uqTextField, deathTextField, nPwmTextField;



    private JTable table;

    ExchangeModel exchangeModel;
    TerminalModel terminalModel;




    public StepMode1Face() {

        GridBagHelper helper = new GridBagHelper();
        setLayout(new GridBagLayout());
        helper.setWeights(0.33f, 0.01f).fillBoth();

        stepTextField = new JTextField("50");
        add(AppFrameHelper.getTextFieldLabeled(stepTextField, "STEP:", 40, 40), helper.setGridWidth(2).get());

        uqTextField = new JTextField("300");
        add(AppFrameHelper.getTextFieldLabeled(uqTextField, "UQ:", 40, 40), helper.setGridWidth(1).rightColumn().rightColumn().get());

        deathTextField = new JTextField("150");
        add(AppFrameHelper.getTextFieldLabeled(deathTextField, "DEATH:", 40, 40), helper.rightColumn().get());

        nPwmTextField = new JTextField("2200");
        add(AppFrameHelper.getTextFieldLabeled( nPwmTextField, "N_PWM:", 40, 40), helper.rightColumn().get());

        add(new JLabel("Power:"), helper.nextRow().setGridWidth(1).get());
        powerA = AppFrameHelper.createLeftCheckBox("A:");
        add(powerA, helper.rightColumn().get());

        powerB = AppFrameHelper.createLeftCheckBox("B:");
        add(powerB, helper.rightColumn().get());

        powerG = AppFrameHelper.createLeftCheckBox("G:");
        add(powerG, helper.rightColumn().get());


        powerF = AppFrameHelper.createLeftCheckBox("F:");
        add(powerF, helper.rightColumn().get());

        powerP = AppFrameHelper.createLeftCheckBox("P:");
        add(powerP, helper.rightColumn().get());

        powerT = AppFrameHelper.createLeftCheckBox("T:");
        add(powerT, helper.rightColumn().get());

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


        terminalModel.addFace(CebExchangeMode.STEP_MODE_1, this);
        exchangeModel.addCebModeEventListener(CebExchangeMode.STEP_MODE_1, this);

    }



    private void createTable(){
        String [] columnNames = {"", "ALPHA", "BETA", "GAMMA", "PHI", "PSI", "TETHA"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        tableModel.addRow(createRowNameData("SIN GO"));
        tableModel.addRow(createRowNameData("COS GO"));
        tableModel.addRow(createRowNameData("SIN TO"));
        tableModel.addRow(createRowNameData("COS TO"));
        tableModel.addRow(createRowNameData("FHV GO"));
        tableModel.addRow(createRowNameData("FHV TO"));
        tableModel.addRow(createRowNameData("Ia"));
        tableModel.addRow(createRowNameData("Ib"));
        tableModel.addRow(createRowNameData("Empty"));

        table.setModel(tableModel);



        table.getColumnModel().getColumn(0).setCellRenderer(new ColorColumnRenderer(new Color(243, 237, 248)));
        table.getColumnModel().getColumn(1).setCellRenderer(new ColorColumnRenderer(new Color(183, 224, 215)));
        table.getColumnModel().getColumn(2).setCellRenderer(new ColorColumnRenderer(new Color(244, 221, 239)));
        table.getColumnModel().getColumn(3).setCellRenderer(new ColorColumnRenderer(new Color(215, 221, 44)));
        table.getColumnModel().getColumn(4).setCellRenderer(new ColorColumnRenderer(new Color(171, 220, 158)));
        table.getColumnModel().getColumn(5).setCellRenderer(new ColorColumnRenderer(new Color(211, 185, 210)));
        table.getColumnModel().getColumn(6).setCellRenderer(new ColorColumnRenderer(new Color(167, 217, 192)));

    }

    private String[] createRowNameData(String name){
        String [] rowData  = new String[7];
        rowData[0] = name;
        return rowData;
    }


    @Override
    public byte[] createRequest() {
        byte []packet= new byte[1];

        boolean [] arg0  = {
                powerA.isSelected(),
                powerB.isSelected(),
                powerG.isSelected()
        };
        packet[0] = PacketHelper.bool2byte(arg0);
        packet = PacketHelper.addDataToPacket(packet, (byte)0x00);
        boolean [] arg1  = {
                powerF.isSelected(),
                powerP.isSelected(),
                powerT.isSelected()
        };
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.bool2byte(arg1));
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


        Integer uq  = Integer.parseInt(uqTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, uq);

        Integer death  = Integer.parseInt(deathTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, death);

        Integer nPwm   = Integer.parseInt(nPwmTextField.getText());
        packet = PacketHelper.addDataToPacket(packet, nPwm);

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
