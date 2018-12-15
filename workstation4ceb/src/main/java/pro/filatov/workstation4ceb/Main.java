package pro.filatov.workstation4ceb;

import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.view.AppViewFrame;

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
                       AppViewFrame MainFrame = new AppViewFrame();

                        //new AppFrame();
                    }
                }
        );

    }


}
