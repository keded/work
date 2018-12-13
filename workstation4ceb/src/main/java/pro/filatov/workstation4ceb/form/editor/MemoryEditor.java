package pro.filatov.workstation4ceb.form.editor;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.terminal.GridBagHelper;
import pro.filatov.workstation4ceb.form.terminal.LeftRadioButton;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.ChannelForPhases;
import pro.filatov.workstation4ceb.model.uart.rom.CurrentBlock;
import pro.filatov.workstation4ceb.model.uart.rom.PacketToRomHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * Created by yuri.filatov on 11.08.2016.
 */
public class MemoryEditor extends JPanel implements IMemoryEventListener, ILongProcessEventListener {




    private JTable table;
    private JButton readButton, writeButton, resetButton,initButton, resetCebButton, confFlashButton;
    private JButton readStorageButton;
    private JTextField addressTextField;
    private LeftRadioButton radioTrade, radioBup, radioBase, radioKpu;
    private JTextField numWordsInBind, numBinds, nameOutFile, storageAddress;
    private JTextField postfixTextField;
    private JTextField filenameTextValuesField;
    private JTextField countTextValuesField;
    private JTextField addressForTextValues;
    private JButton writeTextValuesButton;
    private JCheckBox enableConf;
    private JTextField pathForTarrirovka;
    private JButton writeAddressTableButton;
    List<LeftRadioButton> radioChannels;

    private final int WIDTH_BUTTON = 100;
    private final int HEIGHT_BUTTON = 20;
    public MemoryEditor() {
        super();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        JPanel memoryAccessPanel = new JPanel();
            memoryAccessPanel.setLayout(new BoxLayout(memoryAccessPanel, BoxLayout.Y_AXIS));

        JPanel memoryControlPanel = new JPanel();
        memoryControlPanel.setLayout(new BoxLayout(memoryControlPanel, BoxLayout.Y_AXIS));

        createTable();

        JPanel memoryTablePanel = new JPanel();
        memoryTablePanel.setLayout(new BoxLayout(memoryTablePanel, BoxLayout.X_AXIS));

        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new BoxLayout(addressPanel, BoxLayout.X_AXIS));


        addressTextField = new JTextField("20480");
        addressTextField.setAlignmentX(LEFT_ALIGNMENT);


        JScrollPane scrollPaneTable = new JScrollPane(table);
        scrollPaneTable.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
        scrollPaneTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        memoryAccessPanel.add(scrollPaneTable);


