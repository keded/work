package pro.filatov.workstation4ceb.model.uart;

import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.editor.IMemoryEventListener;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.editor.FileHelper;
import pro.filatov.workstation4ceb.model.fpga.parser.ModeAttrFile;
import pro.filatov.workstation4ceb.model.uart.rom.CurrentBlock;
import pro.filatov.workstation4ceb.model.uart.rom.PacketToRom;
import pro.filatov.workstation4ceb.model.uart.rom.PacketToRomHelper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by yuri.filatov on 11.08.2016.
 */
public class MemoryModel {


    private IMemoryEventListener memoryEventListener;

    private List<Integer> values;

    private Integer address;
    private CurrentBlock curBlock;
    private String pathReadWriteFiles;



    private Integer numBinds;
    private Integer wordsInBind;
    private String nameTextFileFromStorage;
    private String nameFileTxtValues;
    private Integer countTxtValues;
    private Integer addressForTxtValues;


    public void writeSelectedFilesToFlash() {
        Model.getUartModel().setProgramming(true);
        new WriteSelectedFilesToFlashAction().start();
        return;
    }

    public void writeValuesFromTxtFile(){
        Model.getUartModel().setProgramming(true);
        new WriteTxtValuesAction().start();
        return;
    }


    public void configurationFlashAction(){
        new ConfigurationFlashAction().start();
        return;
    }

    public void resetProgramInit(){
        Model.getUartModel().setProgramming(true);
        curBlock = CurrentBlock.TRADE;
        new ResetProgramInitAction().start();
    }

    public void imitRequestAO(){
        new ImitRequestAOAction().start();
    }

    private class ResetProgramInitAction extends  Thread {


