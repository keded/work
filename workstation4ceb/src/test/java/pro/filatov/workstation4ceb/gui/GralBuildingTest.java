package pro.filatov.workstation4ceb.gui;

import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.form.terminal.GridBagHelper;
import pro.filatov.workstation4ceb.form.terminal.graph.HistoryTextField;

import javax.swing.*;
import java.awt.*;

/**
 * Created by user on 07.02.2017.
 */
public class GralBuildingTest {


    
    

    public static void main(String []args){

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });




    }


    private static void createGUI(){
        JFrame frame = new JFrame("Test");
        frame.setLayout(new GridBagLayout());
        AppFrameHelper.setupAppLocation(frame);
        GridBagHelper helper = new GridBagHelper();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HistoryTextField textField = new HistoryTextField("TextField:", 80,80);
        frame.add(textField, helper.get());
        
        frame.pack();
        frame.setVisible(true);



    }

}