        Action performReadMemory= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Integer address =Integer.parseInt(addressTextField.getText(), 10);
                Model.getMemoryModel().setAddress(address);
                Model.getMemoryModel().readValues();
            }
        };
        readButton = new JButton(performReadMemory);
        readButton.setText("Read");
        readButton.setToolTipText("Read memory from address from this block");
        readButton.setActionCommand("readMemory");
        readButton.getActionMap().put("performReadMemory", performReadMemory);
        readButton.setMaximumSize(new Dimension(WIDTH_BUTTON,HEIGHT_BUTTON));
        readButton.setMargin(new Insets(2,2,2,2));



        Action performWriteMemory= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Integer address =Integer.parseInt(addressTextField.getText(), 10);
                Model.getMemoryModel().setValues(getTableValues());
                Model.getMemoryModel().setAddress(address);
                Model.getMemoryModel().writeValues();
            }
        };
        writeButton = new JButton(performWriteMemory);
        writeButton.setText("Write");
        writeButton.setToolTipText("Write memory to address from this block");
        writeButton.setActionCommand("writeMemory");
        writeButton.getActionMap().put("performWriteMemory", performWriteMemory);
        writeButton.setMaximumSize(new Dimension(WIDTH_BUTTON,HEIGHT_BUTTON));
        writeButton.setMargin(new Insets(2,2,2,2));

        ButtonGroup group = new ButtonGroup();
        radioTrade = new LeftRadioButton("TRADE");
        radioBup = new LeftRadioButton("BUP");
        radioBase = new LeftRadioButton("BASE");
        radioKpu = new LeftRadioButton("KPU");

        group.add(radioTrade);
        group.add(radioBup);
        group.add(radioBase);
        group.add(radioKpu);

        radioTrade.setSelected(true);
        radioBup.setSelected(false);
        radioBase.setSelected(false);
        radioKpu.setSelected(false);

        JPanel radioPanel1 = new JPanel();
        radioPanel1.setLayout(new BoxLayout(radioPanel1, BoxLayout.X_AXIS));

        JPanel radioPanel2 = new JPanel();
        radioPanel2.setLayout(new BoxLayout(radioPanel2, BoxLayout.X_AXIS));

        radioPanel1.add(radioTrade);
        radioPanel1.add(radioBup);

        radioPanel1.setAlignmentX(LEFT_ALIGNMENT);
        radioPanel2.add(radioBase);
        radioPanel2.add(radioKpu);
        radioPanel2.setAlignmentX(LEFT_ALIGNMENT);


        Action performResetCounters= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().imitRequestAO();
                PacketToRomHelper.resetCounters();
            }
        };
        resetButton = new JButton(performResetCounters);
        resetButton.setText("Init ADC");
        resetButton.setToolTipText("Reset counters after programming FPGA ");
        resetButton.setActionCommand("performResetCounters");
        resetButton.getActionMap().put("performResetCounters", performResetCounters);
        resetButton.setMaximumSize(new Dimension(WIDTH_BUTTON,HEIGHT_BUTTON));
        resetButton.setMargin(new Insets(2,2,2,2));






        Action performInitBlock= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Integer address =Integer.parseInt(addressTextField.getText(), 10);
                Model.getMemoryModel().setAddress(address);
                if(radioTrade.isSelected()){
                    Model.getMemoryModel().setCurBlock(CurrentBlock.TRADE);
                }else if(radioBup.isSelected()) {
                    Model.getMemoryModel().setCurBlock(CurrentBlock.BUP);
                }else if(radioBase.isSelected()) {
                    Model.getMemoryModel().setCurBlock(CurrentBlock.BASE);
                }else {
                    Model.getMemoryModel().setCurBlock(CurrentBlock.KPU);
                }
                Model.getMemoryModel().startInitBlock();
            }
        };
        initButton = new JButton(performInitBlock);
        initButton.setText("Init Block");
        initButton.setToolTipText("Init current block from address");
        initButton.setActionCommand("init");
        initButton.getActionMap().put("performInitBlock", performInitBlock);
        initButton.setMaximumSize(new Dimension(WIDTH_BUTTON,HEIGHT_BUTTON));
        initButton.setMargin(new Insets(2,2,2,2));


        Action performResetCeb= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().resetCeb();
            }
        };

        resetCebButton = new JButton(performResetCeb);
        resetCebButton.setText("Reset CEB");
        resetCebButton.setToolTipText("Reset CEB without inner memory");
        resetCebButton.setActionCommand("resetCeb");
        resetCebButton.getActionMap().put("performResetCeb", performResetCeb);
        resetCebButton.setMaximumSize(new Dimension(WIDTH_BUTTON,HEIGHT_BUTTON));
        resetCebButton.setMargin(new Insets(2,2,2,2));


        Action performConfFlash= new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().configurationFlashAction();
            }
        };
        confFlashButton = new JButton(performConfFlash);
        confFlashButton.setText("Config Flash");
        confFlashButton.setToolTipText("Configuration Flash to 256 Mode ");
        confFlashButton.setActionCommand("performConfFlash");
        confFlashButton.getActionMap().put("performConfFlash", performConfFlash);
        confFlashButton.setMaximumSize(new Dimension(WIDTH_BUTTON,HEIGHT_BUTTON));
        confFlashButton.setMargin(new Insets(2,2,2,2));

        enableConf =     AppFrameHelper.createLeftCheckBox("Init ADC:");



        numWordsInBind = new JTextField("1");
        numBinds = new JTextField("16382");
        nameOutFile = new JTextField("alpha_data.txt");
        postfixTextField = new JTextField("_data.txt");
        storageAddress= new JTextField("0");

        JPanel panelAddress =AppFrameHelper.getTextFieldLabeled(addressTextField, "Address:", 60,40);
        panelAddress.setAlignmentX(LEFT_ALIGNMENT);

        memoryControlPanel.add(panelAddress);
        memoryControlPanel.add(readButton);
        memoryControlPanel.add(writeButton);
        memoryControlPanel.add(radioPanel1);
        memoryControlPanel.add(radioPanel2);
        memoryControlPanel.add(initButton);
        memoryControlPanel.add(resetButton);
        memoryControlPanel.add(resetCebButton);
        memoryControlPanel.add(confFlashButton);
        memoryControlPanel.add(enableConf);


        memoryAccessPanel.add(scrollPaneTable);
        memoryControlPanel.setAlignmentY(TOP_ALIGNMENT);
        memoryAccessPanel.add(memoryControlPanel);
        add(memoryAccessPanel);



        Action performReadStorage = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().setAddress(Integer.parseInt(storageAddress.getText(), 10));
                Model.getMemoryModel().setNumBinds(Integer.parseInt(numBinds.getText()));
                Model.getMemoryModel().setWordsInBind(Integer.parseInt(numWordsInBind.getText(),10));
                Model.getMemoryModel().setNameTextFileFromStorage(nameOutFile.getText());
                Model.getMemoryModel().setPathReadWriteFiles(pathForTarrirovka.getText());
                Model.getMemoryModel().readStorage();
            }
        };

        readStorageButton = new JButton(performReadStorage);
        readStorageButton.setText("Read");
        readStorageButton.setToolTipText("Read from storage memory");
        readStorageButton.setActionCommand("performReadStorage");
        readStorageButton.getActionMap().put("performReadStorage", performReadStorage);
        readStorageButton.setMinimumSize(new Dimension(WIDTH_BUTTON,HEIGHT_BUTTON));
        readStorageButton.setMargin(new Insets(2,2,2,2));



        /************** channels ***************/
        JPanel panelChannels = new JPanel(new GridBagLayout());




        //panelChannels.setBorder(BorderFactory.createTitledBorder("Storage"));


        GridBagHelper helperChannels = new GridBagHelper();
        panelChannels.setLayout(new GridBagLayout());



        helperChannels.setWeight(1);
      //  panelChannels.setMaximumSize(new Dimension(100,50));
        radioChannels = new ArrayList<>();
        ButtonGroup groupChannels = new ButtonGroup();
        ActionListener chClickListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LeftRadioButton radio = (LeftRadioButton) e.getSource();
                ChannelForPhases ch =ChannelForPhases.getChannel(radio.getMnemonic());
                String name = String.valueOf(ch.getLongName()).toLowerCase();
                System.out.println("Selected to storage " + name + " channel");
                nameOutFile.setText(name + postfixTextField.getText());
                filenameTextValuesField.setText(name + "_table_fix2.txt");
                addressForTextValues.setText(Integer.toHexString(ch.getTableAddress()));
            }
        };
        helperChannels.setGridWidth(1);

        for(int i=0; i <= 5; i++){
            LeftRadioButton radioButton = new LeftRadioButton(ChannelForPhases.getChannel(i).getShortName()+":");
            radioButton.setMnemonic(i);
            groupChannels.add(radioButton);
            radioButton.addActionListener(chClickListener);
            if(i== 0 || i==3){
                panelChannels.add(radioButton, helperChannels.nextRow().get());
            }else {
                panelChannels.add(radioButton, helperChannels.rightColumn().get());
            }
            radioChannels.add(radioButton);
        }

        radioChannels.get(0).setSelected(true);


        add(panelChannels);
        /************** ********* ***************/


        JPanel storagePanel = new JPanel();





        storagePanel.setBorder(BorderFactory.createTitledBorder("Storage"));

        pathForTarrirovka = new JTextField("D:\\work\\repo\\simulink\\6KKP_1REZ");




        GridBagHelper helper = new GridBagHelper();
        storagePanel.setLayout(new GridBagLayout());
        storagePanel.add( panelChannels , helper.get());
        storagePanel.add(getTextFieldLabeled( numWordsInBind, "words in bind:", 120,40), helper.nextRow().get());
        storagePanel.add(getTextFieldLabeled( numBinds, "binds:", 120,40), helper.nextRow().get());
        storagePanel.add(getTextFieldLabeled(pathForTarrirovka, "path:", 120,110), helper.nextRow().get());
        storagePanel.add(getTextFieldLabeled( nameOutFile, "name file:", 120,110 ), helper.nextRow().get());
        storagePanel.add(getTextFieldLabeled( postfixTextField, "postfix:", 120,110 ), helper.nextRow().get());

        storagePanel.add(getTextFieldLabeled( storageAddress, "address:", 120,80), helper.nextRow().get());

        storagePanel.add(readStorageButton, helper.nextRow().get());




       add(storagePanel);


       // JPanel panelTextValues = new JPanel();
       // panelTextValues.setLayout(new GridBagLayout());
       // panelTextValues.setBorder(BorderFactory.createTitledBorder("Writing values from text file"));

        GridBagHelper tvHelper = new GridBagHelper();

        filenameTextValuesField = new JTextField("alpha_table_fix2.txt");
        countTextValuesField = new JTextField("4096");
        addressForTextValues = new JTextField("8000");


        Action performWriteTxtFile = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                Model.getMemoryModel().setCountTxtValues(Integer.parseInt(countTextValuesField.getText()));
                Model.getMemoryModel().setAddressForTxtValues(Integer.parseInt(addressForTextValues.getText(), 16));
                Model.getMemoryModel().setNameFileTxtValues(filenameTextValuesField.getText());
                Model.getMemoryModel().setPathReadWriteFiles(pathForTarrirovka.getText());
                Model.getMemoryModel().writeValuesFromTxtFile();

            }
        };



        writeTextValuesButton = new JButton(performWriteTxtFile );
        writeTextValuesButton.setText("Write TXT");
        writeTextValuesButton.setToolTipText("Writing to memory values from file");
        writeTextValuesButton.setActionCommand("performWriteTxtFile");
        writeTextValuesButton.getActionMap().put("performWriteTxtFile", performWriteTxtFile);
        writeTextValuesButton.setMargin(new Insets(2,2,2,2));




        storagePanel.add(getTextFieldLabeled( filenameTextValuesField, "filename:",120,110 ), helper.nextRow().get());
        storagePanel.add(getTextFieldLabeled( countTextValuesField, "num words:", 120,110 ), helper.nextRow().get());
        storagePanel.add(getTextFieldLabeled( addressForTextValues, "address:", 120,110 ), helper.nextRow().get());
        storagePanel.add(writeTextValuesButton, helper.nextRow().get());




        Action performWriteAddressTable = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                List<Integer> list =new ArrayList<>();
                Integer address = Integer.parseInt(addressForTextValues.getText(), 16);
                address = address >> 5;
                address = 0x3803 | address;
                list.add(address);
                Model.getMemoryModel().setValues(list);

                Model.getMemoryModel().setAddress(20793);
                Model.getMemoryModel().writeValues();
            }
        };

        writeAddressTableButton = new JButton(performWriteAddressTable );
        writeAddressTableButton.setText("Write Address");
        writeAddressTableButton.setToolTipText("");
        writeAddressTableButton.setActionCommand("performWriteAddressTable");
        writeAddressTableButton.getActionMap().put("performWriteAddressTable", performWriteAddressTable);
        writeAddressTableButton.setMargin(new Insets(2,2,2,2));

        storagePanel.add(writeAddressTableButton, helper.nextRow().get());

      // add(panelTextValues);

        Model.getMemoryModel().setMemoryEventListener(this);
        Model.getUartModel().addLongProcessEventListener(this);
    }


    public JPanel getTextFieldLabeled(JTextField textField,String label, int sizeLabel, int sizeField){
        JPanel  panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel comment= new JLabel(label, JLabel.RIGHT);
        comment.setMaximumSize(new Dimension(sizeLabel, 20));
        panel.add(comment);
        textField.setColumns(5);
        textField.setMinimumSize(new Dimension(sizeField, 20));
        panel.add(textField);
        return panel;
    }


    private List<Integer> getTableValues(){
        List<Integer> res = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        Vector data = model.getDataVector();
        for (int i = 0; i <= 15; i++) {
            Vector<String> row = (Vector<String>)  data.get(i);
            Integer word = row.get(1) != null ? Integer.parseInt(row.get(1), 16) : 0;
            res.add(word);
        }
        return res;
    }


    public void createTable(){
        String [] columnNames = {"Address", "Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames,16);
        table = new JTable(tableModel);
        //table.setMinimumSize(new Dimension(200,200));
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(1).setMaxWidth(60);
    }

    @Override
    public void updateValues() {
        List <Integer> memoryValues = Model.getMemoryModel().getValues();
        Integer address = Model.getMemoryModel().getAddress();
        String [] columnNames = {"address", "value"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        Integer i = 0;
        for(Integer value : memoryValues){
            String[]row = new String [2];
            Integer a = (i + address | 0x100000);
            String aString =Integer.toHexString(a).substring(1);
            row[0] =aString;
            Integer v = (value | 0x10000);
            String vString = Integer.toHexString(v).substring(1);
            row[1] = vString;
            tableModel.insertRow(i, row);
           i++;
        }
        table.setModel(tableModel);
        this.revalidate();
        table.repaint();

    }

    @Override
    public void updateStatusOfParsing() {

    }

    @Override
    public void updateStatusOfProgramming() {
        if(Model.getUartModel().isProgramming()){
            readButton.setEnabled(false);
            writeButton.setEnabled(false);
            initButton.setEnabled(false);
            resetCebButton.setEnabled(false);
            confFlashButton.setEnabled(false);

        }else {
            readButton.setEnabled(true);
            writeButton.setEnabled(true);
            initButton.setEnabled(true);
            resetCebButton.setEnabled(true);
            confFlashButton.setEnabled(true);
        }
    }

    public Integer getAddressForInit(){
       return Integer.parseInt(addressTextField.getText(), 10);


    }




}
