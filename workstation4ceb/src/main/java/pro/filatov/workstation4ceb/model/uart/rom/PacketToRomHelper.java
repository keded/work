package pro.filatov.workstation4ceb.model.uart.rom;

import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.model.uart.PacketHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuri.filatov on 08.08.2016.
 */
public class PacketToRomHelper{



    final static int WORD_COUNT_ROM = 16;
    private static Integer actionRun;

    private static Integer INIT_MODE = 0xAAAA;
    private static Integer READ_MODE = 0xBBBB;
    private static Integer WRITE_MODE = 0xCCCC;
    private static Integer READCOM_MODE = 0xDDDD ;
    private static Integer CONF_MODE = 0xFFFF;

    public static PacketToRom createPacketForStartInit(Integer address, CurrentBlock currentBlock, Integer numCommand){
        return new PacketToRom(getNextActionRun(),INIT_MODE, numCommand, null,  address, currentBlock);
    }

    public static PacketToRom createPacketForReadFromFlash(Integer address, Integer numWords){
        return new PacketToRom(getNextActionRun(),READ_MODE,   numWords,null, address, CurrentBlock.ANY);
    }
    public static PacketToRom createPacketForWriteToFlash(Integer address, List<Integer> data ){
        return new PacketToRom(getNextActionRun(),WRITE_MODE,  data.size(), data, address, CurrentBlock.ANY);
    }
    public static PacketToRom createPacketForReadingCommandFromFlash(Integer address, Integer numWords, CurrentBlock currentBlock){
        return new PacketToRom(getNextActionRun(),READCOM_MODE, numWords, null, address, currentBlock);
    }

    public static PacketToRom createPacketForConfigurationFlash(){
        return new PacketToRom(getNextActionRun(),CONF_MODE, 0, null, 0, CurrentBlock.ANY);
    }

    public static byte[] getDataFromPacketToROM(PacketToRom packet){
        byte[]res = new byte[0];
        res = PacketHelper.addDataToPacket(res, packet.getId());
        res = PacketHelper.addDataToPacket(res, packet.getActionRun());
        res = PacketHelper.addDataToPacket(res, packet.getMode());
        res = PacketHelper.addDataToPacket(res, packet.getNumWords());
        res = PacketHelper.addDataToPacket(res, packet.getStartAddressL());
        res = PacketHelper.addDataToPacket(res, packet.getStartAddressH());
        if(packet.getData() != null) {
            res = PacketHelper.addDataToPacket(res, packet.getData());
        }
        return res;
    }


    private static Integer getNextActionRun(){
        actionRun = getActionRun() + 10;
        return actionRun;
    }

    public static Integer getActionRun() {
        if(actionRun== null){
            String actionRunStr = WorkstationConfig.getProperty(ConfProp.ACTION_RUN_ROM);
            if(actionRunStr != null) {
                actionRun = Integer.parseInt(actionRunStr);
            }else {
                actionRun = 0;
            }
        }
        return actionRun;
    }



    public static  List<PacketToRom> createPacketsForNormValues(Integer address, List<String> hexCodes) {
        List<PacketToRom> res = new ArrayList<>();
        try {
            int i = 1;
            int k = 1;
            int iteration = 0;

            int ref = (128-address%128)%16;
            List<Integer> dataSinglePacket = new ArrayList<>();
            for (String hex : hexCodes) {
                dataSinglePacket.add(Integer.parseInt(hex, 16));
                if (k == WORD_COUNT_ROM | (iteration == 0  &&  k == ref)) {
                    List<Integer> dataCurPacket = new ArrayList<>();
                    dataCurPacket.addAll(dataSinglePacket);
                    res.add(PacketToRomHelper.createPacketForWriteToFlash(i  + address - ((iteration == 0  &&  k == ref)? ref: WORD_COUNT_ROM), dataCurPacket));
                    dataSinglePacket.clear();
                    k = 0;
                    iteration++;
                }
                i++;
                k++;
            }
            if (dataSinglePacket.size() != 0) {
                res.add(PacketToRomHelper.createPacketForWriteToFlash(i - 1 - dataSinglePacket.size() + address, dataSinglePacket));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return res;


    }

    public static void resetCounters(){
        actionRun  = 0;
    }

    public static void saveProperties(){
        WorkstationConfig.setProperty(ConfProp.ACTION_RUN_ROM, getActionRun().toString());
    }

}