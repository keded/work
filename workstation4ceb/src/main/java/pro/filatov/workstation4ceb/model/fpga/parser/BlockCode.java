package pro.filatov.workstation4ceb.model.fpga.parser;


/**
 * Created by yuri.filatov on 21.06.2016.
 */
public enum BlockCode {
    KPU     ("000", 0b10000000),
    BASE    ("110",0b10000000),
    OTHERS  ("1000",0b1000000),
    BLOCK   ("0011", 0b1000000),
    ROM     ("0100",0b1000000),
    TRADE   ("0101", 0b1000000),
    ADC     ("0110",0b1000000),
    LINK    ("0111",0b1000000),
    BUP     ("100", 0b10000000),
    USTP    ("10011",0b100000),
    KEYS    ("10100",0b100000),
    UTIL ("10101",0b100000),
    ROM_CONTROL ("101100",0b10000),
    WATCHER("1011011", 0b1000),
    STORAGE("1011100", 0b1000),
    LINK_CONTROL ("1011010",0b1000),
    BUP1    ("1", 0b10000000),
    ADC_CONTROL    ("1111", 0b1000000)
    ;

    private String opCode;

    private int mask;

    BlockCode(String opCode, int mask ){
        this.opCode = opCode;
        this.mask = mask;
    }


    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public static BlockCode getName(String command){
        for(BlockCode item  : BlockCode.values()){
            if(item.toString().equals(command)){
                return item;
            }
        }
        return null;
    }

    public int getMask() {
        return mask;
    }


}
