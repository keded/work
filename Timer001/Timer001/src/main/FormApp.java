package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.SynchronousQueue;


public class FormApp extends JFrame {

    static int interval;
    static Timer timer;


    private Font fontButton;
    public JLabel textLabel = new JLabel("Таймер на:");
    public JTextField textField = new JTextField("№ места");
    public JButton start5 = new JButton("5 замеров");
    public JButton start18 = new JButton("18 замеров");
    public JButton start20 = new JButton("20 замеров");
    public JButton yglomer = new JButton("Угломер");

    public FormApp(){

        ImageIcon icon = new ImageIcon("src/resource.images/clock.gif");
        setIconImage(icon.getImage());
        fontButton = new Font("Arial", Font.PLAIN, 20);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Timer-001");

        setSize(300,400);

        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        textLabel.setSize(100,100);
        start5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Time interval: " + start5.getText());
                Time("330");
//                Time("1");
            }
        });
        start20.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Time interval: " + start20.getText());
                Time("1320");
            }
        });
        start18.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Time interval: " + start18.getText());
                Time("1188");
            }
        });
        yglomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Time interval: " + yglomer.getText());
                Time("3840");
            }
        });

        add(textField);
        add(textLabel);
        add(start5);
        add(start18);
        add(start20);
        add(yglomer);

        setVisible(true);
        pack();




    }

    private static final int setInterval() {
        if (interval == 1)
            timer.cancel();
        return --interval;
    }

    void Time(String secs){

        Scanner sc = new Scanner(System.in);
        System.out.print("Input seconds => : ");
       // String secs = sc.nextLine();
        int delay = 1000;
        int period = 1000;
        timer = new Timer();
        interval = Integer.parseInt(secs);
        System.out.println(secs);
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                int time = setInterval();
                System.out.println("Ostalos: " + time);
                textLabel.setText(String.valueOf(time) + " сек.");
                if (time == 0)
                {
                    textLabel.setText("Таймер на:");
                    JFrame EndFrame = new JFrame("GO WORK!");
                    JLabel textLabel = new JLabel("ВРЕМЯ ВЫШЛО, ИДИ РАБОТАЙ!");


                    textLabel.setFont(new Font("Arial", Font.PLAIN, 70));
                    textLabel.setForeground(Color.RED);

                    EndFrame.add(textLabel);
                    EndFrame.setAlwaysOnTop(true);
                    EndFrame.setSize(1400,300);
                    EndFrame.setBackground(Color.GREEN);

                    EndFrame.setVisible(true);
                }
            }
        }, delay, period);
    }

}
