package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.editor.MemoryEditor;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuri.filatov on 11.08.2016.
 */
public class Terminal extends JPanel {

    private JTabbedPane  tabbedPane;
    private TerminalModel terminalModel;
    private Map<Integer, CebExchangeMode> tabeIndexMap;

    public Terminal(){

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
        tabbedPane.add(CebExchangeMode.USTP_MODE.getName(), new UstpModeFace());
        tabeIndexMap.put(0, CebExchangeMode.USTP_MODE);
        tabbedPane.add(CebExchangeMode.STEP_MODE_1.getName(),new StepMode1Face());
        tabeIndexMap.put(1, CebExchangeMode.STEP_MODE_1);
        tabbedPane.add("Step Mode 2", AppFrameHelper.createPanel("Step Mode 2"));
        tabeIndexMap.put(2, CebExchangeMode.STEP_MODE_2);
        tabbedPane.add(CebExchangeMode.SENSOR_CALIBRATION.getName(), new SensorCalibrationFace());
        tabeIndexMap.put(3, CebExchangeMode.SENSOR_CALIBRATION);
        tabbedPane.add(CebExchangeMode.MAIN_MODE.getName(), new MainModeFace());
        tabeIndexMap.put(4, CebExchangeMode.MAIN_MODE);
        tabbedPane.add(CebExchangeMode.REDUCTION.getName(), new ReductionFace());
        tabeIndexMap.put(5, CebExchangeMode.REDUCTION);
        tabbedPane.add(CebExchangeMode.ENGINE_MODE.getName(), new EngineModeFace());
        tabeIndexMap.put(6, CebExchangeMode.ENGINE_MODE);
        tabbedPane.add(CebExchangeMode.PRECISE_REDUCTION.getName(), new PreciseReductionFace());
        tabeIndexMap.put(7, CebExchangeMode.PRECISE_REDUCTION);
        tabbedPane.add(CebExchangeMode.EXAMPLE_FACE.getName(), new ExampleFace());
        tabeIndexMap.put(1, CebExchangeMode.EXAMPLE_FACE);



        tabbedPane.getModel().setSelectedIndex(getIndex(terminalModel.getCurrentExchangeMode()));


        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                terminalModel.setCurrentExchangeMode(tabeIndexMap.get(tabbedPane.getSelectedIndex()));
            }
        });


        JSplitPane splitPaneModesAndImitator = new JSplitPane();

        splitPaneModesAndImitator.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneModesAndImitator.setDividerSize(9);
        splitPaneModesAndImitator.setDividerLocation(0.6);
        splitPaneModesAndImitator.setResizeWeight(1);
        splitPaneModesAndImitator.setOneTouchExpandable(true);
        splitPaneModesAndImitator.setLeftComponent(tabbedPane);
        splitPaneModesAndImitator.setRightComponent(new MemoryEditor());
        add(splitPaneModesAndImitator);

        add(splitPaneModesAndImitator, c);



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
