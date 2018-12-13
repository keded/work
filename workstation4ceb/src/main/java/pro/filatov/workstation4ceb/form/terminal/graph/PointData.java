package pro.filatov.workstation4ceb.form.terminal.graph;


import java.awt.*;
import java.util.LinkedList;

/**
 * Created by user on 17.07.2017.
 */
public class PointData {
    private LinkedList<PointPackage> pointPackages;
    private int buffer;

    private long xZero = -1;


    public long getxZero() {
        return xZero;
    }

    public void setxZero(long xZero) {
        this.xZero = xZero;
    }



    public PointData(int buf) {
        this.buffer = buf;
        this.pointPackages = new LinkedList<PointPackage>();
    }

    public double getMax(){
        double Max = getPointPackage(0).getPointValue(0);
        int n, k;
        n = pointPackages.size();
        for (int i = 0; i < n; i++){
            k = getPointPackage(i).getSize();
            for(int j = 0; j < k; j++){
                if(getPointPackage(i).getPointValue(j) > Max) {Max = getPointPackage(i).getPointValue(j);}
            }
        }
        return Max;
    }

    public double getMin(){
        double Min = getPointPackage(0).getPointValue(0);
        int n, k;
        n = pointPackages.size();
        for (int i = 0; i < n; i++){
            k = getPointPackage(i).getSize();
            for(int j = 0; j < k; j++){
                if(getPointPackage(i).getPointValue(j) < Min) {Min = getPointPackage(i).getPointValue(j);}
            }
        }
        return Min;
    }

    public void clearData(){
        while (this.getSize() != 0){
            this.pointPackages.removeLast();
        }
    }

    public void addPointPackage() {
        PointPackage pointPackage = new PointPackage();
        if (this.pointPackages.size() < buffer)
            this.pointPackages.addFirst(pointPackage);
        else {
            while (pointPackages.size() >= buffer) {
                this.pointPackages.removeLast();
            }
            this.pointPackages.addFirst(pointPackage);
        }
    }


    public int getSize(){
        return this.pointPackages.size();
    }

    public PointPackage getPointPackage(int i) {
        return this.pointPackages.get(i);
    }

    public void addPointStruct(double value, String ind, Color color){
        this.getPointPackage(0).addPointStruct(value, ind, color);
        if (xZero >= 0){
            this.getPointPackage(0).setTime(System.currentTimeMillis());
            //this.getPointPackage(0).setCycle(System.currentTimeMillis() - xZero);
        }

    }


}
