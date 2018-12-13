package pro.filatov.workstation4ceb.form.terminal;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Created by yuri.filatov on 02.09.2016.
 */
public class Indicator extends JPanel{

    private BufferedImage image;
    StatusIndicator status;

    private int w = 16;
    private int h = 16;

    public Indicator() {
        setLayout(new BorderLayout());
        image = GraphicsHelper.getCircleImage(Color.GRAY);
        status = StatusIndicator.DISABLED;
    }
    public Indicator(int w, int h) {
        this.w = w;
        this.h = h;
        image = GraphicsHelper.getCircleImage(Color.GRAY, w,h);
        status = StatusIndicator.DISABLED;
    }

    public void refresh(Boolean newState){
        if(newState == null && !status.equals(StatusIndicator.DISABLED)) {
            image = GraphicsHelper.getCircleImage(Color.GRAY,w,h);
            status = StatusIndicator.DISABLED;
        }else if (newState && !status.equals(StatusIndicator.ON)){
            image = GraphicsHelper.getCircleImage(Color.GREEN,w,h);
            status = StatusIndicator.ON;
        }else if(!newState && !status.equals(StatusIndicator.OFF)){
            image = GraphicsHelper.getCircleImage(Color.RED,w,h);
            status = StatusIndicator.OFF;
        }
        this.repaint();
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters
    }


}
