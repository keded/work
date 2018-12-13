package pro.filatov.workstation4ceb.model.uart.rom;

import java.util.List;

/**
 * Created by yuri.filatov on 08.08.2016.
 */
public class PacketToRom {


    private static int NEXT_GEN;
    private final Integer id = 0x3000;


    private Integer mode;
    private Integer actionRun;
    private Integer numWords;
    private Integer startAddressL;
    private Integer startAddressH;
    private CurrentBlock currentBlock;
    List<Integer> data;


    public PacketToRom(Integer actionRun, Integer mode,  Integer numWords, List<Integer> data, Integer address, CurrentBlock currentBlock) {
        this.actionRun = actionRun;
        this.mode = mode;
        this.numWords = numWords;
        this.data = data;
        this.startAddressL = address & 0xFFFF;
        this.startAddressH = (address >> 16 | currentBlock.getMask());
    }


    public Integer getId() {
        return id;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getActionRun() {
        return actionRun;
    }

    public void setActionRun(Integer actionRun) {
        this.actionRun = actionRun;
    }

    public Integer getNumWords() {
        return numWords;
    }

    public void setNumWords(Integer numWords) {
        this.numWords = numWords;
    }

    public Integer getStartAddressL() {
        return startAddressL;
    }

    public void setStartAddressL(Integer startAddressL) {
        this.startAddressL = startAddressL;
    }

    public Integer getStartAddressH() {
        return startAddressH;
    }

    public void setStartAddressH(Integer startAddressH) {
        this.startAddressH = startAddressH;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}
