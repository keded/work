package pro.filatov.workstation4ceb.form;


import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.editor.*;
import pro.filatov.workstation4ceb.form.terminal.Terminal;
import pro.filatov.workstation4ceb.form.terminal.TerminalMain;
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
public class MainFrame extends JFrame implements IModelEditorEventListener {



    private JTextArea textAreaErrors;

    private StatusBar statusBar;

    private static MainFrame instance;

    public static MainFrame getInstance() {
        if(MainFrame.instance == null) {
            MainFrame.instance = new MainFrame();
        }
        return MainFrame.instance;
    }


    public MainFrame() throws HeadlessException {




        AppFrameHelper.setupLookAndFeel();

        AppFrameHelper.setupAppLocation2(this);


        addWindowListener(new MainWindowListener());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(Icons.mainIcon.getImage());
        setTitle(AppFrameHelper.APLICATION_NAME);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // force to do nothing



        // *********************** *******************//


		/* system output */
        JPanel outPanel = new JPanel();
        outPanel.setLayout(new BorderLayout());



        textAreaErrors = new JTextArea();
        //textAreaErrors.setRows(5);
        JScrollPane sysLogScrollPane =  new JScrollPane(textAreaErrors, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outPanel.add(sysLogScrollPane, BorderLayout.CENTER);

        //outPanel.add(statusBar = new StatusBar(),BorderLayout.SOUTH);
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
        splitMain0.setTopComponent(new TerminalMain());//createPanel("TopPanelMain0")
        splitMain0.setBottomComponent(outPanel);
        add(splitMain0);
        Model.getEditorModel().setModelEditorEventListener(this);
        Model.getEditorModel().openActiveTab(getCurrentMainTabForInit());
        setVisible(true);

    }


    public String getAppTitle(){
        return this.getTitle();
    }




    public void setAppTitle(String title){
        this.setTitle(title);
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

    }
}
