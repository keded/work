package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.editor.MemoryEditor;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuri.filatov on 11.08.2016.
 */
public class TerminalMain extends JPanel {

    private JTabbedPane  tabbedPane;
    private TerminalModel terminalModel;
    private Map<Integer, CebExchangeMode> tabeIndexMap;

    public TerminalMain(){

        terminalModel = Model.getTerminalModel();

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);


        GridBagConstraints c =  new GridBagConstraints();


       c.fill   = GridBagConstraints.BOTH;
       //c.gridheight = GridBagConstraints.REMAINDER;
       // c.gridwidth  = GridBagConstraints.REMAINDER;
        c.gridx = 0;
        c.gridy = 0;
        //c.insets = new Insets(40, 0, 0, 0);

        c.weightx = 1;
        c.weighty = 0.99 ;



        tabeIndexMap = new HashMap<Integer, CebExchangeMode>();

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);


        tabbedPane.add(CebExchangeMode.EXAMPLE_FACE.getName(), new ExampleFace());
        tabeIndexMap.put(1, CebExchangeMode.EXAMPLE_FACE);




        //tabbedPane.getModel().setSelectedIndex(getIndex(terminalModel.getCurrentExchangeMode()));


        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                terminalModel.setCurrentExchangeMode(tabeIndexMap.get(tabbedPane.getSelectedIndex()));
            }
        });


        add(tabbedPane, c);



        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0.01;

        add(new ModeControlPanel(), c);




    }


    private Integer getIndex(CebExchangeMode cebExchangeMode){
        for(Map.Entry<Integer, CebExchangeMode> entry  : tabeIndexMap.entrySet()){
            if(entry.getValue().equals(cebExchangeMode)){
                return entry.getKey();
            }
        }
        return null;
    }




}
