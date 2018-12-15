package pro.filatov.workstation4ceb.form.terminal.graph;

/**
 * Created by user on 12.07.2017.
 */

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import pro.filatov.workstation4ceb.form.terminal.EngineModeFace;
import pro.filatov.workstation4ceb.form.terminal.GridBagHelper;
import pro.filatov.workstation4ceb.model.Model;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class GraphPanel   implements GLEventListener{


    private JPanel settingsPlotPanel = new JPanel(new GridBagLayout());
    private PointData pointData;
    private double rangeY, textOffsetZero = 0.0d, yMax, yMin, delYRange, textOffset = 0.0d;
    private boolean flagLabelList = true;
    private boolean flagGLCanvas = false;
    private boolean flagStop = false;
    private boolean flagPressed = true;
    private boolean flagPushMatrix = true;
    private boolean flagPopMatrix = false;
    private boolean flagSaveTimeX = true;
    private boolean flagScale = false;
    private boolean flagCheck = true;
    private float timeX, timeXSave, timeStop, scaleWidth = 1.0f, scaleHeight = 1.0f, scaleOffsetWheelX = 0.0f, scaleOffsetWheelY = 0.0f;
    private int width, height, delX, delY;
    private long timeForX, timeZero, offsetDelX, offsetSize, offsetSizeZero, gmaMax = 0, pressedX, pressedY, pressedXZero, pressedYZero, rangeX, delXRange;
    private GL2 gl2_display;
    private float scaleX = 0.0f, scaleY = 0.0f, scaleOffsetY = 0.0f, scaleXZero = 0.0f, scaleYZero = 0.0f, scaleTime = 0.0f, timeOffset = 0.0f, xOffset = 0.0f, yOffset = 0.0f, scaleTimeOffset = 0.0f;
    private TextRenderer renderer;
    private LinkedList<LabelList> labelList;
    private DecimalFormat df;

    public JPanel getGraphPanel() {
        return graphPanel;
    }

    private JPanel graphPanel;
    public GraphPanel(PointData data, long rangeStart, int delXStart, double max, double min){
        rangeX = rangeStart;
        delX = delXStart;
        rangeY = max - min;
        yMax = max;
        yMin = min;
        //scaleY = 2.0f;
        this.start(this, data);
    }

    public GraphPanel(PointData data, long rangeStart, int delXStart){
        rangeX = rangeStart;
        delX = delXStart;
        rangeY = 20;
        yMax = 10;
        yMin = -10;
        //scaleY = 2.0f;
        this.start(this, data);
    }

    public GraphPanel(PointData data, long rangeStart){
        rangeX = rangeStart;
        delX = 10;
        rangeY = 20;
        yMax = 10;
        yMin = -10;
        //scaleY = 2.0f;
        this.start(this, data);
    }

    public GraphPanel(PointData data){
        rangeX = 5000;
        delX = 10;
        rangeY = 20;
        yMax = 10;
        yMin = -10;
        //scaleY = 2.0f;
        this.start(this, data);
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    public void display(GLAutoDrawable glAutoDrawable) {
        timeZero = System.currentTimeMillis() - rangeX;
        if ((long)(delXRange/scaleWidth) == 0) {flagCheck = false;}
        else {
            offsetSizeZero = timeZero % (long)(delXRange/scaleWidth);
        }
        if (offsetSizeZero < gmaMax) {
            timeStop +=(float)rangeX/(delX*1000*scaleWidth);
        }
        gmaMax = offsetSizeZero;
        if (!flagStop){
            timeForX = timeZero;
            pointData.setxZero(timeForX);
            offsetSize = offsetSizeZero;
            timeX = timeStop;
        }

        gl2_display = glAutoDrawable.getGL().getGL2();
        gl2_display.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        if (flagGLCanvas)
        {
            if (flagPushMatrix)
            {
                gl2_display.glPushMatrix();
                flagPushMatrix = false;
            }
            if (flagPopMatrix)
            {
                gl2_display.glPopMatrix();
                gl2_display.glPushMatrix();
                flagPopMatrix = false;
            }
            if (!flagPopMatrix)
            {
                scaleXZero += scaleX - scaleOffsetWheelX;
                scaleYZero += scaleY + scaleOffsetY - scaleOffsetWheelY;
                xOffset = 1.7f * ((rangeX * scaleXZero / 1.7f) % delXRange) / rangeX;
                yOffset = 1.9f * (float)(((rangeY * scaleYZero / 1.9d) % delYRange) / rangeY);
                timeOffset = -scaleTime*(((long)(rangeX * scaleXZero / 1.7f)) / delXRange );
                textOffset = delYRange * ((long) (rangeY * scaleYZero / 1.9f / delYRange));
                //scaleTimeOffset = (long)(((long)(rangeX*scaleOffsetWheelX/2))/(scaleTime*1000));
                gl2_display.glTranslated(scaleX - scaleOffsetWheelX, -scaleY - scaleOffsetY + scaleOffsetWheelY, 0);
                pressedXZero = pressedX;
                pressedYZero = pressedY;
                scaleOffsetY = 0.0f;
                scaleOffsetWheelX = 0.0f;
                scaleOffsetWheelY = 0.0f;
                if (flagScale) {
                    textOffsetZero = textOffset;
                    flagScale = false;
                }
            }
            flagGLCanvas = false;
        }


        xNet();
        yNet();
        if (pointData.getSize() != 0) printFunc();
        gl2_display.glColor3f(1.0f,1.0f,1.0f);
        gl2_display.glBegin(GL2.GL_QUADS);
        gl2_display.glVertex2f( -1.0f - scaleXZero, -0.95f + scaleYZero);
        gl2_display.glVertex2f( 1.0f - scaleXZero, -0.95f + scaleYZero);
        gl2_display.glVertex2f( 1.0f - scaleXZero,-1.0f + scaleYZero);
        gl2_display.glVertex2f( -1.0f - scaleXZero,-1.0f + scaleYZero);
        gl2_display.glEnd();
        width = glAutoDrawable.getWidth();
        height = glAutoDrawable.getHeight();

        xAxis(width, height);
        //yAxis(width, height);
        gl2_display.glColor3f(1.0f,1.0f,1.0f);
        gl2_display.glBegin(GL2.GL_QUADS);
        gl2_display.glVertex2f(-1.0f - scaleXZero, 1.0f + scaleYZero);
        gl2_display.glVertex2f( -0.9f - scaleXZero, 1.0f + scaleYZero);
        gl2_display.glVertex2f( -0.9f - scaleXZero,-1.0f + scaleYZero);
        gl2_display.glVertex2f( -1.0f - scaleXZero,-1.0f + scaleYZero);
        gl2_display.glEnd();
        //width = glAutoDrawable.getWidth();
        //height = glAutoDrawable.getHeight();
        yAxis(width, height);


/*
        gl2_display.glColor3f(0.0f,0.5f,0.5f);
        gl2_display.glBegin(GL2.GL_QUADS);
        gl2_display.glVertex2f( 0.005f, 0.005f);
        gl2_display.glVertex2f( -0.005f, 0.005f);
        gl2_display.glVertex2f( -0.005f,-0.005f);
        gl2_display.glVertex2f( 0.005f,-0.005f);
        gl2_display.glEnd();
        gl2_display.glColor3f(0.0f,0.5f,0.5f);
        gl2_display.glBegin(GL2.GL_QUADS);
        gl2_display.glVertex2f( 0.005f - scaleXZero, 0.005f + scaleYZero);
        gl2_display.glVertex2f( -0.005f - scaleXZero, 0.005f + scaleYZero);
        gl2_display.glVertex2f( -0.005f - scaleXZero,-0.005f + scaleYZero);
        gl2_display.glVertex2f( 0.005f - scaleXZero,-0.005f + scaleYZero);
        gl2_display.glEnd();*//*
        renderer.beginRendering(width,height);
        renderer.setColor(1.0f, 1.0f, 1.0f, 1);
        renderer.draw(".", width/2 - 1, height/2 - 1);
        renderer.endRendering();*/
        /*gl2_display.glColor3f(0.5f,0.5f,1.0f);
        gl2_display.glBegin(GL2.GL_QUADS);
        gl2_display.glVertex2f( (width/2) + 0.005f - scaleXZero, (height/2)/height + 0.005f + scaleYZero);
        gl2_display.glVertex2f( (width/2) - 0.005f - scaleXZero, (height/2) + 0.005f + scaleYZero);
        gl2_display.glVertex2f( (width/2) - 0.005f - scaleXZero, (height/2) - 0.005f + scaleYZero);
        gl2_display.glVertex2f( (width/2) + 0.005f - scaleXZero, (height/2) - 0.005f + scaleYZero);
        gl2_display.glEnd();*/
        printLabel(width, height);
        while (labelList.size() != 0) {
            labelList.removeLast();
        }
        synchronized (Model.pointData){
            Model.pointData.notify();
        }
        //scaleOffsetWheelX = 0.0f;
        //scaleOffsetWheelY = 0.0f;
    }

    public void reshape(GLAutoDrawable drawable, int x,  int y, int width, int height) {

    }

    private void printFunc(){
        double x1, x2, y1, y2;
        int n = pointData.getSize();
        int k, sizeLabelList;
        for (int i = 0; i < n - 1; i++) { //i - buffer; j - sum of ID
            k = pointData.getPointPackage(i).getSize();
            sizeLabelList = labelList.size();
            for (int j = 0; j < k; j++) {
                gl2_display.glBegin(GL2.GL_LINES);

                //indColor(gl2_display, pointData.getPointPackage(i).getPointInd(j));
                setColor(gl2_display, pointData.getPointPackage(i).getPointColor(j));

                for (int numLabel = 0; numLabel < sizeLabelList; numLabel++){
                    if (sizeLabelList > 0){
                        if (labelList.get(numLabel).getLabel() == pointData.getPointPackage(i).getPointInd(j)){
                            flagLabelList = false;
                        }
                    }
                }
                if (flagLabelList){
                    labelList.add(new LabelList(pointData.getPointPackage(i).getPointInd(j), pointData.getPointPackage(i).getPointColor(j)));
                }
                flagLabelList = true;
                if (j < pointData.getPointPackage(i+1).getSize()) {
                    if (pointData.getPointPackage(i).getPointInd(j) == pointData.getPointPackage(i + 1).getPointInd(j)) {
                        x1 = scaleWidth * (1.7f * ((float) (pointData.getPointPackage(i).getTime() - timeForX) / rangeX) - 0.9f);
                        y1 = scaleHeight * (1.9f * pointData.getPointPackage(i).getPointValue(j)) / rangeY;
                        //scaleOffsetZeroX = (float)x1;
                        //scaleOffsetZeroY = (float)y1;
                        //x1 = x1*scaleWidth;
                        //y1 = y1*scaleHeight;

                        x2 = scaleWidth * (1.7f * ((float) (pointData.getPointPackage(i + 1).getTime() - timeForX) / rangeX) - 0.9f);
                        y2 = scaleHeight * (1.9f * pointData.getPointPackage(i + 1).getPointValue(j)) / rangeY;
                        //x2 = x2*scaleWidth;
                        //y2 = y2*scaleHeight;

                        gl2_display.glVertex2d(x1, y1);
                        gl2_display.glVertex2d(x2, y2);
                    }
                }
                    gl2_display.glEnd();
            }
        }
    }

    private void xNet (){
        gl2_display.glBegin(GL2.GL_LINES);
        gl2_display.glColor3f(0.9f, 0.9f, 1.0f);
        for (int i = -1; i <= delX + 1; i++) {
            gl2_display.glVertex2f(1.7f*(rangeX/2 - delXRange*i - offsetSize*scaleWidth + offsetDelX)/rangeX + 0.05f - scaleXZero + xOffset, -1.0f + scaleYZero);
            gl2_display.glVertex2f(1.7f*(rangeX/2 - delXRange*i - offsetSize*scaleWidth + offsetDelX)/rangeX + 0.05f - scaleXZero + xOffset, 1.0f + scaleYZero);
        }
        gl2_display.glEnd();
    }

    private void xAxis(int x, int y){
        Float textX;
        float x1, y1;
        gl2_display.glBegin(GL2.GL_LINES);
        gl2_display.glColor3f(0.0f, 0.0f, 0.0f);
        gl2_display.glVertex2f(-1.2f - scaleXZero + xOffset, -0.95f + scaleYZero); //-0.3f(x)
        gl2_display.glVertex2f(1.3f - scaleXZero + xOffset, -0.95f + scaleYZero); // +0.3f(x)
        gl2_display.glEnd();
        for (int i = -1; i <= delX + 1; i++){
            gl2_display.glColor3f(0.0f, 0.0f, 0.0f);
            x1 = 1.7f*(-rangeX/2 + delXRange*i - offsetSize*scaleWidth + offsetDelX)/rangeX + 0.05f + xOffset;
            //x1 = 1.7f*(-rangeX/2 + offsetDelX)/rangeX + 0.05f;
            y1 = -0.95f + scaleYZero;
            gl2_display.glBegin(GL2.GL_LINES);
            gl2_display.glVertex2f(x1 - scaleXZero, y1);
            gl2_display.glVertex2f(x1 - scaleXZero, y1 + 0.02f);
            gl2_display.glEnd();
            renderer.beginRendering(x, y);
            renderer.setColor(0.0f, 0.0f, 0.0f, 1);
            textX = timeX + i * scaleTime/scaleWidth + timeOffset/scaleWidth;
            renderer.draw(df.format(textX), (int)(x*(x1+1)/2 - df.format(textX).length()*3), 5);
            renderer.endRendering();
            gl2_display.glEnd();
        }
    }
    private void yNet(){

        //sizeY = sizeY * scaleYStart;
        gl2_display.glBegin(GL2.GL_LINES);
        gl2_display.glColor3f(0.9f, 0.9f, 1.0f);
        for (int i = -1; i <= delY + 2; i++){
            gl2_display.glVertex2f(-1.0f - scaleXZero, ((-0.95f + 0.19f*i)) - yOffset + scaleYZero);
            gl2_display.glVertex2f(1.0f - scaleXZero, ((-0.95f + 0.19f*i)) - yOffset + scaleYZero);
        }
        gl2_display.glEnd();
    }

    private void yAxis(int x, int y){
        float x1, y1;
        Double textY;
        gl2_display.glBegin(GL2.GL_LINES);
        gl2_display.glColor3f(0.0f, 0.0f, 0.0f);
        gl2_display.glVertex2f(-0.9f - scaleXZero, -0.95f + scaleYZero);
        gl2_display.glVertex2f(-0.9f - scaleXZero, 0.95f + scaleYZero);
        gl2_display.glEnd();
        for (int i = -1; i <= delY + 2; i++){
            gl2_display.glColor3f(0.0f, 0.0f, 0.0f);
            gl2_display.glBegin(GL2.GL_LINES);
            x1 = -0.9f - scaleXZero;
            y1 = ((-0.95f + 0.19f*i)) - yOffset;
            gl2_display.glVertex2f(x1, y1 + scaleYZero);
            gl2_display.glVertex2f(x1 + 0.01f, y1 + scaleYZero);
            gl2_display.glEnd();
            renderer.beginRendering(x, y);
            renderer.setColor(0.0f, 0.0f, 0.0f, 1);
            textY = yMin/scaleHeight - textOffsetZero + rangeY/delY*i/scaleHeight + textOffset/scaleHeight;
            renderer.draw(df.format(textY), 5, (int)(y*(y1+1)/2 - 5));
            renderer.endRendering();
        }
    }

    private void printLabel(int x, int y){
        gl2_display.glColor3f(1.0f,1.0f,1.0f);
        gl2_display.glBegin(GL2.GL_QUADS);
        gl2_display.glVertex2f(0.8f - scaleXZero, 1.0f + scaleYZero);
        gl2_display.glVertex2f( 1.0f - scaleXZero, 1.0f + scaleYZero);
        gl2_display.glVertex2f( 1.0f - scaleXZero,-1.0f + scaleYZero);
        gl2_display.glVertex2f( 0.8f - scaleXZero,-1.0f + scaleYZero);
        gl2_display.glEnd();

        renderer.setColor(0.0f, 0.0f, 0.0f, 1);
        for (int i = 0; i < labelList.size(); i++){
            renderer.beginRendering(x, y);
            renderer.draw(labelList.get(i).getLabel(), (int)((x*0.9f) + 5), y - 30 - 40*i);
            renderer.endRendering();
            setColor(gl2_display.getGL2(), labelList.get(i).getColor());
            gl2_display.glBegin(GL2.GL_QUADS);
            gl2_display.glVertex2f( 0.8f - scaleXZero + 1.9f*5/width, 1.0f + scaleYZero - 2.0f*(40 + 40*i)/height);
            gl2_display.glVertex2f( 0.8f - scaleXZero + 1.9f*45/width, 1.0f + scaleYZero - 2.0f*(40 + 40*i)/height);
            gl2_display.glVertex2f( 0.8f - scaleXZero + 1.9f*45/width, 1.0f + scaleYZero - 2.0f*(50 + 40*i)/height);
            gl2_display.glVertex2f( 0.8f - scaleXZero + 1.9f*5/width, 1.0f + scaleYZero - 2.0f*(50 + 40*i)/height);
            gl2_display.glEnd();
        }


    }

    private void setColor(GL2 gl, Color color){
        gl.glColor3f(color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f);
    }

    private void indColor(GL2 gl, int IND){
        int mod = IND % 3;
        double x = 0, y = 0, z = 0;

        int k = IND / 3 + 1;
        switch (mod) {
            case 0:
                x = 1.0;
                if (k%2 == 0) {
                    y = cycle(k/ 2);
                    z = cycle(k/ 2 - 1);
                }
                else
                {
                    z = cycle(k / 2);
                    y = cycle(k / 2 - 1);
                }
                break;
            case 1:
                y = 1.0;
                if (k%2 == 0) {
                    x = cycle(k / 2);
                    z = cycle(k / 2 - 1);
                }
                else
                {
                    z = cycle(k / 2);
                    x = cycle(k / 2 - 1);
                }
                break;
            case 2:
                z = 1.0;
                if (k%2 == 0) {
                    x = cycle(k / 2);
                    y = cycle(k / 2 - 1);
                }
                else
                {
                    y = cycle(k / 2);
                    x = cycle(k / 2 - 1);
                }
                break;
        }
        gl.glColor3d(x, y, z);
    }

    private double cycle(double IND){
        double s = 0.0, k = 0.5;
        for (int i = 0; i < IND; i++){
            s = s + k;
            k = k / 2;
        }
        return s;
    }

    private void changeRange(){
        //timeStop = - range / 1000;
        delXRange = rangeX / delX;
        delYRange = rangeY / delY;
        scaleTime = delXRange/ 1000.0f;
        offsetDelX = timeForX % delXRange;

    }


    public void start(GraphPanel b, PointData data){



        renderer = new TextRenderer(new Font("Serif", Font.PLAIN, 14), true, true);
        df = new DecimalFormat("#.##");
        labelList = new LinkedList<LabelList>();
        delY = 10;
        timeStop = -rangeX / 1000;
        pointData = data;
        //timeStop = - range / 1000;
        //delXRange = range / delX;
        //scaleTime = delXRange/1000.0f;

        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        final GLCapabilities capabilities = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(capabilities);
        glcanvas.addGLEventListener(b);
        glcanvas.setSize(new Dimension(400,500));
        glcanvas.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                pressedXZero = e.getX();
                pressedYZero = e.getY();
                flagPressed = true;
            }

            public void mouseReleased(MouseEvent e) {
                flagPressed = false;
                flagGLCanvas = false;
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        glcanvas.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                if (flagPressed){
                    pressedX = e.getX();
                    pressedY = e.getY();
                    scaleY = 2.0f*(pressedY - pressedYZero)/(height);
                    scaleX = 2.0f*(pressedX - pressedXZero)/(width);
                    flagGLCanvas = true;
                }
            }

            public void mouseMoved(MouseEvent e) {

            }
        });
        glcanvas.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                float x0;
                //if (flagStop) {
                    int notches = e.getWheelRotation();
                    //scaleOffsetWheelX = 0.1f*e.getX()/(width);
                    //scaleOffsetWheelY = 0.1f*e.getY()/(height);
                if (flagSaveTimeX){
                    timeXSave = timeX;
                    flagSaveTimeX = false;
                }
                    x0 = 1.7f*(-rangeX/2 + offsetDelX - offsetSize)/rangeX + 0.05f;
                    if (notches < 0) {
                        if(flagCheck) {
                            //scaleOffsetWheelX = scaleOffsetZeroX*1.1f - scaleOffsetZeroX;
                            //scaleOffsetWheelY = scaleOffsetZeroY*1.1f - scaleOffsetZeroY;
                            scaleOffsetWheelX = 2.0f * ((e.getX() - (width / 2 + width * scaleXZero / 2)) * 2.0f - (e.getX() - (width / 2 + width * scaleXZero / 2))) / (width);
                            scaleOffsetWheelY = 2.0f * ((e.getY() - (height / 2 + height * scaleYZero / 2)) * 2.0f - (e.getY() - (height / 2 + height * scaleYZero / 2))) / (height);
                            scaleWidth = scaleWidth * 2.0f;
                            scaleHeight = scaleHeight * 2.0f;
                            timeX -= rangeX / 1000.0f * ((x0) / scaleWidth) / 1.7f;
                        }
                        //textOffsetZero -= rangeY*
                    } else {
                        scaleOffsetWheelX = 2.0f * ((e.getX() - (width / 2 + width * scaleXZero / 2)) / 2.0f - (e.getX() - (width / 2 + width * scaleXZero / 2))) / (width);
                        scaleOffsetWheelY = 2.0f * ((e.getY() - (height / 2 + height * scaleYZero / 2)) / 2.0f - (e.getY() - (height / 2 + height * scaleYZero / 2))) / (height);
                        timeX += rangeX/1000.0f*((x0)/(scaleWidth))/1.7f;
                        scaleWidth = scaleWidth / 2.0f;
                        scaleHeight = scaleHeight / 2.0f;
                        flagCheck = true;
                    }
                flagGLCanvas = true;
            }
        });
       graphPanel = new  JPanel();//ew JFrame ("Graph OTKFrame");

