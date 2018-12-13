import main.FormApp;

import javax.swing.*;

public class Main {


    public static void main(String []args){




        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {

                        FormApp frame = new FormApp();



                    }
                }
        );

    }


}