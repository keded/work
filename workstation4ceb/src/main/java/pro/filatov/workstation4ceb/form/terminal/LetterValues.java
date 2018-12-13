package pro.filatov.workstation4ceb.form.terminal;

import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;

import javax.swing.*;

/**
 * Created by yuri.filatov on 02.09.2016.
 */
public class LetterValues {


    //CurrentLetter letter;
    String  angle, sin_go, cos_go, sin_to, cos_to, fhv_go, fhv_to;


    public LetterValues(CurrentLetter letter){
       // this.letter = letter;
        String v = WorkstationConfig.getProperty(letter.name() + "_" + "angle");
        angle = v != null ? v : "0" ;
        v = WorkstationConfig.getProperty(letter.name() + "_" + "sin_go");
        sin_go = v != null ? v : "0" ;
        v= WorkstationConfig.getProperty(letter.name() + "_" + "cos_go");
        cos_go = v != null ? v : "0" ;
        v= WorkstationConfig.getProperty(letter.name() + "_" + "sin_to");
        sin_to =  v != null ? v : "0" ;
        v=  WorkstationConfig.getProperty(letter.name() + "_" + "cos_to");
        cos_to = v != null ? v : "0" ;
        v =  WorkstationConfig.getProperty(letter.name() + "_" + "fhv_go");
        fhv_go = v != null ? v : "0" ;
        v =  WorkstationConfig.getProperty(letter.name() + "_" + "fhv_go");
        fhv_to = v != null ? v : "0" ;
    }


    public String getAngle() {
        return angle;
    }

    public String getSin_go() {
        return sin_go;
    }

    public String getCos_go() {
        return cos_go;
    }

    public String getSin_to() {
        return sin_to;
    }

    public String getCos_to() {
        return cos_to;
    }

    public String getFhv_go() {
        return fhv_go;
    }

    public String getFhv_to() {
        return fhv_to;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public void setSin_go(String sin_go) {
        this.sin_go = sin_go;
    }

    public void setCos_go(String cos_go) {
        this.cos_go = cos_go;
    }

    public void setSin_to(String sin_to) {
        this.sin_to = sin_to;
    }

    public void setCos_to(String cos_to) {
        this.cos_to = cos_to;
    }

    public void setFhv_go(String fhv_go) {
        this.fhv_go = fhv_go;
    }

    public void setFhv_to(String fhv_to) {
        this.fhv_to = fhv_to;
    }
}