//        graphPanel.setSize(new Dimension(400,500));
        graphPanel.setMinimumSize(new Dimension(400,500));
//        graphPanel.setMaximumSize(new Dimension(1200,650));
        final JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flagStop = !flagStop;
                if (flagStop) {
                    stopButton.setText("Run");
                }
                else {stopButton.setText("Stop");}
            }
        });

        final JButton returnTranslated = new JButton("Return");
        returnTranslated.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flagGLCanvas = true;
                flagPopMatrix = true;
                scaleXZero = 0;
                scaleYZero = 0;
                //xOffset = 0;
                yOffset = 0;
                textOffset = 0;
                timeOffset = 0;
                scaleOffsetY = (float)(1.9f*(yMin + rangeY / 2)/rangeY);
                scaleOffsetWheelX = 0.0f;
                scaleOffsetWheelY = 0.0f;
                scaleWidth = 1.0f;
                scaleHeight = 1.0f;
                if (!flagSaveTimeX){
                    timeX = timeXSave;
                    flagSaveTimeX = true;
                }
            }
        });

        final JButton scale = new JButton("Scale");
        scale.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //double min, max;
                if(pointData.getSize() != 0) {
                    flagScale = true;
                    flagGLCanvas = true;
                    flagPopMatrix = true;
                    /*min = pointData.getMin();
                    max = pointData.getMax();
                    scaleOffsetY = (float)(yMin - min + ((yMax - max) - (yMin - min))/2);*/
                    //scaleOffsetY = 1.9f*scaleOffsetY/((float)rangeY);
                    scaleXZero = 0.0f;
                    scaleYZero = 0.0f;
                    //xOffset = 0;
                    yOffset = 0;
                    textOffset = 0;
                    timeOffset = 0;
                    //scaleOffsetY = (float)(1.9f*(yMin + rangeY / 2)/rangeY);
                    yMax = pointData.getMax();
                    yMin = pointData.getMin();

                    scaleOffsetY = (float)(1.9f*(yMin + rangeY / 2)/rangeY);
                    rangeY = yMax - yMin;
                    scaleOffsetWheelX = 0.0f;
                    scaleOffsetWheelY = 0.0f;
                    if (!flagSaveTimeX){
                        timeX = timeXSave;
                        flagSaveTimeX = true;
                    }
                    scaleWidth = 1.0f;
                    scaleHeight = 1.0f;
                    //scaleOffsetY = (float)(1.9f*scaleOffsetY/(rangeY));
                    delYRange = rangeY / delY;
                    //flagPopMatrix = true;

                    //delYRange = rangeY / delY;
                    //changeRange();
                    //flagGLCanvas = true;
                }
            }
        });

        final Timer updateTimer = new Timer(40, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeZero = System.currentTimeMillis() - rangeX;
                if ((long)(delXRange/scaleWidth) == 0) {flagCheck = false;}
                else {
                    offsetSizeZero = timeZero % (long)(delXRange/scaleWidth);
                }
                if (offsetSizeZero < gmaMax) {
                    timeStop +=(float)rangeX/(delX*1000*scaleWidth);
                }
                gmaMax = offsetSizeZero;
                if (!flagStop){
                    timeForX = timeZero;
                    pointData.setxZero(timeForX);
                    offsetSize = offsetSizeZero;
                    timeX = timeStop;
                }
                //glcanvas.display();
            }
        });

        final JTextField rangeXText = new JTextField(5);
        final JTextField delXText = new JTextField(5);
        //final JTextField maxYText = new JTextField(5);
        //final JTextField minYText = new JTextField(5);
        JLabel rangeXName = new JLabel("Range X:");
        JLabel delXName = new JLabel("Del X:");
        //final JLabel maxYName = new JLabel("max Y:");
        //JLabel minYName = new JLabel("min Y:");


        rangeXText.setText(String.valueOf(rangeX/1000));
        rangeXText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rangeX = Long.parseLong(rangeXText.getText()) * 1000;
                changeRange();
            }
        });

        delXText.setText(String.valueOf(delX));
        delXText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delX = Integer.parseInt(delXText.getText());
                changeRange();
            }
        });
