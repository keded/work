package pro.filatov.workstation4ceb.model.fpga.parser;

/**
 * Created by Администратор on 20.06.2016.
 */
public enum TradeCommand {

    NOP     ("0000000000", CommandGroup.OTHER),
    ADD     ("0000000001", CommandGroup.TRANSFORM),
    SUB     ("0000000010", CommandGroup.TRANSFORM),
    MULT    ("0000000011", CommandGroup.TRANSFORM),

    SETB    ("000000010", CommandGroup.TRANSFORM),
    CLRB    ("000000011", CommandGroup.TRANSFORM),
    MOVE    ("0000001000", CommandGroup.TRANSFORM),
    ORL     ("0000001101", CommandGroup.TRANSFORM),
    ANL     ("0000001110", CommandGroup.TRANSFORM),
    CFIX    ("0000001111000", CommandGroup.OTHER),
    INC     ("0000001111001", CommandGroup.TRANSFORM),
    DEC     ("0000001111010", CommandGroup.TRANSFORM),
    STP_DEBUG    ("0000001111011000", CommandGroup.OTHER),
    ABS     ("0000001111100", CommandGroup.TRANSFORM),
    RJUMP   ("0000001111101", CommandGroup.TRANSFORM),
    STOREPC ("0000001111110", CommandGroup.TRANSFORM),
    CLR     ("0000001111111", CommandGroup.TRANSFORM),
    JUMP    ("000001", CommandGroup.TRANSFORM),
    LSL     ("000010000", CommandGroup.TRANSFORM),
    RSR     ("000010001", CommandGroup.TRANSFORM),
    ASR     ("000010001", CommandGroup.TRANSFORM),
    EQUAL0  ("00010", CommandGroup.BRANCH),
    MORE0   ("00011", CommandGroup.BRANCH),
    LESS0   ("00100", CommandGroup.BRANCH),
    DJNZ    ("00101", CommandGroup.BRANCH),
    CONST   ("0011", CommandGroup.TRANSFORM),
    EQUAL   ("0101", CommandGroup.BRANCH),
    MORE    ("0110", CommandGroup.BRANCH),
    ISBIT   ("0111", CommandGroup.BRANCH),
    STORE   ("100",CommandGroup.MEMORY),
    CSTORE  ("101", CommandGroup.MEMORY),
    LOAD    ("110",CommandGroup.MEMORY),
    CLOAD   ("111",CommandGroup.MEMORY),
    RSTORE  ("0100000", CommandGroup.MEMORY),
    ;

    private String opCode;
    private CommandGroup group;


    TradeCommand(String opCode, CommandGroup group){
        this.opCode = opCode;
        this.group = group;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public static TradeCommand getCommand(String command){
        for(TradeCommand item  : TradeCommand.values()){
            if(item.toString().equals(command)){
                return item;
            }
        }
        return null;
    }

    public CommandGroup getGroup() {
        return group;
    }
}
