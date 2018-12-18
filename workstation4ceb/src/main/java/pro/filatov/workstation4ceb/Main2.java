package pro.filatov.workstation4ceb;



import pro.filatov.workstation4ceb.form.AppFrame;
import pro.filatov.workstation4ceb.form.MainFrame;
import pro.filatov.workstation4ceb.form.tree.OTKFrame;
import pro.filatov.workstation4ceb.model.Model;

import javax.swing.*;


/**
 * Created by Администратор on 14.07.2016.
 */
public class Main2 {


    public static void main(String []args){




        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {

                       OTKFrame frame = new OTKFrame();


                    }
                }
        );

    }


}