/*
        maxYText.setText(String.valueOf(yMax));
        maxYText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flagYMax = true;
                if (flagYMax & flagYMin) {
                    yMax = Double.parseDouble(maxYText.getText());
                    yMin = Double.parseDouble(minYText.getText());
                    rangeY = yMax - yMin;
                    delYRange = rangeY / delY;
                    flagYMax = false;
                    flagYMin = false;
                }
            }
        });

        minYText.setText(String.valueOf(yMin));
        minYText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                flagYMin = true;
                if (flagYMax&flagYMin) {
                    yMax = Double.parseDouble(maxYText.getText());
                    yMin = Double.parseDouble(minYText.getText());
                    rangeY = yMax - yMin;
                    delYRange = rangeY / delY;
                    flagYMax = false;
                    flagYMin = false;
                }
            }
        });*/

        GridBagHelper helper = new GridBagHelper();

        //helper.setWeights(0.33f, 0.06f).fillBoth();
        settingsPlotPanel.setBackground(Color.white);
        settingsPlotPanel.add(rangeXName, helper.get());
        settingsPlotPanel.add(rangeXText, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(maxYName, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(maxYText, helper.rightColumn().fillBoth().get());
        settingsPlotPanel.add(stopButton, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(scale, helper.rightColumn().get());

        settingsPlotPanel.add(delXName, helper.nextRow().get());
        settingsPlotPanel.add(delXText, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(scaleYName, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(scaleYText, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(minYName, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(minYText, helper.rightColumn().fillBoth().get());
        settingsPlotPanel.add(returnTranslated, helper.rightColumn().fillBoth().get());
        settingsPlotPanel.add(scale, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(but, helper.rightColumn().fillBoth().get());

        //settingsPlotPanel.add(stopButton, helper.rightColumn().fillBoth().get());
        //settingsPlotPanel.add(rangeX, helper.rightColumn().get());
        //settingsPlotPanel.add(returnTranslated, helper.nextRow().get());

        graphPanel.setSize(1200, 750);
        graphPanel.setVisible(true);
        graphPanel.setLayout(new BorderLayout());

        EngineModeFace engineModeFace = new EngineModeFace();
//        engineModeFace.refreshDataOnFace();
        graphPanel.add(engineModeFace.getGrahButtonPanel(), BorderLayout.NORTH);
        graphPanel.add(glcanvas, BorderLayout.CENTER);
        graphPanel.add(settingsPlotPanel, BorderLayout.SOUTH);

        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       /*
        frame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {

            }

            public void windowClosing(WindowEvent e) {
                flagGLCanvas = true;
                flagPopMatrix = true;
                scaleXZero = 0;
                xOffset = 0;
                yOffset = 0;
                timeOffset = 0;
                flagStop = false;
                Model.flagQueue = false;
                glcanvas.destroy();
                frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
            }

            public void windowClosed(WindowEvent e) {

            }

            public void windowIconified(WindowEvent e) {

            }

            public void windowDeiconified(WindowEvent e) {

            }

            public void windowActivated(WindowEvent e) {

            }

            public void windowDeactivated(WindowEvent e) {

            }
        });
        */
        //frame.
        timeForX = System.currentTimeMillis() - rangeX;
        //offsetDel = timeForX % delXRange;
        changeRange();
        //offsetDel = timeForX % delXRange;
        //updateTimer.start();
        final FPSAnimator animator = new FPSAnimator(glcanvas, 300,true);
        animator.start();
    }
/*
    public static void main(String[] args) {

        PointData data = new PointData(1000);
        BasicFrame basicFrame = new BasicFrame(data, 5000, 10);
    }*/
}