package pro.filatov.workstation4ceb;


import pro.filatov.workstation4ceb.form.AppFrame;
import pro.filatov.workstation4ceb.model.Model;

import javax.swing.*;


/**
 * Created by Администратор on 14.07.2016.
 */
public class Main {


    public static void main(String []args){




        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        Model.init();
                        AppFrame.getInstance();

                        //new AppFrame();
                    }
                }
        );

    }


}
