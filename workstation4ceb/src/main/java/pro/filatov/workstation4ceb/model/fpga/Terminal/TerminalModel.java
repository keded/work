package pro.filatov.workstation4ceb.model.fpga.Terminal;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.terminal.IModeFace;
import pro.filatov.workstation4ceb.form.terminal.TerminalModelEventListener;
import pro.filatov.workstation4ceb.form.terminal.graph.IEnableGraphListener;
import pro.filatov.workstation4ceb.model.editor.FileHelper;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import java.io.File;
import java.util.*;

/**
 * Created by yuri.filatov on 09.09.2016.
 */
public class TerminalModel {

    private CebExchangeMode currentExchangeMode;
    private Boolean isEnableMode;
    private TerminalModelEventListener terminalModelEventListener;
    private Map<CebExchangeMode, IModeFace> modeFaceMap;
    private Boolean isRepeatingCebExchange = false;
    private Boolean runCurrentMode = false;
    private Integer countResponse  = 0;
    private Boolean isGraphing = false;

    private Map<String , DataTable>  dataTables;
    private List<String> namesGraphics;

    private IEnableGraphListener graphListener;


    public void addValueToRowGraph(String name, double value){
        dataTables.get(name).add(countResponse, value);
    }

    public void addFunctionToGraph(String name){
        namesGraphics.add(name);
    }

    public void removeFunctionFromGraph(String name){
        namesGraphics.remove(name);
    }


    public TerminalModel() {

        modeFaceMap = new HashMap<CebExchangeMode, IModeFace>();
        currentExchangeMode = getCurrentExchangeModeForInit();
        isEnableMode = false;
    }



    public void createLinkDataFromRequest(){
        new CreateLinkDataFromRequestAction().start();
    }

    public  void createHexToImitRequest(){
        new CreateHexToImitRequestAction().start();
        return;
    }


    public void setCurrentExchangeMode(CebExchangeMode currentExchangeMode) {
        //if(!isEnableMode) {
            this.currentExchangeMode = currentExchangeMode;
            terminalModelEventListener.updateEnableButton();
        //}
    }

    public CebExchangeMode getCurrentExchangeMode() {
        return currentExchangeMode;
    }

    public void setTerminalModelEventListener(TerminalModelEventListener listener){
        terminalModelEventListener = listener;
    }

    private CebExchangeMode getCurrentExchangeModeForInit(){
        String exchangeModeString = WorkstationConfig.getProperty(ConfProp.CURRENT_EXCHANGE_MODE);
        if(exchangeModeString != null) {
            CebExchangeMode mode = CebExchangeMode.valueOf(exchangeModeString);
            if (mode != null) {
                return mode;
            }
        }
        return CebExchangeMode.MAIN_MODE;

    }

    public void setGraphListener(IEnableGraphListener graphListener) {
        this.graphListener = graphListener;
    }

    public void saveProperties(){
        WorkstationConfig.setProperty(ConfProp.CURRENT_EXCHANGE_MODE, currentExchangeMode.name());
    }


    private class CreateHexToImitRequestAction extends Thread {

        @Override
        public void run() {

            byte[] request = getRequestCurrentMode();
            byte[] cebPacket = PacketHelper.createCebPacket(request);
            byte[] packetToBox = PacketHelper.createBoxPacketToCeb(cebPacket);

            ArrayList<String> hexCodes = new ArrayList<String>();
            System.out.println("Create file ceb_box_tb_ram.hex from this request:");


            String mess = "";
            for(int i =0; i <packetToBox.length; i++){
                String hex =  Integer.toHexString((packetToBox[i] & 0xFF)| 0x100).substring(1);
                hexCodes.add(hex);
                mess += (hex + " ");
            }

            System.out.println(mess);

            for(int i = 0; i <= 50; i++){
                hexCodes.add("00");
            }

            FileHelper.createOutputFile(ConfProp.FILE_PATH_HEX_CODES, "ceb_box_tb_ram.hex", hexCodes, ".hex");


        }


    }


    private class CreateLinkDataFromRequestAction extends Thread {

        @Override
        public void run() {

            byte[] request = getRequestCurrentMode();
            ArrayList<String> hexCodes = new ArrayList<String>();
            System.out.println("Modify ceb_link_ao_ram.hex for this request:");
            String mess = "";
            for(int i =0; i <request.length; i = i+2){
                String hex =  Integer.toHexString((request[i+1] & 0xFF)| 0x100).substring(1) +  Integer.toHexString((request[i] & 0xFF)| 0x100).substring(1)  ;
                hexCodes.add(hex);
                mess += (hex + " ");
            }

            ArrayList<String> text = new ArrayList<String>();



            for(int i = 0; i<= 31; i++){
                text.add ( "00");
            }
            int k = 0;
            while(k < hexCodes.size()){
                text.add (hexCodes.get(k));
                k ++;
            }

            while(k < 31){
                text.add ("00");
                k ++;
            }
            FileHelper.createOutputFile(ConfProp.FILE_PATH_HEX_CODES, "ceb_link_ao_ram.hex", text, ".hex");
            System.out.println(mess);
            //File pathCurrentFile = new File(WorkstationConfig.getProperty(ConfProp.FILE_PATH_CURRENT_FILE));
           // String  targetPath = pathCurrentFile.getParent() + File.separator + "ceb_link_ao_ram.txt";

            //FileHelper.createFile(targetPath, text);


        }


    }

    public Boolean getEnableMode() {
        return isEnableMode;
    }

    public void setEnableMode(Boolean enableMode) {
        isEnableMode = enableMode;
    }

    public void addFace(CebExchangeMode mode, IModeFace face){
        modeFaceMap.put(mode, face);
    }

    public void refreshCurrentFace(){
        countResponse++;
        modeFaceMap.get(currentExchangeMode).refreshDataOnFace();
    }


    public byte[] getRequestCurrentMode(){
        return modeFaceMap.get(currentExchangeMode).createRequest();
    }

    public Boolean getRepeatingCebExchange() {
        return isRepeatingCebExchange;
    }

    public void setRepeatingCebExchange(Boolean repeatingCebExchange) {
        isRepeatingCebExchange = repeatingCebExchange;
    }

    public Boolean getRunCurrentMode() {
        return runCurrentMode;
    }

    public void setRunCurrentMode(Boolean runCurrentMode) {
        this.runCurrentMode = runCurrentMode;
    }

    public Boolean getGraphing() {

        return isGraphing;
    }

    public void setGraphing(Boolean graphing) {
        if(!graphing){
            graphListener.initButtonGraph();
        }
        isGraphing = graphing;
    }
}