        @Override
        public void run() {
            try {

            ResetCebAction resetCebAction = new ResetCebAction();
            resetCebAction.start();
            resetCebAction.join(2000);
            if (resetCebAction.isAlive()) {
                resetCebAction.interrupt();
                resetCebAction.join();
                System.out.println("Error for sending reset signal! ");
                return;
            }
            sleep(500);

            WriteSelectedFilesToFlashAction writingAction = new WriteSelectedFilesToFlashAction();
            writingAction.start();
            writingAction.join();
            if (writingAction.isAlive()) {
                writingAction.interrupt();
                writingAction.join();
                System.out.println("Error for program to CEB! ");
                return;
            }
            sleep(100);

            InitBlockAction initBlockAction = new InitBlockAction();
            initBlockAction.start();
            initBlockAction.join(1000);
            if (initBlockAction.isAlive()) {
                initBlockAction.interrupt();
                initBlockAction.join();
                System.out.println("Error for sending Init signal! ");
                return;
            }

            sleep(100);

                Model.getUartModel().setProgramming(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

        public int resetInitOTKMode(Thread thread) throws InterruptedException {
             setCurBlock(CurrentBlock.TRADE);
                ResetCebAction resetCebAction = new ResetCebAction();
                resetCebAction.start();
                resetCebAction.join(2000);
                if (resetCebAction.isAlive()) {
                    resetCebAction.interrupt();
                    resetCebAction.join();
                    System.out.println("Error for sending reset signal! ");
                    return -1;
                }
            thread.sleep(500);
                address = 20480;
                InitBlockAction initBlockAction = new InitBlockAction();
                initBlockAction.start();
                initBlockAction.join(1000);
                if (initBlockAction.isAlive()) {
                    initBlockAction.interrupt();
                    initBlockAction.join();
                    System.out.println("Error for sending Init signal! ");
                    return -1;
                }

            thread.sleep(100);

                Model.getUartModel().setProgramming(false);
                return 0;

        }

    private class WriteSelectedFilesToFlashAction extends Thread {

        final int WORD_COUNT_ROM = 16;

        private List<PacketToRom> getPacketsToROM(Map<ModeAttrFile, List<String>> hexData) {
            List<PacketToRom> res = new ArrayList<>();
            for (Map.Entry<ModeAttrFile, List<String>> block : hexData.entrySet()) {
                List<PacketToRom> packetsForBlock = createPacketsForBlock(block.getKey(), block.getValue());
                res.addAll(packetsForBlock);
            }
            return res;
        }

        private List<PacketToRom> createPacketsForBlock(ModeAttrFile attr, List<String> hexCodes) {
            if(attr.getNumBits() <= 16){
               return  PacketToRomHelper.createPacketsForNormValues(attr.getAddress(),hexCodes);
            }else {
                List<String> halfHexCodes = hexCodes.subList(0, hexCodes.size()/2);
                return createPacketForBigValues(attr,halfHexCodes);
            }


        }

        private List<PacketToRom> createPacketForBigValues(ModeAttrFile attr, List<String> hexCodes){
            List<PacketToRom> res = new ArrayList<>();
            try {
                int i = 2;
                int k = 2;
                int iteration = 0;
                Double d =  Math.pow(2, 16);
                BigInteger mask16 = BigDecimal.valueOf(d-1).toBigInteger();
                int address = attr.getAddress();
                int ref = (128-address%128)%16;
                List<Integer> lowValuesList = new ArrayList<>();
                List<Integer> highValuesList = new ArrayList<>();
                for (String hex : hexCodes) {
                    //lowValuesList.
                    BigInteger number = new BigInteger(hex, 16);
                    Integer intValueLow = number.and(mask16).intValue();
                    Integer intValueHigh = number.shiftRight(16).intValue();
                    lowValuesList.add(intValueLow);
                    highValuesList.add(intValueHigh);
                    if (k == WORD_COUNT_ROM  | (iteration == 0  &&  k == ref)) {
                        List<Integer> dataCurPacket = new ArrayList<>();
                        dataCurPacket.addAll(lowValuesList);
                        dataCurPacket.addAll(highValuesList);
                        res.add(PacketToRomHelper.createPacketForWriteToFlash(i  + attr.getAddress() - ((iteration == 0  &&  k == ref)? ref: WORD_COUNT_ROM), dataCurPacket));
                        lowValuesList.clear();
                        highValuesList.clear();
                        k = 0;
                        iteration++;
                    }
                    i = i + 2;
                    k = k + 2;
                }
                if (lowValuesList.size() != 0) {
                    List<Integer> dataCurPacket =  new ArrayList<>();
                    dataCurPacket.addAll(lowValuesList);
                    dataCurPacket.addAll(highValuesList);
                    res.add(PacketToRomHelper.createPacketForWriteToFlash(i - 1 - dataCurPacket.size() + attr.getAddress(), dataCurPacket));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return res;

        }

        @Override
        public void run() {
            List<File> selectedFiles = Model.getTreeViewModel().getSelectedFiles();
            System.out.println("\n\nStarting parsing selected files:");
            System.out.println("*****************************");

            for (int i = 0; i < selectedFiles.size(); i++) {
                File file = selectedFiles.get(i);
                if(Model.getEditorModel().getFileAttr(file).isProgram()) {
                    System.out.println(file.getName());
                }else{
                    System.out.println("File "+ file.getName()+ "is not programming!");
                    selectedFiles.remove(file);
                    i--;
                }
            }
            System.out.println("*****************************");
            Thread parsing = Model.getParserModel().parseSelectedFiles(selectedFiles);
            Map<ModeAttrFile, List<String>> hexData = null;
            try {
                parsing.join();
                hexData = Model.getParserModel().getHexData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (hexData != null) {
                List<PacketToRom> listForSending = getPacketsToROM(hexData);
                System.out.println("Created " + listForSending.size() + " packets for writing to flash");
                for (PacketToRom curPacket : listForSending) {
                    try {
                        byte[] romData = PacketToRomHelper.getDataFromPacketToROM(curPacket);
                        byte[] cebData = PacketHelper.createCebPacket(romData);
                        byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);

                        UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                        t.join(3000);
                        if (t.isAlive()) {
                            t.interrupt();
                            t.join();
                            System.out.println("Error for sending packet. This packet will be ignored! ");
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                } else {
                System.out.println("Emptry data for write to flash");
            }

            Model.getUartModel().setProgramming(false);
        }


    }

    private class WriteTxtValuesAction extends Thread {

        @Override
        public void run() {

            List<Integer>  values = FileHelper.getValuesFromFile(nameFileTxtValues, countTxtValues, pathReadWriteFiles );
            System.out.println("Is readed " + values.size() +" values");
            List<String> hexValues = PacketHelper.createHex(values, 0x10000);
            if (values.size() != 0) {
                List<PacketToRom> listForSending = PacketToRomHelper.createPacketsForNormValues(addressForTxtValues,hexValues);
                System.out.println("Created " + listForSending.size() + " packets for writing to flash");
                for (PacketToRom curPacket : listForSending) {
                    try {
                        byte[] romData = PacketToRomHelper.getDataFromPacketToROM(curPacket);
                        byte[] cebData = PacketHelper.createCebPacket(romData);
                        byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);

                        UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                        t.join(3000);
                        if (t.isAlive()) {
                            t.interrupt();
                            t.join();
                            System.out.println("Error for sending packet. This packet will be ignored! ");
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Emptry data for write to flash");
            }

            Model.getUartModel().setProgramming(false);
        }
    }



    public Thread readValues() {
        Model.getUartModel().setProgramming(true);
        ReadValuesAction action = new ReadValuesAction();
        action.start();
        return action;

    }

    public void writeValues() {
        Model.getUartModel().setProgramming(true);
        new WriteValuesAction().start();
    }



    public void startInitBlock() {
        Model.getUartModel().setProgramming(true);
        new InitBlockAction().start();
    }



    public void resetCeb() {
        new ResetCebAction().start();

    }


    public void readStorage(){
        new ReadFromStorageAction().start();
    }

    private class InitBlockAction extends Thread {
        @Override
        public void run() {

            PacketToRom packetToRom = PacketToRomHelper.createPacketForStartInit( address, curBlock, 1024 );
            byte[] romData = PacketToRomHelper.getDataFromPacketToROM(packetToRom);
            byte[] cebData = PacketHelper.createCebPacket(romData);
            byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);
            try {
                UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                t.join(3000);
                if (t.isAlive()) {
                    while (!t.isInterrupted()){
                        t.interrupt();
                    }
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    Model.getUartModel().setProgramming(false);
                    return;
                }
                Model.getUartModel().setProgramming(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }


    }



    private class ResetCebAction extends Thread {
        @Override
        public void run() {
            byte []mode = new byte [2];
            mode[1] = (byte)0xF0;
            byte[] cebData = PacketHelper.createCebPacket(mode);
            byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);
            try {
                UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                t.join(3000);
                if (t.isAlive()) {
                    while (!t.isInterrupted()){
                        t.interrupt();
                    }
                    System.out.println("Error for sending packet. This packet will be ignored! ");;
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private class WriteValuesAction extends Thread {
        @Override
        public void run() {

            PacketToRom packet = PacketToRomHelper.createPacketForWriteToFlash(address, values);

            byte[] romData = PacketToRomHelper.getDataFromPacketToROM(packet);
            byte[] cebData = PacketHelper.createCebPacket(romData);
            byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);

            try {
                UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                t.join(3000);
                if (t.isAlive()) {
                    t.interrupt();
                    t.join();
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    return;
                }
                sleep(100);
                ReadValuesAction readValuesAction = new ReadValuesAction();
                readValuesAction.start();
                readValuesAction.join(1000);
                if (readValuesAction.isAlive()) {
                    readValuesAction.interrupt();
                    readValuesAction.join();
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            Model.getUartModel().setProgramming(false);
        }
    }



    private class ConfigurationFlashAction extends Thread {
        @Override
        public void run() {

             PacketToRom packetToRom = PacketToRomHelper.createPacketForConfigurationFlash();

            byte[] romData = PacketToRomHelper.getDataFromPacketToROM(packetToRom );
            byte[] cebData = PacketHelper.createCebPacket(romData);
            byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);

            try {
                UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                t.join(3000);
                if (t.isAlive()) {
                    t.interrupt();
                    t.join();
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }


    private class ImitRequestAOAction extends Thread {
        @Override
        public void run() {

            byte []packet = {0x00, 0x00,0x00, 0x00};
            byte[] cebData = PacketHelper.createCebPacket(packet);
            byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);

            try {
                UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                t.join(3000);
                if (t.isAlive()) {
                    t.interrupt();
                    t.join();
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private class ReadFromStorageAction extends  Thread {

        Integer prevAddress;
        Set<Integer> requestMap;
        Set <Integer> notReceivedPacket;
        Map <Integer, List<Integer>> successReceivedPackets;


        @Override
        public void run() {
            int size = wordsInBind * numBinds;
            requestMap = calcRequestMap(address,size);
            System.out.println("Created "+ requestMap.size() + "requests for CEB");
            notReceivedPacket = new HashSet<>();
            notReceivedPacket.addAll(requestMap);

            int num_iteration_exchange = 5;
            int iteration_exchange = 1;

            successReceivedPackets = new HashMap<>();

            while (iteration_exchange  <= num_iteration_exchange ){

                System.out.println(" Start iteration exhange #" + String.valueOf(iteration_exchange) );
                exhangeProcessing();
                String mess = (notReceivedPacket.size() == 0)? "Successful exchange #" + String.valueOf(iteration_exchange): "Lost packet in  exhange #" + String.valueOf(iteration_exchange) ;
                System.out.println(mess);
                iteration_exchange++;
                if(notReceivedPacket.size() == 0){
                    break;
                }
                requestMap.clear();
                requestMap.addAll(notReceivedPacket);
            }

            List<Integer>  listValues = getListValues(successReceivedPackets, address, size);
            System.out.println("Received " + listValues.size() + " data words");
            List<StorageBind> storageBinds = getBindsValues(listValues);
            System.out.println("Created " + storageBinds.size() + " binds");
            createTxtFile(storageBinds);
            System.out.println("Created " + nameTextFileFromStorage);
            
        }





        private void exhangeProcessing(){
            for(Integer address : requestMap){
                System.out.println("address :" + address);
                exhangeData(address);
            }

        }


        private Set<Integer> calcRequestMap(Integer address, int size){
            Set<Integer> requestMap = new HashSet<>();

            int rem = size%16;
            size =  rem > 0 ? size + 16 - rem : size ;
            int numPackets = size/16;
            int i = 0;
            while( i < numPackets){
                requestMap.add(address);
                address = address + 16;
                i++;
            }
            return requestMap;
        }

        private void createTxtFile(List<StorageBind> values){
            List<String>lines = new ArrayList<>();
            for(StorageBind value : values){
                lines.add(value.getRow());
            }
            if(pathReadWriteFiles == null || pathReadWriteFiles == "") {
                FileHelper.createFile(ConfProp.FILE_PATH_HEX_CODES, nameTextFileFromStorage, lines);
            } else{
                 Path outFile = Paths.get(pathReadWriteFiles+ File.separator  +  nameTextFileFromStorage);
                try {
                    Files.write(outFile, lines, Charset.forName("UTF-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }




        private List<Integer> exhangeData(Integer address){
            byte [] storageData= PacketHelper.i2b(0x4000);
            storageData = PacketHelper.addDataToPacket(storageData, PacketHelper.i2b(address));
            byte[] cebData = PacketHelper.createCebPacket(storageData);
            byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);

            try {
                UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                t.join(3000);
                if (t.isAlive()) {
                    t.interrupt();
                    t.join();
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    return null;
                }
                byte[] response = Model.getUartModel().getResponse();
                processResponce(response);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }


            return null;

        }


        private List<Integer>getListValues (Map<Integer, List<Integer>> successReceivedData, Integer address, int size){
            List<Integer> res = new ArrayList<>();
            int startKey = address;
            while(startKey < address + size){
                res.addAll(successReceivedData.get(startKey));
                startKey = startKey + 16;
            }
            return  res;
        }

        private List<StorageBind> getBindsValues(List<Integer>arg ){
            List<StorageBind> res = new ArrayList<>();
            int w = 0;
            int b = 0;
            int i = 0;
            while (b < numBinds) {
                StorageBind bind = new StorageBind();
                while (w < wordsInBind) {
                    bind.addValue(arg.get(i));
                    i++;
                    w++;
                }
                res.add(bind);
                w = 0;
                b++;
            }
            return res;
        }
        private void processResponce(byte[] response){
            if(response.length > 40) {
                byte[] cebResponse = PacketHelper.extractCebPacket(response);
                List<Integer> resp = PacketHelper.getIntList(cebResponse);

                List<Integer> values = new ArrayList<>();
                Integer address;
                if (resp.get(1) == 0x4000) {
                    if (requestMap.contains(address = resp.get(2))) {
                        resp.remove(0);
                        resp.remove(0);
                        resp.remove(0);
                        values.addAll(resp);
                        successReceivedPackets.put(address, values);
                        notReceivedPacket.remove(address);
                    }
                }
            }
        }





    }





    private class ReadValuesAction extends Thread {
        @Override
        public void run() {

            PacketToRom packetToRom = PacketToRomHelper.createPacketForReadFromFlash(address, 16);
            byte[] romData = PacketToRomHelper.getDataFromPacketToROM(packetToRom);
            byte[] cebData = PacketHelper.createCebPacket(romData);
            byte[] boxData = PacketHelper.createBoxPacketToCeb(cebData);

            try {
                UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(boxData);
                t.join(1000);
                if (t.isAlive()) {

                    while (!t.isInterrupted()){
                        t.interrupt();
                    }
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    Model.getUartModel().setProgramming(false);
                    return;
                }
                sleep(200);
                byte[] ack = {0x77, 0x77};
                byte[] req = PacketHelper.createBoxPacketToCeb(PacketHelper.createCebPacket(ack));
                UartModel.EchangePacketAction t2 = Model.getUartModel().doExchangePacket(req);
                t2.join(1000);
                if (t2.isAlive()) {
                    while (!t2.isInterrupted()){
                        t2.interrupt();
                    }
                    System.out.println("Error for sending packet. This packet will be ignored! ");
                    Model.getUartModel().setProgramming(false);
                    return;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            Model.getUartModel().setProgramming(false);

            byte[] response = Model.getUartModel().getResponse();
            if(response != null && response.length != 0) {
                byte[] romResponse = PacketHelper.getDataFromPacket(response);
                byte[] data = new byte[32];
                System.arraycopy(romResponse, 8, data, 0, 32);
                List<Integer> res = PacketHelper.getIntList(data);
                values = res;
                memoryEventListener.updateValues();
                Model.getUartModel().setProgramming(false);
            }

        }


    }

    public void setMemoryEventListener(IMemoryEventListener memoryEventListener) {
        this.memoryEventListener = memoryEventListener;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
        //memoryEventListener.updateValues();
    }


    private void doReadValues() {


    }

    public CurrentBlock getCurBlock() {
        return curBlock;
    }

    public void setCurBlock(CurrentBlock curBlock) {
        this.curBlock = curBlock;
    }

    public Integer getAddress() {
        return address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public void setNumBinds(Integer numBinds) {
        this.numBinds = numBinds;
    }

    public void setWordsInBind(Integer wordsInBind) {
        this.wordsInBind = wordsInBind;
    }

    public void setNameTextFileFromStorage(String nameTextFileFromStorage) {
        this.nameTextFileFromStorage = nameTextFileFromStorage;
    }

    public void setNameFileTxtValues(String nameFileTxtValues) {
        this.nameFileTxtValues = nameFileTxtValues;
    }

    public void setCountTxtValues(Integer countTxtValues) {
        this.countTxtValues = countTxtValues;
    }

    public void setAddressForTxtValues(Integer addressForTxtValues) {
        this.addressForTxtValues = addressForTxtValues;
    }

    public String getPathReadWriteFiles() {
        return pathReadWriteFiles;
    }

    public void setPathReadWriteFiles(String pathReadWriteFiles) {
        this.pathReadWriteFiles = pathReadWriteFiles;
    }
}
