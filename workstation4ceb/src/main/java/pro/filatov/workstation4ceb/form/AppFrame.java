package pro.filatov.workstation4ceb.form;


import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.editor.*;
import pro.filatov.workstation4ceb.form.terminal.Terminal;
import pro.filatov.workstation4ceb.form.tree.TreeViewPane;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.uart.rom.PacketToRomHelper;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;


/**
 * Created by yuri.filatov on 15.07.2016.
 */
public class AppFrame extends JFrame implements IModelEditorEventListener {



    private AsmEditor asmEditor;
    private MenuBar menuBar;
    private JTextArea textAreaErrors;
    JTabbedPane  tabbedPane;
    private JTextField textFieldPathHexCodes;
    private JTextField textFieldPathMifCodes;
    private StatusBar statusBar;


    private static AppFrame instance;

    public static AppFrame getInstance() {
        if(AppFrame.instance == null) {
            AppFrame.instance = new AppFrame();
        }
        return AppFrame.instance;
    }


    public AppFrame() throws HeadlessException {




        AppFrameHelper.setupLookAndFeel();

        AppFrameHelper.setupAppLocation(this);


        addWindowListener(new MainWindowListener());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Icons.mainIcon.getImage());
        setTitle(AppFrameHelper.APLICATION_NAME);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // force to do nothing
        asmEditor =  new AsmEditor(this);


        // *********************** *******************//
        MenuBar menuBar = new MenuBar(asmEditor);

        JPanel panelLeftTree = new JPanel();

        JPanel hexPathPanel = new JPanel();
        hexPathPanel.setLayout(new BoxLayout(hexPathPanel, BoxLayout.X_AXIS));
        hexPathPanel.add(new JLabel("HEX:"));

        textFieldPathHexCodes = new JTextField(Model.getEditorModel().getPathHexFiles());
        textFieldPathHexCodes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        textFieldPathHexCodes.setMinimumSize(new Dimension(200,20));
        textFieldPathHexCodes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Model.getEditorModel().setPathHexFiles(textFieldPathHexCodes.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Model.getEditorModel().setPathHexFiles(textFieldPathHexCodes.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                Model.getEditorModel().setPathHexFiles(textFieldPathHexCodes.getText());
            }
        });


        hexPathPanel.add(textFieldPathHexCodes);
        JPanel mifPathPanel = new JPanel();
        mifPathPanel.setLayout(new BoxLayout(mifPathPanel, BoxLayout.X_AXIS));
        mifPathPanel.add(new JLabel("MIF: "));
        textFieldPathMifCodes = new JTextField(Model.getEditorModel().getPathMifFiles());
        textFieldPathMifCodes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        textFieldPathMifCodes.setMinimumSize(new Dimension(200,20));
        textFieldPathMifCodes.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Model.getEditorModel().setPathMifFiles(textFieldPathMifCodes.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Model.getEditorModel().setPathMifFiles(textFieldPathMifCodes.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                Model.getEditorModel().setPathMifFiles(textFieldPathMifCodes.getText());
            }
        });

        mifPathPanel.add(textFieldPathMifCodes);
        panelLeftTree.setLayout(new BoxLayout(panelLeftTree,BoxLayout.Y_AXIS));
        menuBar.setMaximumSize(new Dimension(Integer.MAX_VALUE,20));
        menuBar.setSize(new Dimension(200,20));
        menuBar.setMinimumSize(new Dimension(200,20));
        menuBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panelLeftTree.add(menuBar);
        TreeViewPane treeViewPane =  new TreeViewPane();
        panelLeftTree.add(treeViewPane);
        panelLeftTree.add(hexPathPanel);
        panelLeftTree.add(mifPathPanel);
        panelLeftTree.setMinimumSize(new Dimension(200,0));
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.add("Asm Editor",asmEditor);
        tabbedPane.add("Terminal",  new Terminal());
//        tabbedPane.add("Memory Editor", new MemoryEditor());


        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Model.getEditorModel().setActiveTab(tabbedPane.getSelectedIndex());
            }
        });


		/* system output */
        JPanel outPanel = new JPanel();
        outPanel.setLayout(new BorderLayout());






        JSplitPane splitMain1 = new JSplitPane();
        splitMain1.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitMain1.setDividerSize(9);
       // splitMain1.setPreferredSize(new Dimension(getWidth(), 0));
        splitMain1.setDividerLocation(0.2);
        splitMain1.setOneTouchExpandable(true);

        splitMain1.setResizeWeight(0);
        splitMain1.setLeftComponent(panelLeftTree);
        splitMain1.setRightComponent(tabbedPane);


        textAreaErrors = new JTextArea();
        //textAreaErrors.setRows(5);
        JScrollPane sysLogScrollPane =  new JScrollPane(textAreaErrors, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outPanel.add(sysLogScrollPane, BorderLayout.CENTER);

        outPanel.add(statusBar = new StatusBar(),BorderLayout.SOUTH);
        sysLogScrollPane.setRowHeaderView(new TextLineNumber(textAreaErrors));
        PrintStream printStream = new PrintStream(new CustomOutputStream(textAreaErrors));
        System.setOut(printStream);
        System.setErr(printStream);






        JSplitPane splitMain0 = new JSplitPane();
        splitMain0.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitMain0.setDividerSize(9);
        splitMain0.setSize(getWidth(), getHeight());
        splitMain0.setDividerLocation(0.8);
        splitMain0.setResizeWeight(1);
        splitMain0.setOneTouchExpandable(true);
        splitMain0.setTopComponent(splitMain1);//createPanel("TopPanelMain0")
        splitMain0.setBottomComponent(outPanel);
        add(splitMain0);

        Model.getEditorModel().setModelEditorEventListener(this);

        Model.getEditorModel().openActiveTab(getCurrentMainTabForInit());
        setVisible(true);

    }


    public JTextField getTextFieldPathHexCodes() {
        return textFieldPathHexCodes;
    }

    public String getAppTitle(){
        return this.getTitle();
    }




    public void setAppTitle(String title){
        this.setTitle(title);
    }

    public AsmEditor getAsmEditor() {
        return asmEditor;
    }

    class MainWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            PacketToRomHelper.saveProperties();
            WorkstationConfig.setProperty(ConfProp.CURRENT_MAIN_TAB, Model.getEditorModel().getActiveTab().toString());
           // Model.getImitatorModel().saveProperties();
            Model.getTerminalModel().saveProperties();
            WorkstationConfig.storeProperties();
            System.exit(0);
        }
    }

    private Integer getCurrentMainTabForInit(){
        String currentTabString = WorkstationConfig.getProperty(ConfProp.CURRENT_MAIN_TAB);
        if(currentTabString  != null) {
            Integer tab = Integer.parseInt(currentTabString);
            if (tab != null) {
                return tab;
            }
        }
        return 0;

    }


    @Override
    public void cleanTextErorrs() {
        textAreaErrors.setText("");
    }


    @Override
    public void updateCurrentTab() {
        tabbedPane.setSelectedIndex(Model.getEditorModel().getActiveTab());

    }
}
