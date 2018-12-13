package pro.filatov.workstation4ceb.model.fpga.Terminal;

import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.terminal.CurrentLetter;
import pro.filatov.workstation4ceb.form.terminal.IImitatorFace;
import pro.filatov.workstation4ceb.form.terminal.LetterValues;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuri.filatov on 02.09.2016.
 */
public class ImitatorModel {

    Map<CurrentLetter, LetterValues> letterValuesMap = new HashMap<>();
    CurrentLetter currentLetter;
    private String pathReadExcel;
    private Boolean useExcel;
    private String factor;
    private IImitatorFace imitatorFace;

    BoxExchangeMode boxExchangeMode = BoxExchangeMode.NOT_WORK;
    Boolean isRepeatingBoxExchange = false;


    public ImitatorModel() {
        for(CurrentLetter letter : CurrentLetter.values()){
            letterValuesMap.put(letter, new LetterValues(letter));
        }
        String cur = WorkstationConfig.getProperty(ConfProp.CURRENT_LETTER);
        currentLetter =cur != null ?  CurrentLetter.valueOf(cur) : CurrentLetter.A;

    }


    public void setImitatorFace(IImitatorFace imitatorFace) {
        this.imitatorFace = imitatorFace;
    }

    public LetterValues getCurrentLettersValues(){
        return letterValuesMap.get(currentLetter);
    }

    public void saveProperties(){
        for(Map.Entry<CurrentLetter, LetterValues> entry : letterValuesMap.entrySet()){
            LetterValues values = entry.getValue();
            CurrentLetter letter = entry.getKey();
            WorkstationConfig.setProperty(letter.name() + "_" + "angle", values.getAngle());

            WorkstationConfig.setProperty(letter.name() + "_" + "sin_go" , values.getSin_go());

            WorkstationConfig.setProperty(letter.name() + "_" + "cos_go", values.getCos_go());

            WorkstationConfig.setProperty(letter.name() + "_" + "sin_to", values.getSin_to());

            WorkstationConfig.setProperty(letter.name() + "_" + "cos_to", values.getCos_to());

            WorkstationConfig.setProperty(letter.name() + "_" + "fhv_go", values.getFhv_go());

            WorkstationConfig.setProperty(letter.name() + "_" + "fhv_go", values.getFhv_to());

        }
        if(pathReadExcel != null) {
            WorkstationConfig.setProperty(ConfProp.PATH_READ_EXCEL, pathReadExcel);
        }
        WorkstationConfig.setProperty(ConfProp.BOX_DATA_FACTOR, factor);
    }

    public void setCurrentLetter(CurrentLetter currentLetter) {
        this.currentLetter = currentLetter;
    }

    public String getPathReadExcel() {
        return pathReadExcel;
    }

    public void setPathReadExcel(String pathReadExcel) {
        this.pathReadExcel = pathReadExcel;
    }

    public Boolean getUseExcel() {
        return useExcel;
    }

    public void setUseExcel(Boolean useExcel) {
        this.useExcel = useExcel;
    }

    public String getFactor() {
        if(factor == null){
            String f = WorkstationConfig.getProperty(ConfProp.BOX_DATA_FACTOR);
            f = f == null ? "5" : f;
            factor = f;
        }
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public Map<CurrentLetter, LetterValues> getLetterValuesMap() {
        return letterValuesMap;
    }





    public  byte[] getDataToBox(){
        byte []data = null;
        if(useExcel){

        }else{
            imitatorFace.storeValuesToModel();
            Map<CurrentLetter, LetterValues> values = getLetterValuesMap();
            data = getDataFromLetterValues(values.get(CurrentLetter.A));
            data = PacketHelper.addDataToPacket(data, getDataFromLetterValues(values.get(CurrentLetter.B)));
            data =PacketHelper.addDataToPacket(data, getDataFromLetterValues(values.get(CurrentLetter.G)));
            data =PacketHelper.addDataToPacket(data, getDataFromLetterValues(values.get(CurrentLetter.F)));
            data =PacketHelper.addDataToPacket(data, getDataFromLetterValues(values.get(CurrentLetter.P)));
            data =PacketHelper.addDataToPacket(data, getDataFromLetterValues(values.get(CurrentLetter.T)));
        }
        return  data;
    }

    private byte[] getDataFromLetterValues(LetterValues values){
        byte[] packet = PacketHelper.s2b(values.getSin_go());
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.s2b(values.getCos_go()));
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.s2b(values.getSin_to()));
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.s2b(values.getCos_to()));
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.s2b(values.getFhv_go()));
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.s2b(values.getFhv_to()));
        return packet;
    }


    public BoxExchangeMode getBoxExchangeMode() {
        return boxExchangeMode;
    }

    public void setBoxExchangeMode(BoxExchangeMode boxExchangeMode) {
        this.boxExchangeMode = boxExchangeMode;
    }

    public Boolean getRepeatingBoxExchange() {
        return isRepeatingBoxExchange;
    }

    public void setRepeatingBoxExchange(Boolean repeatingBoxExchange) {
        isRepeatingBoxExchange = repeatingBoxExchange;
    }
}
