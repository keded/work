package pro.filatov.workstation4ceb.form.terminal.graph;


import java.awt.*;
import java.util.LinkedList;

/**
 * Created by user on 17.07.2017.
 */
public class PointPackage {
    private LinkedList<PointStruct> pointList;
    private long time;
    //private long cycle;



    public PointPackage() {
        this.pointList = new LinkedList<PointStruct>();
    }

    public void addPointStruct(double value, String ind, Color color){
        PointStruct point = new PointStruct(value, ind, color);
        this.pointList.add(point);
    }

    public double getPointValue(int index){
        return pointList.get(index).getValue();
    }

    public String getPointInd(int index){
        return pointList.get(index).getInd();
    }

    public Color getPointColor(int index){
        return pointList.get(index).getColor();
    }

    public int getSize(){
        return this.pointList.size();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
/*
    public long getCycle() {
        return cycle;
    }

    public void setCycle(long cycle) {
        this.cycle = cycle;
    }*/
}
