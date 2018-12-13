package pro.filatov.workstation4ceb.form;

import pro.filatov.workstation4ceb.form.terminal.Indicator;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yuri.filatov on 22.07.2016.
 */
public class AppFrameHelper {

    public static final String APLICATION_NAME = "CEB ASM IDE";


    public  static void  setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            try {
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (Exception ex) {
                System.err.println("Nimbus look and feel not found.\n"
                        + "Default look and feel not found.");
            }
        }
    }


    public static JPanel getLumpLabeled(Indicator lump, String label){
        JPanel  panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel comment= new JLabel(label);
        panel.add(lump);
        panel.add(comment);
        return panel;
    }


    public static JPanel getPanelIndicator(Indicator indicator, int radius){
        JPanel indPanel = new JPanel();
        indPanel.setLayout(new BorderLayout());
        indPanel.add(indicator, BorderLayout.CENTER);
        indPanel.setPreferredSize(new Dimension(radius,radius));
        return indPanel;
    }

    /**
     * Setup the application location on the screen if there is more than one monitor
     * and they work in union mode
     * @param frame the frame of current application
     */
    public static void setupAppLocation(JFrame frame) {
        int width = 0;
        int height = 0;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] monitors = ge.getScreenDevices();
        GraphicsConfiguration gc = null;
        Rectangle screenRes = null;
        // select 1st(default monitor)
        if(monitors.length > 0) {
            gc = monitors[0].getDefaultConfiguration();
            screenRes = gc.getBounds();
        } else {
            throw new RuntimeException("No monitor found!");
        }
        //height of the task bar
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;

        width = screenRes.width;
        height = screenRes.height - taskBarSize;

        frame.setSize(new Double(width / 1.3).intValue(),height);

        frame.setLocation((width - frame.getWidth()) / 2, 0);
    }

    public static void setupAppLocation3(JFrame frame) {
        int width = 0;
        int height = 0;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] monitors = ge.getScreenDevices();
        GraphicsConfiguration gc = null;
        Rectangle screenRes = null;
        // select 1st(default monitor)
        if(monitors.length > 0) {
            gc = monitors[0].getDefaultConfiguration();
            screenRes = gc.getBounds();
        } else {
            throw new RuntimeException("No monitor found!");
        }
        //height of the task bar
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;

        width = screenRes.width;
        height = screenRes.height - taskBarSize;

        frame.setSize(new Double(width / 2).intValue(),height/2);

        frame.setLocation((width - frame.getWidth()) / 3, height/4);
    }

    public static void setupAppLocation2(JFrame frame) {
        int width = 0;
        int height = 0;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] monitors = ge.getScreenDevices();
        GraphicsConfiguration gc = null;
        Rectangle screenRes = null;
        // select 1st(default monitor)
        if(monitors.length > 0) {
            gc = monitors[0].getDefaultConfiguration();
            screenRes = gc.getBounds();
        } else {
            throw new RuntimeException("No monitor found!");
        }
        //height of the task bar
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;

        width = screenRes.width;
        height = screenRes.height - taskBarSize;

        frame.setSize(new Double(width / 4).intValue(),height);

        frame.setLocation(width / 2 - width/8 , 0);
    }


    public static JPanel createPanel(String text){

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(text));
        panel.setBorder(BorderFactory.createTitledBorder(text));
        return panel;
    }

    public static JPanel createPanelWithoutLabel(String text){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(text));
        return panel;
    }



    public static JCheckBox createLeftCheckBox(String text){
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setHorizontalTextPosition(JCheckBox.LEFT);
        return checkBox;
    }

    public static JCheckBox createRightCheckBox(String text){
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setHorizontalTextPosition(JCheckBox.RIGHT);
        return checkBox;
    }


    public static JPanel getTextFieldLabeled(JTextField textField,String label, int sizeLabel, int sizeField){
        JPanel  panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel comment= new JLabel(label, JLabel.RIGHT);
        comment.setMaximumSize(new Dimension(sizeLabel, 20));
        panel.add(comment);
        textField.setColumns(5);
        textField.setMaximumSize(new Dimension(sizeField, 20));
        panel.add(textField);
        return panel;
    }


}
